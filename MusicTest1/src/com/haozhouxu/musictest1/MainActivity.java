package com.haozhouxu.musictest1;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.jakewharton.disklrucache.*;

public class MainActivity extends Activity implements OnClickListener {

	protected static final int MSG_SUCCESS = 0;
	protected static final int MSG_FAILURE = 1;
	private Button BtnPlay;
	private ImageView mImage;
	private DiskLruCache mDiskLruCache = null;
//	private MediaPlayer mediaPlayer = new MediaPlayer();
	private Boolean isDownload = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mImage = (ImageView) findViewById(R.id.image1);
		initLoad();
		LoadImage();
		// BtnPlay = (Button)findViewById(R.id.play);
		// BtnPlay.setOnClickListener(this);
		// initmediaPlay();
	}
	
	 private Handler mHandler = new Handler() {  
	        public void handleMessage (Message msg) {//此方法在ui线程运行  
	            switch(msg.what) {  
	            case MSG_SUCCESS:  
	            	LoadImage();
	                Toast.makeText(getApplication(), "success", Toast.LENGTH_LONG).show();  
	                break;  
	  
	            case MSG_FAILURE:  
	                Toast.makeText(getApplication(), "fail", Toast.LENGTH_LONG).show();  
	                break;  
	            }  
	        }  
	    };  
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			if (null!=mDiskLruCache) {
				mDiskLruCache.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if (null!=mDiskLruCache) {
				mDiskLruCache.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void initLoad() {
		// TODO Auto-generated method stub
		try {
			File cacheDir = getDiskCacheDir(MainActivity.this, "bitmap");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(MainActivity.this), 1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void LoadImage() {
		// TODO Auto-generated method stub
		try {  
		    String imageUrl = "http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg";  
		    String key = hashKeyForDisk(imageUrl);  
		    DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);  
		    if (snapShot != null) {  
		        InputStream is = snapShot.getInputStream(0);  
		        Bitmap bitmap = BitmapFactory.decodeStream(is);  
		        mImage.setImageBitmap(bitmap);  
		    } 
		    else{
		    	if (!isDownload) {
			    	DownloadImage();
			    	isDownload = true;
				}
		    }
		} catch (IOException e) {  
		    e.printStackTrace();  
		}  
	}
	
	

	private void DownloadImage() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String imageUrl = "http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg";
					String key = hashKeyForDisk(imageUrl);
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
				    	
					}
					mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
				} catch (IOException e) {
					mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
					e.printStackTrace();
				}
			}
		}).start();
	}

	private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
			out = new BufferedOutputStream(outputStream, 8 * 1024);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
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
