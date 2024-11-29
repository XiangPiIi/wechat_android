package cn.homework.myweixin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class ChatActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */

	private WebSocket webSocket;
	private Button mBtnImage;
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private String senderID;
	private String receiverId;
	private String receiverName;
	private String receiverAvatarUrl;
	private static final int PICK_IMAGE_REQUEST = 1;




	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_xiaohei);
        //��ȡ�û�id
//		this.senderID=getIntent().getStringExtra("Sender_ID");
		this.senderID=UserInfo.getInstance().getUserId();
		this.receiverId=getIntent().getStringExtra("Receiver_ID");
		this.receiverName=getIntent().getStringExtra("ReceiverName");
		this.receiverAvatarUrl=getIntent().getStringExtra("ReceiverAvatur");

		TextView tvUsername=findViewById(R.id.tv_username);
		tvUsername.setText(this.receiverName);
//		System.out.println(this.senderID);

		connectWebSocket();

        //����activityʱ���Զ����������
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initView();
        initData();


    }

	private void connectWebSocket() {
		System.out.println("websocket��������");
		OkHttpClient client = new OkHttpClient();
		String websocketurl="ws://10.0.2.2:9090/chat";
		Request request = new Request.Builder().url(websocketurl).build();
		webSocket = client.newWebSocket(request, new WebSocketListener() {
			@Override
			public void onOpen(WebSocket webSocket, okhttp3.Response response) {
				// WebSocket �����Ѵ�
				System.out.println("websocket���ӳɹ�");
				System.out.println(mDataArrays.toString());

				// ���������֤��Ϣ
				JSONObject identifyMessage = new JSONObject();
				try {
					identifyMessage.put("type", "identify");
					identifyMessage.put("senderId", senderID);
					identifyMessage.put("receiverId",receiverId);
					webSocket.send(identifyMessage.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onMessage(WebSocket webSocket, String text) {
				runOnUiThread(() -> {
					try {
						JSONObject jsonMessage = new JSONObject(text);
						if (jsonMessage.has("type") && jsonMessage.getString("type").equals("history")) {
							// ������ʷ��Ϣ
							JSONArray messages = jsonMessage.getJSONArray("messages");
							for (int i = 0; i < messages.length(); i++) {
								JSONObject message = messages.getJSONObject(i);
								addMessage(
										message.getString("senderId"),
										message.getString("receiverId"),
										message.getString("messageContent"),
										message.getString("mediaUrl"),
										new Date(message.getLong("timestamp"))
								);
							}
							mAdapter.notifyDataSetChanged();
//							System.out.println(mDataArrays.toString());
						} else {
							// ������ͨ��Ϣ
//							JSONObject jsonMessage = new JSONObject(text);
							String senderId = jsonMessage.getString("senderId");
							String messageContent = jsonMessage.getString("messageContent");
							String receiverId=jsonMessage.getString("receiverId");
							String mediaUrl=jsonMessage.getString("mediaUrl");
							Long timestamp=jsonMessage.getLong("timestamp");
							Date date=new Date(timestamp);
							addMessage(senderId, receiverId, messageContent, mediaUrl,date);
							mAdapter.notifyDataSetChanged();
//							System.out.println("�������յ���");
							mListView.setSelection(mListView.getCount() - 1);
//							System.out.println(mDataArrays.toString());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				});
			}

			@Override
			public void onMessage(WebSocket webSocket, ByteString bytes) {
				// ���յ���������Ϣ
			}

			@Override
			public void onClosing(WebSocket webSocket, int code, String reason) {
				webSocket.close(1000, null);
			}

			@Override
			public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
				System.out.println("websocket����ʧ��");
				t.printStackTrace(); // ��ӡ������ջ���鿴����ԭ��
				if (response != null) {
					System.out.println("������룺" + response.code());
					System.out.println("������Ϣ��" + response.message());
				}
			}
		});
	}

	private void sendMessage(String messageContent) {
		if (webSocket != null) {
			// ������Ϣ����
			JSONObject message = new JSONObject();
			try {
				message.put("senderId", senderID); // ʹ��senderID�ֶ�
				message.put("receiverId", receiverId); // ʹ��receiverId�ֶ�
				message.put("messageContent", messageContent);
				message.put("mediaUrl", "empty"); // ������Ӷ�ý�� URL��������Ϊ null
				message.put("timestamp", System.currentTimeMillis());
				message.put("isRead", false);
				message.put("type","chat");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String contString = mEditTextContent.getText().toString();
			//��ӵ�UI
			Long nowTime=System.currentTimeMillis();
			addMessage(senderID,receiverId,contString,"empty",new Date(nowTime));
			// ������Ϣ
			webSocket.send(message.toString());
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			mListView.setSelection(mListView.getCount() - 1);
		}
	}
	private void sendImageMessage(String mediaUrl) {
		if (webSocket != null) {
			// ������Ϣ����
			JSONObject message = new JSONObject();
			try {
				message.put("senderId", senderID); // ʹ��senderID�ֶ�
				message.put("receiverId", receiverId); // ʹ��receiverId�ֶ�
				message.put("messageContent", "[ͼƬ]");
				message.put("mediaUrl", mediaUrl); // ������Ӷ�ý�� URL��������Ϊ null
				message.put("timestamp", System.currentTimeMillis());
				message.put("isRead", false);
				message.put("type","chat");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Long nowTime=System.currentTimeMillis();
			addMessage(senderID,receiverId,"[ͼƬ]",mediaUrl,new Date(nowTime));
			// ������Ϣ
			webSocket.send(message.toString());
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			mListView.setSelection(mListView.getCount() - 1);
		}
	}

	public void addMessage(String senderId, String receiverId, String messageContent, String mediaUrl,Date timestamp) {

			ChatMsgEntity chatMessage = new ChatMsgEntity(
					0,
					senderId,
					receiverId,
					messageContent,
					mediaUrl,
					timestamp, // ���õ�ǰʱ��Ϊʱ���
					false // ��ʼ״̬Ϊδ��
			);
			// ����ѡ�� chatMessage ������ӵ��б����Ա������ʾ
			mDataArrays.add(chatMessage);

	}

	public void initView()
    {
    	mListView = (ListView) findViewById(R.id.listview);
    	mBtnSend = (Button) findViewById(R.id.btn_send);
    	mBtnSend.setOnClickListener(this);
    	mBtnBack = (Button) findViewById(R.id.btn_back);
    	mBtnBack.setOnClickListener(this);
    	
    	mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		mBtnImage = (Button) findViewById(R.id.btn_send_image); // ���衰ͼƬ����ťID��btn_image
		mBtnImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChatActivity.this, SendImageActivity.class);
//				startActivity(intent); // ����SendImageActivity
				startActivityForResult(intent, PICK_IMAGE_REQUEST);
			}
		});

    }

    public void initData()
    {
    	mAdapter = new ChatMsgViewAdapter(this, mDataArrays,senderID,this.receiverName,this.receiverAvatarUrl);
		mListView.setAdapter(mAdapter);
		
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
			// ��ȡͼƬURL
			String imageUrl = data.getStringExtra("imageUrl");
			if (imageUrl != null) {
				sendImageMessage(imageUrl); // ����ͼƬ��Ϣ
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			Intent intent = new Intent();
			intent.putExtra("refresh", true); // ���ݱ�־
			setResult(RESULT_OK, intent); // ���ý��
			finish(); // ������ǰ Activity
			break;
		}
	}

	private void send()
	{
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0)
		{
			sendMessage(contString); // ͨ�� WebSocket ������Ϣ

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (webSocket != null) {
			webSocket.close(1000, "Activity���ٹر�����");
		}
	}

}