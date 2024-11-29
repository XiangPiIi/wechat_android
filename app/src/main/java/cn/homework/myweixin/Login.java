package cn.homework.myweixin;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Login extends Activity {
	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框
    private OkHttpClient client = new OkHttpClient();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        mUser = (EditText)findViewById(R.id.login_user_edit);
        mPassword = (EditText)findViewById(R.id.login_passwd_edit);
        
    }

    public void login_mainweixin(View v) {
        String user_id = mUser.getText().toString();
        String password = mPassword.getText().toString();
        if (user_id.isEmpty() || password.isEmpty()) {
            new AlertDialog.Builder(Login.this)
                    .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                    .setTitle("登录失败")
                    .setMessage("用户名和密码不能为空")
                    .create().show();
            return;
        }
        // 创建JSON请求体
        String json = "{\"userId\":\"" + user_id + "\", \"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        // 创建请求
        Request request = new Request.Builder()
                .url("http://10.0.2.2:9090/api/login")
                .post(body)
                .build();
        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 登录成功，跳转到LoadingActivity
                    String responseData = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(responseData);
                        String username = jsonObject.getString("username");
                        String avatarUrl=jsonObject.getString("avatarUrl");
                        UserInfo.getInstance().setUserId(user_id);
                        UserInfo.getInstance().setAvatarUrl(avatarUrl);
                        UserInfo.getInstance().setUserName(username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Login.this, LoadingActivity.class);

//                    UserInfo.getInstance().setAvatarUrl();
                    intent.putExtra("USER_ID",user_id);
                    startActivity(intent);
                    finish();
                } else {
                    // 登录失败，显示错误信息
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(Login.this)
                                .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                                .setTitle("登录失败")
                                .setMessage("用户名或密码错误")
                                .create().show();
                    });
                }
            }
        });




//    	if("buaa".equals(mUser.getText().toString()) && "123".equals(mPassword.getText().toString()))   //判断 帐号和密码
//        {
//             Intent intent = new Intent();
//             intent.setClass(Login.this,LoadingActivity.class);
//             startActivity(intent);
//             //Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
//          }
//        else if("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString()))   //判断 帐号和密码
//        {
//        	new AlertDialog.Builder(Login.this)
//			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
//			.setTitle("登录错误")
//			.setMessage("微信帐号或者密码不能为空，\n请输入后再登录！")
//			.create().show();
//         }
//        else{
//
//        	new AlertDialog.Builder(Login.this)
//			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
//			.setTitle("登录失败")
//			.setMessage("微信帐号或者密码不正确，\n请检查后重新输入！")
//			.create().show();
//        }
    	
    	//登录按钮
    	/*
      	Intent intent = new Intent();
		intent.setClass(Login.this,Whatsnew.class);
		startActivity(intent);
		Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
		this.finish();*/
      }  
    public void login_back(View v) {     //标题栏 返回按钮
      	this.finish();
      }  
    public void login_pw(View v) {     //忘记密码按钮
    	Uri uri = Uri.parse("http://3g.qq.com"); 
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
    	startActivity(intent);
    	//Intent intent = new Intent();
    	//intent.setClass(Login.this,Whatsnew.class);
        //startActivity(intent);
      }  
}
