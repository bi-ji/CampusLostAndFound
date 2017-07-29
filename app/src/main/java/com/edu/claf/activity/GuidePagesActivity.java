package com.edu.claf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.SharedPrefUtils;

import java.util.ArrayList;

public class GuidePagesActivity extends Activity {

	private ViewPager vp;
	private ArrayList<ImageView> views;
	private final int[] splashImages = new int[]{R.drawable.guide1,R.drawable.guide2,
			R.drawable.guide3};
	private LinearLayout ll_point;
	private int mPointWidth;
	private View redPoint;
	private Button btnStart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guidepagers);
		BaseApplication.getApplication().addActivity(this);
		vp = (ViewPager)findViewById(R.id.vp_guide);
		ll_point =(LinearLayout)findViewById(R.id.ll_point_group);
		redPoint = findViewById(R.id.view_red_point);
		btnStart = (Button) findViewById(R.id.btn_start);
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPrefUtils.setBoolean(GuidePagesActivity.this,"is_user_guide_showed",true);
				Intent intent = new Intent(GuidePagesActivity.this,MainActivity.class);
				intent.putExtra("type","guidePage");
				startActivity(intent);
				finish();
			}
		});
		initViews();
		vp.setAdapter(new GuideAdapter());
        vp.addOnPageChangeListener(new GuidePageChangeListener());
	}


	private void initViews(){
		views = new ArrayList<ImageView>();
		for (int i = 0; i < splashImages.length; i++) {
			ImageView myImageView = new ImageView(this);
			myImageView.setBackgroundResource(splashImages[i]);
			views.add(myImageView);
		}

		for (int j = 0; j < splashImages.length;j++){
            View dot = new View(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15,15);
			dot.setBackgroundResource(R.drawable.shap_point_gray);
			if (j > 0){
				params.leftMargin = 10;
			}
			dot.setLayoutParams(params);
			ll_point.addView(dot);
			ll_point.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {
					 @Override
					 public void onGlobalLayout() {
						 ll_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						 mPointWidth = ll_point.getChildAt(1).getLeft()
								 - ll_point.getChildAt(0).getLeft();
					 }
				 }
			);
		}
	}


	class GuideAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return splashImages.length;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

	}


    class GuidePageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			int len =(int) (mPointWidth * positionOffset) + (position * mPointWidth);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)redPoint.getLayoutParams();
			params.leftMargin = len;
			redPoint.setLayoutParams(params);
        }
        @Override
        public void onPageSelected(int position) {
			if (position == splashImages.length - 1){
				btnStart.setVisibility(View.VISIBLE);
			}else{
				btnStart.setVisibility(View.INVISIBLE);
			}
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }



}
