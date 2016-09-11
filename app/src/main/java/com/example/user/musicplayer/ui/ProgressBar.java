package com.example.user.musicplayer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.musicplayer.R;


public class ProgressBar extends RelativeLayout{
	
	private static final int PROGRESS_MAX = 1000;
	private final float initBgLpWidth = 36;
	private View bgView;
	private View attachView;
	private MarginLayoutParams bgLp;
	private MarginLayoutParams attachLp;
	
	private float processFrom = 0.0f;
	private float processTo = 0.0f;
	private String attachViewText;
	private int attachViewWidth = 0;
	
	public ProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		bgView = findViewById(R.id.bg_view);
		bgLp = (MarginLayoutParams)bgView.getLayoutParams();
//		attachView = findViewById(R.id.attached_view);
//		if(attachView != null){
//			attachLp = (MarginLayoutParams)attachView.getLayoutParams();
//		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		bgLp.width = (int)((MeasureSpec.getSize(widthMeasureSpec)-initBgLpWidth) * (processTo - processFrom)/PROGRESS_MAX + 0.5f + initBgLpWidth);
		bgLp.leftMargin = (int)(MeasureSpec.getSize(widthMeasureSpec) * processFrom + 0.5f);
		layoutAttachView(widthMeasureSpec);
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
	}

	private void layoutAttachView(int widthMeasureSpec) {
		if(attachLp != null){
			((TextView)attachView).setText(attachViewText);
			attachViewWidth = attachView.getMeasuredWidth();
			if(attachView.getWidth() > attachViewWidth) {
				attachViewWidth = attachView.getWidth();
			}
			attachLp.leftMargin = bgLp.leftMargin + bgView.getMeasuredWidth() - attachViewWidth/2;
			if(attachLp.leftMargin < 0) {
				attachLp.leftMargin = 0;
			}
			if(attachLp.leftMargin > MeasureSpec.getSize(widthMeasureSpec) - attachViewWidth) {
				attachLp.leftMargin = MeasureSpec.getSize(widthMeasureSpec) - attachViewWidth;
			}
		}
	}

	public void setProcessRange(float from, float to){
		this.processFrom = from;
		this.processTo = to;
		requestLayout();
	}
	
	public void setAttachViewText(String text) {
		this.attachViewText = text;
	}
}
