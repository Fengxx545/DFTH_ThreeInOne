package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.dftaihua.dfth_threeinone.mediator.Component;
import com.dftaihua.dfth_threeinone.mediator.ECGHistoryMediator;

public class ECGLookProgressBar extends SeekBar implements OnSeekBarChangeListener, Component<ECGHistoryMediator> {
	private ECGHistoryMediator mMediator;

	public ECGLookProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnSeekBarChangeListener(this);
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
								  boolean fromUser) {
		if(mMediator != null && fromUser){
			mMediator.seekBarChange(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void bindMediator(ECGHistoryMediator mediator) {
		mMediator = mediator;
	}
}
