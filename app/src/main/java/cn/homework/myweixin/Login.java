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
	private EditText mUser; // �ʺű༭��
	private EditText mPassword; // ����༭��
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
                    .setTitle("��¼ʧ��")
                    .setMessage("�û��������벻��Ϊ��")
                    .create().show();
            return;
        }
        // ����JSON������
        String json = "{\"userId\":\"" + user_id + "\", \"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        // ��������
        Request request = new Request.Builder()
                .url("http://10.0.2.2:9090/api/login")
                .post(body)
                .build();
        // ��������
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "�������", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // ��¼�ɹ�����ת��LoadingActivity
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
                    // ��¼ʧ�ܣ���ʾ������Ϣ
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(Login.this)
                                .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                                .setTitle("��¼ʧ��")
                                .setMessage("�û������������")
                                .create().show();
                    });
                }
            }
        });




//    	if("buaa".equals(mUser.getText().toString()) && "123".equals(mPassword.getText().toString()))   //�ж� �ʺź�����
//        {
//             Intent intent = new Intent();
//             intent.setClass(Login.this,LoadingActivity.class);
//             startActivity(intent);
//             //Toast.makeText(getApplicationContext(), "��¼�ɹ�", Toast.LENGTH_SHORT).show();
//          }
//        else if("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString()))   //�ж� �ʺź�����
//        {
//        	new AlertDialog.Builder(Login.this)
//			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
//			.setTitle("��¼����")
//			.setMessage("΢���ʺŻ������벻��Ϊ�գ�\n��������ٵ�¼��")
//			.create().show();
//         }
//        else{
//
//        	new AlertDialog.Builder(Login.this)
//			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
//			.setTitle("��¼ʧ��")
//			.setMessage("΢���ʺŻ������벻��ȷ��\n������������룡")
//			.create().show();
//        }
    	
    	//��¼��ť
    	/*
      	Intent intent = new Intent();
		intent.setClass(Login.this,Whatsnew.class);
		startActivity(intent);
		Toast.makeText(getApplicationContext(), "��¼�ɹ�", Toast.LENGTH_SHORT).show();
		this.finish();*/
      }  
    public void login_back(View v) {     //������ ���ذ�ť
      	this.finish();
      }  
    public void login_pw(View v) {     //�������밴ť
    	Uri uri = Uri.parse("http://3g.qq.com"); 
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
    	startActivity(intent);
    	//Intent intent = new Intent();
    	//intent.setClass(Login.this,Whatsnew.class);
        //startActivity(intent);
      }  
}
