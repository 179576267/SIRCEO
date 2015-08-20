package com.xiankezu.sirceo.globals;
//package com.douqu.app.globals;
//
//import java.io.File;
//import java.net.HttpURLConnection;
//
//import org.apache.http.Header;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.ab.download.AbDownloadProgressListener;
//import com.ab.download.AbFileDownloader;
//import com.ab.download.DownFile;
//import com.ab.util.AbFileUtil;
//import com.ab.util.AbLogUtil;
//import com.douqu.app.https.RestTemplate;
//import com.douqu.app.tools.ContentUtils;
//import com.douqu.app.widghts.CommonProgressDialog;
//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
///**
// * Created with IntelliJ IDEA.
// * Author: shefenfei
// * Date: 15-1-15
// * Time: 上午10:00
// * Description: 检测软件更新的管理器
// * Version: 3.0
// */
//public class UpdateManager{
//
//    private Context mContext;
//    private String mContent;
//    private Dialog mNoticeDialog;
//    private Dialog mDownLoadDialog;
//    
//    private RestTemplate restTemplate;
//    private RequestParams params;
//    //进度条对话框
//    private CommonProgressDialog dialog;
//    private static int MAX_PROGRESS ;
//    private static int Size2;    
//    private DownFile downfile;
//    private AbFileDownloader ab;
//    private String content;
//    public UpdateManager(Context context) {
//      this.mContext = context;
//      //请求更新接口
//      restTemplate = new RestTemplate(mContext);
//      params = new RequestParams();
//      //初始化进度框
//      dialog = new CommonProgressDialog(mContext);
//	  dialog.setMessage("正在下载，请稍后...");
//	  dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//	  dialog.setCancelable(false);
//    }
//
//    /**
//     * 检测更新
//     */
//    public void checkUpdateInfo(){
////    	if(compareVersionCode())
////    		showNoticeDialog();
//    	compareVersionCode();
//    }
//
//    private boolean compareVersionCode() {
//    	String version=ContentUtils.getCurrentVersionCode(mContext);
//    	params.put("version", version);
//    	params.put("client", "c");
//    	restTemplate.get(null, params, new JsonHttpResponseHandler(
//				"UTF-8") {
//    		@Override
//    		public void onSuccess(int statusCode, Header[] headers,
//    				JSONObject response) {
//    			// TODO Auto-generated method stub
//    			super.onSuccess(statusCode, headers, response);
//    			try {
//					String status=response.getString("status");
//					switch (Integer.parseInt(status)) {
//					case 0:
//						String result=response.getString("result");
//						JSONObject json=new JSONObject(result);
//						String UpdateUrl=json.getString("url");
//						boolean has_app=json.getBoolean("has_app");
//						boolean update=json.getBoolean("is_update");
//						content=json.getString("content");
//						VersionUpdate(has_app,update,UpdateUrl);
//						break;
//
//					default:
//						break;
//					}
//					
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//    		}
//    		
//    		@Override
//			public void onFailure(int statusCode, Header[] headers,
//					Throwable throwable, JSONObject errorResponse) {
//				// TODO Auto-generated method stub
//				super.onFailure(statusCode, headers, throwable, errorResponse);
//				Toast.makeText(mContext, "请求网络失败", Toast.LENGTH_SHORT).show();
//			}
//    	});
//		return false;
//	}
//    
//    private void VersionUpdate(boolean has_app ,boolean update,String UpdateUrl){
//    	//如果有新的安装包
//    	if(has_app == true){
//    		//如果是必须更新
//	    	if(update==true){	    		
//	    		showNoticeDialog2(UpdateUrl);   		
//	    	}else{
//	    		showNoticeDialog(UpdateUrl);
//	    	}
//    	}
//    }
//
//	/**
//     * 显示更新对话框
//     */
//    private void showNoticeDialog(final String UpdateUrl) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("版本更新");
//        builder.setMessage(content);
//        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//                new MyTask().execute(UpdateUrl);
//            }
//        });
//        builder.setCancelable(false);
//        mNoticeDialog = builder.create();
//        mNoticeDialog.show();
//    }
//    
//    /**
//     * 显示必须更新对话框
//     */
//    private void showNoticeDialog2(final String UpdateUrl) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("版本更新");
//        builder.setMessage("不更新可能会导致后面无法使用");
//        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//                new MyTask().execute(UpdateUrl);
//            }
//        });
//        builder.setCancelable(false);
//        mNoticeDialog = builder.create();
//        mNoticeDialog.show();
//    }
//    
//    class MyTask extends  AsyncTask<String, Integer, Integer>{
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//			dialog.show();
//		}
//		@Override
//		protected Integer doInBackground(String... params) {
//			Log.d("downfile", "params[0]====>"+params[0]);
//			downfile=new DownFile();
//			downfile.setDownUrl(params[0]);
//			downfile.setTotalLength(getContentLengthFormUrl(params[0]));
//			downfile.setName(AbFileUtil.getRealFileNameFromUrl(params[0]));	
//			ab=new AbFileDownloader(mContext, downfile, 2);
//			try {
//				ab.download(new AbDownloadProgressListener() {				
//					//size为已下载完成的长度
//					@Override
//					public void onDownloadSize(long size) {
//						// TODO Auto-generated method stub
//						MAX_PROGRESS=(int) downfile.getTotalLength();
//						Size2=(int) size;
//						if(Size2 < MAX_PROGRESS){							
//							int values=(int)((Size2 / (float)MAX_PROGRESS)*100);
//							dialog.setMax(MAX_PROGRESS);
//							dialog.setProgress(Size2);	
//							// 发布进度信息
//							//publishProgress(values);
//						}
//					}
//				});
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//						
//			return Size2;
//		}
//		
//		@Override
//		protected void onProgressUpdate(Integer... values) {
//			// TODO Auto-generated method stub
//			super.onProgressUpdate(values);
//			// 设置进度对话框的进度值
//			//dialog.setProgress(values[0]);
//		}
//		
//		//完成更新UI操作
//		@Override
//		protected void onPostExecute(Integer result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//			Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();
//			dialog.dismiss();
//			installApp(ab.getSaveFile());
//		}
//		
//	}
//    /**
//     * 跳转到系统安装界面
//     */
//    private void installApp(File appFile){
//        //创建URI 
//        Uri uri = Uri.fromFile(appFile); 
//        //创建Intent意图 
//        Intent intent = new Intent(Intent.ACTION_VIEW); 
//        //设置Uri和类型 
//        intent.setDataAndType(uri, "application/vnd.android.package-archive"); 
//        //执行意图进行安装 
//        mContext.startActivity(intent);
//    }
//    
//    public  int getContentLengthFormUrl(String Url){
//		int mContentLength = 0;
//		try {
//			 java.net.URL url=new java.net.URL(Url);
//			 HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
//			 mHttpURLConnection.setConnectTimeout(5 * 1000);
//			 mHttpURLConnection.setRequestMethod("GET");
//			 //由于之前测试在网络上下载的文件长度一直是-1，所以加上了这串代码就好了，正式这个在Andbase里的那个方法里没有 所以长度才一直是-1
//			 mHttpURLConnection.setRequestProperty("Accept-Encoding", "iden" +"tity");			 
//			 mHttpURLConnection.setRequestProperty("Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//			 mHttpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
//			 mHttpURLConnection.setRequestProperty("Referer", Url);
//			 mHttpURLConnection.setRequestProperty("Charset", "UTF-8");
//			 mHttpURLConnection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//			 mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
//			 mHttpURLConnection.connect();
//			 if (mHttpURLConnection.getResponseCode() == 200){
//				 // 根据响应获取文件大小
//				 mContentLength = mHttpURLConnection.getContentLength();
//			 }else{
//			 }
//	    } catch (Exception e) {
//	    	 e.printStackTrace();
//	    	 AbLogUtil.d(AbFileUtil.class, "获取长度异常："+e.getMessage());
//		}
//		return mContentLength;
//	}
//}
