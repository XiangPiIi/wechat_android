package cn.homework.myweixin;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import java.lang.ref.WeakReference;

public class LoadingActivity extends Activity{

	private final Handler handler = new Handler(Looper.getMainLooper());
	private WeakReference<LoadingActivity> activityReference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		String UserId = getIntent().getStringExtra("USER_ID");
		activityReference = new WeakReference<>(this);

		handler.postDelayed(() -> {
			LoadingActivity activity = activityReference.get();
			if (activity != null && !activity.isFinishing()) {
				Intent intent = new Intent(activity, MainWeixin.class);
				intent.putExtra("USER_ID", UserId);
				activity.startActivity(intent);
				activity.finish();
				Toast.makeText(activity.getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
			}
		}, 200);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null); // 清除Handler中的所有消息
	}
}
