package com.dexilog.smartkeyboard.ui.preferences;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.preference.DialogPreference;  
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


public class SeekBarPref extends DialogPreference implements SeekBar.OnSeekBarChangeListener {	
    private Context context; 
    private SeekBar bar = null;
    private TextView text = null;
    private LinearLayout layout = null;
    private AudioManager mAudioManager = null;
    private Vibrator mVibrator = null;
    private float mDisplayFactor = 1, mMin = 0, mMax=100, mDefault = 100;
    
    public SeekBarPref(Context context, AttributeSet attrs) { 
        super(context, attrs); 
        this.context = context;
		try
		{
			mMin = attrs.getAttributeFloatValue(null, "minValue", 0);
			mMax = attrs.getAttributeFloatValue(null, "maxValue", 100);
			mDisplayFactor = attrs.getAttributeFloatValue(null, "displayFactor",1.0f);
			mDefault = attrs.getAttributeFloatValue(android.R.attr.defaultValue,100);
		}
		catch(Exception e){}
    } 
    
    protected void onPrepareDialogBuilder(Builder builder) {
    	
    	if (getKey().equals("volume")) {
    		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    	}
    	else if (getKey().equals("vibrator_duration_ms")) {
			mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    	}
    	
        layout = new LinearLayout(context); 
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)); 
        layout.setMinimumWidth(400); 
        layout.setPadding(20, 40, 20, 20);
        layout.setOrientation(LinearLayout.VERTICAL);
        bar = new SeekBar(context);
        bar.setOnSeekBarChangeListener(this);

        text = new TextView(context);
        text.setPadding(50, 50, 50, 20);

        final String key = getKey();
        if (key.equals("vibrator_duration_ms")) {
        	bar.setMax(75);
        	bar.setProgress((int)getPersistedFloat(20));
        }
        else if (key.equals("opacity")) {
        	bar.setMax(25);
            mDisplayFactor = 2;
        	bar.setProgress((int)getPersistedFloat(50)/2);
        } else if (key.equals("longpress_duration")) {
        	bar.setMax(100);
        	bar.setProgress((int)getPersistedFloat(50));
        } else if (key.equals("swipe_factor")) {
        	bar.setMax(100);
        	bar.setProgress((int)getPersistedFloat(70));
        } else if (key.equals("multitap_interval")) {
        	bar.setMax(150);
        	bar.setProgress((int)getPersistedFloat(80));
        } else if (key.equals("bottom_padding")) {
        	bar.setMax(40);
    		bar.setProgress((int)getPersistedFloat(0));
        } else {
        	bar.setMax((int)((mMax - mMin)/mDisplayFactor));
        	bar.setProgress((int)((getPersistedFloat(mDefault) - mMin )/ mDisplayFactor));
        }
        bar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)); 
        layout.addView(bar);
        setText();
        layout.addView(text);

        builder.setView(layout); 
    } 
    
    protected void onDialogClosed(boolean positiveResult) { 
        if(positiveResult){ 
            persistFloat(mMin + bar.getProgress()*mDisplayFactor); 
        } 
        super.onDialogClosed(positiveResult);
    }

    private void setText() {
        text.setText(String.format("%f",(mMin + bar.getProgress() * mDisplayFactor)));
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
        setText();
		if (mAudioManager != null) {
			float volume = (float) Math.exp((progress-100)/20);
			mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volume);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
        if (mVibrator != null) {
            mVibrator.vibrate(seekBar.getProgress());
        }
	} 

}
