package com.xiankezu.sirceo.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Author: wangzhenfei Date: 15-4-15 Time: 10:07
 * Description:通用的listview，gridview的�?配器，一般情况下不要修改这个�?并且其它的�?配器也应extends这个�?
 * Version: 1.0
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	protected List<T> mDatas;
	protected LayoutInflater mLayoutInflater;
	protected Context mContext;
	protected final int mItemLayoutId;
	protected int position;

	/**
	 * 构�?方法
	 * 
	 * @param mDatas
	 *            数据�?
	 * @param mContext
	 *            上下�?
	 * @param mItemLayoutId
	 *            布局文件的id
	 */
	public CommonAdapter(List<T> mDatas, Context mContext, int mItemLayoutId) {
		this.mItemLayoutId = mItemLayoutId;
		mLayoutInflater = LayoutInflater.from(mContext);
		this.mDatas = mDatas;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int i) {
		return mDatas.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		position = i;
		final ViewHolder viewHolder = getViewHolder(i, view, viewGroup);
		convert(viewHolder, getItem(i));
		return viewHolder.getConvertView();
	}

	/**
	 * �?��心的回调方法
	 * 
	 * @param viewHolder
	 * @param item
	 */
	public abstract void convert(ViewHolder viewHolder, T item);

	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
				position);
	}
}
