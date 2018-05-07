package com.hireboss.android.roachlabs;
/*
 *Author:Gurdev Singh
 *date  :8 jan 2016
 *version:1.0
 */
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hireboss.android.roachlabs.font.MaterialDesignIconsTextView;
import com.hireboss.android.roachlabs.view.kbv.KenBurnsView;

public class SplashScreensActivity extends Activity {

	public static final String SPLASH_SCREEN_OPTION = "com.hireboss.android.roachlabs.SplashScreensActivity";
	public static final String SPLASH_SCREEN_OPTION_1 = "Fade in + Ken Burns";
	public static final String SPLASH_SCREEN_OPTION_2 = "Down + Ken Burns";
	public static final String SPLASH_SCREEN_OPTION_3 = "Down + fade in + Ken Burns";
	
	private KenBurnsView mKenBurns;
	private MaterialDesignIconsTextView mLogo;
	private TextView welcomeText;
	private ImageView mLogo1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
		setContentView(R.layout.activity_splash_screen);
		
		mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
		mLogo = (MaterialDesignIconsTextView) findViewById(R.id.logo);
		welcomeText = (TextView) findViewById(R.id.welcome_text);
		mLogo1=(ImageView)findViewById(R.id.logo1);

		setAnimation();

		setAnimation();
		Thread t=new Thread(){public void run(){
			try{
				sleep(5000);
			}
			catch(Exception e){e.printStackTrace();}
			finally{startActivity(new Intent(getApplicationContext() ,StartupAppIntro.class));

			}

		}};
		t.start();
	}
	
	/** Animation depends on category.
	 * */
	private void setAnimation() {
			mKenBurns.setImageResource(R.drawable.melb);
			animation2();
			animation3();

	}

	private void animation2() {
		mLogo1.setAlpha(1.0F);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
		mLogo1.startAnimation(anim);
	}
	
	private void animation3() {
		ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
		alphaAnimation.setStartDelay(1700);
		alphaAnimation.setDuration(500);
		alphaAnimation.start();
	}
}
