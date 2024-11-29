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

        // ��ʼ����ͼ
        imagePreview = findViewById(R.id.image_preview);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSendImage = findViewById(R.id.btn_send_image);

        // ����ѡ��ͼƬ��ť����¼�
        // ����ѡ��ͼƬ��ť�ĵ���¼�
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser(); // ��ͼƬѡ����
            }
        });

        // ���÷���ͼƬ��ť����¼�
        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri = mediaUrl; // ������ѡ���ͼƬ URI
                if (imageUri != null) {
                    uploadImageToServer(imageUri);
                } else {
                    Toast.makeText(SendImageActivity.this, "��ѡ��һ��ͼƬ", Toast.LENGTH_SHORT).show();
                }
//                finish(); // ������ͼƬ�󣬹رմ˽���
            }
        });
        // �ҵ����ذ�ť
        Button btnBack = findViewById(R.id.btn_back);

        // ���õ���¼�������
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ������ǰActivity�����ص���һ��Activity
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData(); // ��ȡͼƬ�� URI
            this.mediaUrl=imageUri;
            displaySelectedImage(imageUri); // ��ʾͼƬԤ��
        }
    }
    private void displaySelectedImage(Uri imageUri) {
        if (imageUri != null) {
            imagePreview.setImageURI(imageUri); // ����ͼƬ URI
        }
    }

    // ��ϵͳͼƬѡ�����ķ���
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "ѡ��ͼƬ"), PICK_IMAGE_REQUEST);
    }
    private void uploadImageToServer(Uri imageUri) {
        try {
            // �� URI ��ȡ Bitmap ��ѹ���� JPEG ��ʽ
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();

            // ʹ�� OkHttp �ϴ�ͼƬ
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
                        Toast.makeText(SendImageActivity.this, "ͼƬ�ϴ�ʧ��", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // ��ȡ��˷��ص�ͼƬ URL
                        String imageUrl = response.body().string();
                        runOnUiThread(() -> {
//                            sendImageMessage(imageUrl); // ����ͼƬ��Ϣ
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("imageUrl", imageUrl); // ��ͼƬURL����Intent
                            setResult(RESULT_OK, resultIntent); // ���÷��ؽ��ΪOK
                            finish(); // �رյ�ǰActivity
                        });
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

