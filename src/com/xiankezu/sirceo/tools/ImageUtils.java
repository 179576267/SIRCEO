package com.xiankezu.sirceo.tools;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

/**
 * Author: wzf Date: 14-10-11 Time: 下午4:51 Description: Version: 3.0
 */
public class ImageUtils {
	public static final String UTF_8 = "UTF-8";
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	public static LruCache<String, Bitmap> cache;// 缓存图片用

	/**
	 * Bitmap → byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] BitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		Log.i("789", "输出长度：" + baos.toByteArray().length);
		return baos.toByteArray();
	}

	/**
	 * Bitmap2File
	 * 
	 * @param bitName
	 * @param bitmap
	 * @return
	 * @throws java.io.IOException
	 */
	public static File bitmapToFile(String bitName, Bitmap bitmap)
			throws IOException {
		File f = new File("mnt/sdcard/" + bitName + ".jpg");
		f.createNewFile();
		FileOutputStream fOut = null;
		fOut = new FileOutputStream(f);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();
		return f;
	}

	/**
	 * 压缩图片
	 * 
	 * @param image
	 * @return
	 */
	private static byte[] compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			Log.i("789", "长度：" + baos.toByteArray().length);
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		Log.i("789", "最终长度：" + baos.toByteArray().length);
		// ByteArrayInputStream isBm = new
		// ByteArrayInputStream(baos.toByteArray());//
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
		// 把ByteArrayInputStream数据生成图片
		return baos.toByteArray();
	}

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[1024];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	/**
	 * @param x
	 *            图像的宽度
	 * @param y
	 *            图像的高度
	 * @param image
	 *            源图片
	 * @param outerRadiusRat
	 *            圆角的大小
	 * @return 圆角图片
	 */
	public static Bitmap createFramedPhoto(int x, int y, Bitmap image,
			float outerRadiusRat) {
		// 根据源文件新建一个darwable对象
		Drawable imageDrawable = new BitmapDrawable(image);

		// 新建一个新的输出图片
		Bitmap output = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		// 新建一个矩形
		RectF outerRect = new RectF(0, 0, x, y);

		// 产生一个红色的圆角矩形
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);
		canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

		// 将源图片绘制到这个圆角矩形上
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		imageDrawable.setBounds(0, 0, x, y);
		canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
		imageDrawable.draw(canvas);
		canvas.restore();
		return output;
	}

	//
	// ImageView imageView = (ImageView) findViewById(R.id.imageView1);
	// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
	// R.drawable.ii);
	// 　　　　//可以设置bitmap.getWidth()/2做半径
	// imageView.setImageBitmap(toRoundCorner(bitmap,80.f));

	/**
	 * 生成二维码Bitmap
	 * 
	 * @param str
	 * @return
	 * @author shaowei.ma
	 * @date 2014年11月19日
	 */
	public static Bitmap createQrCodeBitmap(String str) {
		if (TextUtils.isEmpty(str))
			return null;
		try {
			BitMatrix matrix = new MultiFormatWriter().encode(str,
					BarcodeFormat.QR_CODE, 300, 300);
			int width = matrix.width;
			int height = matrix.height;
			int[] pixels = new int[width * height];
			for (int y = 0; y < width; ++y) {
				for (int x = 0; x < height; ++x) {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000; // black pixel
					} else {
						pixels[y * width + x] = 0xffffffff; // white pixel
					}
				}
			}
			Bitmap bmp = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bmp.setPixels(pixels, 0, width, 0, 0, width, height);
			return bmp;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取bitmap
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = context.openFileInput(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 图片按比例大小压缩方法（根据路径获取图片并压缩）：
	 * 
	 * @param srcPath
	 * @return
	 */
	public static byte[] getCompressImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws Exception {
		// System.out.println("image_url==> "+url);
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new URL(url).openStream(), 2 * 1024);
			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, 2 * 1024);
			copy(in, out);
			out.flush();
			byte[] data = dataStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			data = null;
			return bitmap;
		} catch (IOException e) {
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获得oom大小
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getOOMSize(Context ctx) {
		ActivityManager manager = (ActivityManager) ctx
				.getSystemService(ctx.ACTIVITY_SERVICE);
		return 1024 * 1024 * manager.getMemoryClass();
	}

	/**
	 * 获得图片的缩略图
	 * 
	 * @param w_w
	 * @param w_h
	 * @return bitmap
	 */
	public static Bitmap getThumbnail(byte[] src, int w_w, int w_h) {
		int p_w;
		int p_h;
		BitmapFactory.Options options = new BitmapFactory.Options();
		int size = 1;
		options.inJustDecodeBounds = true;// 只解码边框,返回为null
		// 返回null 当inJustDecodeBounds = true时,但是options 会被设值,进而获得图片的宽高属性
		BitmapFactory.decodeByteArray(src, 0, src.length, options);
		p_w = options.outWidth;
		p_h = options.outHeight;

		while (p_w / size > w_w || p_h / size > w_h) {
			size++;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = size;
		Bitmap bitmap = BitmapFactory.decodeByteArray(src, 0, src.length,
				options);
		return bitmap;
	}

	/**
	 * 获得图片的缩略图
	 * 
	 * @param url
	 * @param w_w
	 * @param w_h
	 * @return bitmap
	 */
	public static Bitmap getThumbnail(String url, int w_w, int w_h) {
		int p_w;
		int p_h;
		BitmapFactory.Options options = new BitmapFactory.Options();
		int size = 1;
		options.inJustDecodeBounds = true;// 只解码边框,返回为null
		// 返回null 当inJustDecodeBounds = true时,但是options 会被设值,进而获得图片的宽高属性
		BitmapFactory.decodeFile(url, options);
		p_w = options.outWidth;
		p_h = options.outHeight;

		while (p_w / size > w_w || p_h / size > w_h) {
			size++;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = size;
		Bitmap bitmap = BitmapFactory.decodeFile(url, options);
		return bitmap;
	}

	@SuppressLint("NewApi")
	public static void initImageCache(Context ctx) {
		cache = new LruCache<String, Bitmap>(getOOMSize(ctx) / 3) {// 和版本号有关系
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
					value.getAllocationByteCount();
				} else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
					value.getByteCount();
				}

				return value.getRowBytes() * value.getHeight();// 通用
			}
		};
	}

	/**
	 * 选择相册图片并切割
	 */
	public static Intent pickAndCrop(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);// 向外输出uri
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 600);
		intent.putExtra("outputY", 300);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		return intent;
	}

	/**
	 * 写图片文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @throws IOException
	 */
	public static void saveImage(Context context, String fileName, Bitmap bitmap)
			throws IOException {
		saveImage(context, fileName, bitmap, 100);
	}

	public static void saveImage(Context context, String fileName,
			Bitmap bitmap, int quality) throws IOException {
		if (bitmap == null || fileName == null || context == null)
			return;

		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Log.d("sss", fileName);
		if (!fileName.substring(fileName.lastIndexOf(".") + 1)
				.toUpperCase(Locale.US).contains("PNG")) {
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
		} else {
			bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
		}
		byte[] bytes = stream.toByteArray();
		fos.write(bytes);
		fos.close();
	}

	/**
	 * Save to local.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @return the string
	 */
	public static String saveToLocal(Context context, Bitmap bitmap) {
		// 需要裁剪后保存为新图片
		String mFileName = System.currentTimeMillis() + ".jpg";
		String path = context.getFilesDir() + File.separator + mFileName;
		try {
			FileOutputStream fos = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return path;
	}

	/**
	 * 
	 * @param context
	 * @param bitmap
	 * @param fileDir
	 * @param name
	 * @return
	 */
	public static String saveToLocalByName(Context context, Bitmap bitmap,
			File fileDir, String name) {
		// 需要裁剪后保存为新图片
		String path = fileDir + File.separator + name;
		try {
			FileOutputStream fos = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return path;
	}

	/**
	 * 
	 * @param context
	 * @param bitmap
	 * @param name
	 *            文件名字
	 * @return
	 */
	public static String saveToLocalByName(Context context, Bitmap bitmap,
			String name) {
		// 需要裁剪后保存为新图片
		String mFileName = name + ".jpg";
		String path = context.getFilesDir() + File.separator + mFileName;
		try {
			FileOutputStream fos = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return path;
	}

	/**
	 * 流转Drawable
	 * 
	 * @param ips
	 * @return
	 */
	public static Drawable streamToDrable(InputStream ips) {
		Bitmap bitmap = BitmapFactory.decodeStream(ips);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * 圆角图片
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {
		System.out.println("图片是否变成圆角模式了+++++++++++++");
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		System.out.println("pixels+++++++" + pixels);

		return output;
	}

	/**
	 * uri2path
	 * 
	 * @param context
	 * @param contentUri
	 * @return
	 */
	public static String uriToPath(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
