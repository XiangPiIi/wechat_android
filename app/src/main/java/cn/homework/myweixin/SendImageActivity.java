package cn.homework.myweixin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendImageActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePreview;
    private Button btnSelectImage;
    private Button btnSendImage;
    private Uri mediaUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_image);

        // 初始化视图
        imagePreview = findViewById(R.id.image_preview);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSendImage = findViewById(R.id.btn_send_image);

        // 设置选择图片按钮点击事件
        // 设置选择图片按钮的点击事件
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(); // 打开图片选择器
            }
        });

        // 设置发送图片按钮点击事件
        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri = mediaUrl; // 保存你选择的图片 URI
                if (imageUri != null) {
                    uploadImageToServer(imageUri);
                } else {
                    Toast.makeText(SendImageActivity.this, "请选择一张图片", Toast.LENGTH_SHORT).show();
                }
//                finish(); // 发送完图片后，关闭此界面
            }
        });
        // 找到返回按钮
        Button btnBack = findViewById(R.id.btn_back);

        // 设置点击事件监听器
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 结束当前Activity，返回到上一个Activity
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData(); // 获取图片的 URI
            this.mediaUrl=imageUri;
            displaySelectedImage(imageUri); // 显示图片预览
        }
    }
    private void displaySelectedImage(Uri imageUri) {
        if (imageUri != null) {
            imagePreview.setImageURI(imageUri); // 设置图片 URI
        }
    }

    // 打开系统图片选择器的方法
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), PICK_IMAGE_REQUEST);
    }
    private void uploadImageToServer(Uri imageUri) {
        try {
            // 从 URI 获取 Bitmap 并压缩成 JPEG 格式
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();

            // 使用 OkHttp 上传图片
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image.jpg", RequestBody.create(MediaType.parse("image/jpeg"), imageData))
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:9090/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(SendImageActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // 获取后端返回的图片 URL
                        String imageUrl = response.body().string();
                        runOnUiThread(() -> {
//                            sendImageMessage(imageUrl); // 发送图片消息
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("imageUrl", imageUrl); // 将图片URL放入Intent
                            setResult(RESULT_OK, resultIntent); // 设置返回结果为OK
                            finish(); // 关闭当前Activity
                        });
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

