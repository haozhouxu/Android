package com.haozhouxu.musictest1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.haozhouxu.musictest1.R.string;
import com.jakewharton.disklrucache.DiskLruCache;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MuiscCacheActivity extends Activity implements OnClickListener {

	protected static final int MSG_SUCCESS = 0;
	protected static final int MSG_FAILURE = 1;
	private DiskLruCache mDiskLruCache = null;
	private MediaPlayer mediaPlayer = null;
	private Boolean isDownload = false;
	Button playBtn ,pauseBtn,stopBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_muisc_cache);
		playBtn = (Button)findViewById(R.id.playBtn);
		pauseBtn = (Button)findViewById(R.id.pauseBtn);
		stopBtn = (Button)findViewById(R.id.stopBtn);
		playBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
		mediaPlayer = new MediaPlayer();
		initLoad();
		initMusic();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.playBtn:
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.start();
			}
			break;
		case R.id.pauseBtn:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
			break;
		case R.id.stopBtn:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
			mediaPlayer.stop();
			try {
				mediaPlayer.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {// 此方法在ui线程运行
			switch (msg.what) {
			case MSG_SUCCESS:
				initMusic();
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
			if (null != mDiskLruCache) {
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
			if (null != mDiskLruCache) {
				mDiskLruCache.close();
			}
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void initLoad() {
		// TODO Auto-generated method stub
		try {
			File cacheDir = getDiskCacheDir(MuiscCacheActivity.this, "music");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(MuiscCacheActivity.this), 1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initMusic() {
		// TODO Auto-generated method stub
		try {
//			String musicUrl = "http://miao.pujisi.org/Files/music/6357991855459854559687709.mp3";
			String musicUrl = "http://miao.pujisi.org/Files/music/6357991612487364512460931.mp3";
			String key = hashKeyForDisk(musicUrl);
			DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
			if (snapShot != null) {
				Toast.makeText(getApplication(), "have download ,ready to play", Toast.LENGTH_LONG).show();
				InputStream is = snapShot.getInputStream(0);
				//创建一个临时文件，用来把流转换为文件
				File tempFilePath = getTempDir(MuiscCacheActivity.this);
				//1.1 判断当前目录是否存在
				if (tempFilePath.isDirectory()&&!tempFilePath.exists()) {
					tempFilePath.mkdirs();
				}
				File tempflie = getDiskCacheDir(MuiscCacheActivity.this, "musicTempFile");
				if (tempflie.exists()) {
					tempflie.delete();
				}
				File temp = new File(tempflie.getAbsolutePath());
				FileOutputStream out = new FileOutputStream(temp);
				// 用BufferdOutputStream速度快
				BufferedOutputStream bis = new BufferedOutputStream(out);
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					if (numread <= 0)
						break;
					bis.write(buf, 0, numread);
				} while (true);
				Log.e("path", temp.getAbsolutePath());
				mediaPlayer.setDataSource(temp.getAbsolutePath());
				mediaPlayer.prepare();
			} else {
				if (!isDownload) {
					Toast.makeText(getApplication(), "begin download", Toast.LENGTH_LONG).show();
					DownloadMusic(musicUrl);
					isDownload = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void DownloadMusic(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String key = hashKeyForDisk(url);
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(url, outputStream)) {
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
	
	public File getTempDir(Context context) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath);
	}
}
