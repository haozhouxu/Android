package com.haozhouxu.musictest1;

import java.io.IOException;
import java.io.OutputStream;

import com.jakewharton.disklrucache.DiskLruCache;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;

public class SwipeRefreshActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

	SwipeRefreshLayout mSwipeLayout;
	ListView mListView;
	public static final int ref_success = 1;
	public static final int ref_fail = 2;
	private String[] fruits = {"apple","pear","banana"};

	Handler mhandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ref_success:
				Toast.makeText(SwipeRefreshActivity.this, "success", Toast.LENGTH_SHORT).show();
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SwipeRefreshActivity.this, android.R.layout.simple_list_item_1,fruits);
				mListView.setAdapter(adapter);
				mSwipeLayout.setRefreshing(false);
				break;
			case ref_fail:
				Toast.makeText(SwipeRefreshActivity.this, "fail", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_swipe_refresh);

		 mListView = (ListView) findViewById(R.id.listview);
		// mListView.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, getData()));

		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
		mSwipeLayout.setProgressBackgroundColorSchemeResource(R.color.red);
		mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
	}

	@Override
	public void onRefresh() {
//		// TODO Auto-generated method stub
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				// 停止刷新
//				mSwipeLayout.setRefreshing(false);
//			}
//		}, 5000); // 5秒后发送消息，停止刷新
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < 1000000; i++) {
						
					}
					mhandler.obtainMessage(ref_success).sendToTarget();
					
				} catch (Exception e) {
					mhandler.obtainMessage(ref_fail).sendToTarget();
					e.printStackTrace();
				}
			}
		}).start();
	}
}
