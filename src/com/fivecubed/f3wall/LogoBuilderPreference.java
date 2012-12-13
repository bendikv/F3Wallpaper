package com.fivecubed.f3wall;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class LogoBuilderPreference extends DialogPreference {
	
	private RelativeLayout rl;
	private BuilderView builder = null;

	public LogoBuilderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LogoBuilderPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		rl = (RelativeLayout)view.findViewById(R.id.builderPlace);
		
		rl.removeAllViews();
		builder = new BuilderView(getContext());
        rl.addView(builder);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
			Editor pe = prefs.edit();
			ParticlesBoard b = builder.getBoard();
			int k=0, l=0;
	        for (int i=0;i<b.getWidth();i++) 
	        {
	            for (int j=0;j<b.getHeight();j++) 
	            {
	            	pe.remove("cubeX"+l);
	            	pe.remove("cubeY"+l);
	            	l++;
	            	if (b.getParticle(i, j)==1)
	            	{
	            		pe.putFloat("cubeX"+k, (float)i);
	            		pe.putFloat("cubeY"+k, (float)j);
	            		k++;
	            	}
	            }
	        }
    		pe.putInt("cubeSize", k);
			pe.commit();
		}
	}	
}
