package com.xiankezu.sirceo.activitys;

import com.xiankezu.sirceo.R;
import com.xiankezu.sirceo.activitys.base.BaseActivity;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void findViews() {
		mLoadingDialog.show();
	}

	@Override
	protected void initSomething() {
		// TODO Auto-generated method stub
		
	}

	

}
