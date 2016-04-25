package com.haozhouxu.musictest1;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	private Button BtnPlay;
	private MediaPlayer mediaPlayer = new MediaPlayer();;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		BtnPlay = (Button)findViewById(R.id.play);
		BtnPlay.setOnClickListener(this);
		initmediaPlay();
	}
	
	private void initmediaPlay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.play:
			
			break;

		default:
			break;
		}
	}

}
