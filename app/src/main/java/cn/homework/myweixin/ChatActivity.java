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
        //获取用户id
//		this.senderID=getIntent().getStringExtra("Sender_ID");
		this.senderID=UserInfo.getInstance().getUserId();
		this.receiverId=getIntent().getStringExtra("Receiver_ID");
		this.receiverName=getIntent().getStringExtra("ReceiverName");
		this.receiverAvatarUrl=getIntent().getStringExtra("ReceiverAvatur");

		TextView tvUsername=findViewById(R.id.tv_username);
		tvUsername.setText(this.receiverName);
//		System.out.println(this.senderID);

		connectWebSocket();

        //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initView();
        initData();


    }

	private void connectWebSocket() {
		System.out.println("websocket正在连接");
		OkHttpClient client = new OkHttpClient();
		String websocketurl="ws://10.0.2.2:9090/chat";
		Request request = new Request.Builder().url(websocketurl).build();
		webSocket = client.newWebSocket(request, new WebSocketListener() {
			@Override
			public void onOpen(WebSocket webSocket, okhttp3.Response response) {
				// WebSocket 连接已打开
				System.out.println("websocket连接成功");
				System.out.println(mDataArrays.toString());

				// 发送身份认证消息
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
							// 处理历史消息
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
							// 处理普通消息
//							JSONObject jsonMessage = new JSONObject(text);
							String senderId = jsonMessage.getString("senderId");
							String messageContent = jsonMessage.getString("messageContent");
							String receiverId=jsonMessage.getString("receiverId");
							String mediaUrl=jsonMessage.getString("mediaUrl");
							Long timestamp=jsonMessage.getLong("timestamp");
							Date date=new Date(timestamp);
							addMessage(senderId, receiverId, messageContent, mediaUrl,date);
							mAdapter.notifyDataSetChanged();
//							System.out.println("接收者收到了");
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
				// 接收到二进制消息
			}

			@Override
			public void onClosing(WebSocket webSocket, int code, String reason) {
				webSocket.close(1000, null);
			}

			@Override
			public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
				System.out.println("websocket连接失败");
				t.printStackTrace(); // 打印出错误栈，查看具体原因
				if (response != null) {
					System.out.println("错误代码：" + response.code());
					System.out.println("错误信息：" + response.message());
				}
			}
		});
	}

	private void sendMessage(String messageContent) {
		if (webSocket != null) {
			// 构建消息对象
			JSONObject message = new JSONObject();
			try {
				message.put("senderId", senderID); // 使用senderID字段
				message.put("receiverId", receiverId); // 使用receiverId字段
				message.put("messageContent", messageContent);
				message.put("mediaUrl", "empty"); // 可以添加多媒体 URL，或设置为 null
				message.put("timestamp", System.currentTimeMillis());
				message.put("isRead", false);
				message.put("type","chat");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String contString = mEditTextContent.getText().toString();
			//添加到UI
			Long nowTime=System.currentTimeMillis();
			addMessage(senderID,receiverId,contString,"empty",new Date(nowTime));
			// 发送消息
			webSocket.send(message.toString());
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			mListView.setSelection(mListView.getCount() - 1);
		}
	}
	private void sendImageMessage(String mediaUrl) {
		if (webSocket != null) {
			// 构建消息对象
			JSONObject message = new JSONObject();
			try {
				message.put("senderId", senderID); // 使用senderID字段
				message.put("receiverId", receiverId); // 使用receiverId字段
				message.put("messageContent", "[图片]");
				message.put("mediaUrl", mediaUrl); // 可以添加多媒体 URL，或设置为 null
				message.put("timestamp", System.currentTimeMillis());
				message.put("isRead", false);
				message.put("type","chat");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Long nowTime=System.currentTimeMillis();
			addMessage(senderID,receiverId,"[图片]",mediaUrl,new Date(nowTime));
			// 发送消息
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
					timestamp, // 设置当前时间为时间戳
					false // 初始状态为未读
			);
			// 可以选择将 chatMessage 对象添加到列表中以便后续显示
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
		mBtnImage = (Button) findViewById(R.id.btn_send_image); // 假设“图片”按钮ID是btn_image
		mBtnImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChatActivity.this, SendImageActivity.class);
//				startActivity(intent); // 启动SendImageActivity
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
			// 获取图片URL
			String imageUrl = data.getStringExtra("imageUrl");
			if (imageUrl != null) {
				sendImageMessage(imageUrl); // 发送图片消息
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
			intent.putExtra("refresh", true); // 传递标志
			setResult(RESULT_OK, intent); // 设置结果
			finish(); // 结束当前 Activity
			break;
		}
	}

	private void send()
	{
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0)
		{
			sendMessage(contString); // 通过 WebSocket 发送消息

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (webSocket != null) {
			webSocket.close(1000, "Activity销毁关闭连接");
		}
	}

}