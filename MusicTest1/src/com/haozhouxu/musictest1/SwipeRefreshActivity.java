package com.haozhouxu.musictest1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout;

public class SwipeRefreshActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeLayout;
    ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_swipe_refresh);
		
//		mListView = (ListView) findViewById(R.id.listview);
//        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData()));

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColorSchemeResource(R.color.red);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		 new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
					// 停止刷新
	                mSwipeLayout.setRefreshing(false);
	            }
	        }, 5000); // 5秒后发送消息，停止刷新
	}
}
