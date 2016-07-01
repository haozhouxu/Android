package com.haozhouxu.ThemeChange;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        ThemeChangeUtil.changeTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void changeTheme(View view) {
		if (ThemeChangeUtil.isChange) {
			ThemeChangeUtil.isChange = false;
		} else {
			ThemeChangeUtil.isChange = true;
		}
		MainActivity.this.recreate();// 重新创建当前Activity实例
	}
}
