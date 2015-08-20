package com.xiankezu.sirceo.widghts;

import com.xiankezu.sirceo.R;

import android.content.Context;


public class FlippingLoadingDialog extends BaseDialog {

	private FlippingImageView mFivIcon;
	private HandyTextView mHtvText;
	private String mText;

	public FlippingLoadingDialog(Context context, String text) {
		super(context);
		mText = text;
		init();
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}

	private void init() {
		setContentView(R.layout.common_flipping_loading_diloag);
		mFivIcon = (FlippingImageView) findViewById(R.id.loadingdialog_fiv_icon);
		mHtvText = (HandyTextView) findViewById(R.id.loadingdialog_htv_text);
		mFivIcon.startAnimation();
		mHtvText.setText(mText);
		setCancelable(false);
	}

	public void setText(String text) {
		mText = text;
		mHtvText.setText(mText);
	}
}
