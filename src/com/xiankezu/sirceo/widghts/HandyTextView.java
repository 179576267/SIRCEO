package com.xiankezu.sirceo.widghts;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
public class HandyTextView extends TextView {
	private int selectColor;
	private int originColor;

	public HandyTextView(Context context) {
		this(context, null, 0);
	}

	public HandyTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HandyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		/*TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.HandyTextView, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.HandyTextView_origin_color:
				originColor = a.getInt(attr, 0);// 获得圆形边框的颜色
				Log.i("jack", "originColor0:" + originColor);
				break;
			}
		}
		Log.i("123", "HandyTextView");
		a.recycle();*/
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		if (text == null) {
			text = "";
		}
		super.setText(text, type);
	}
}
