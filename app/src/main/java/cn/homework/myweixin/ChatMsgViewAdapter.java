
package cn.homework.myweixin;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;

public class ChatMsgViewAdapter extends BaseAdapter {
	
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	private String currentUserId;
	private String ReceiverName;
	private String ReceiverAvatarUrl;
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<ChatMsgEntity> coll;

    private Context ctx;
    
    private LayoutInflater mInflater;

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll,String currentUserId,String ReceiverName,String receiverAvatarUrl) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
		this.currentUserId = currentUserId;
		this.ReceiverName=ReceiverName;
		this.ReceiverAvatarUrl=receiverAvatarUrl;
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    


	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
	 	ChatMsgEntity entity = coll.get(position);
	 	
	 	if (entity.getSenderId().equals(currentUserId))
	 	{
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}else{
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}
	 	
	}


	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ChatMsgEntity entity = coll.get(position);
//    	boolean isComMsg = entity.getMsgType();
    	int isComMsg=getItemViewType(position);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    		
    	ViewHolder viewHolder = null;
		String avatarUrl;
		String nowName;
		// 确定头像和用户名
		if (isComMsg == IMsgViewType.IMVT_COM_MSG) {
			avatarUrl = "http://10.0.2.2:9090/" + this.ReceiverAvatarUrl;
			nowName = this.ReceiverName;
		} else {
			avatarUrl = "http://10.0.2.2:9090/" + UserInfo.getInstance().getAvatarUrl();
			nowName = UserInfo.getInstance().getUserName();
		}
	    if (convertView == null)
	    {
	    	  if (isComMsg==IMsgViewType.IMVT_COM_MSG)
			  {
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
			  }else{
				  convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);

			  }

	    	  viewHolder = new ViewHolder();
			  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			  viewHolder.ivUserHead = convertView.findViewById(R.id.iv_userhead);
			  viewHolder.msgImage=convertView.findViewById(R.id.iv_chatimage);
			  viewHolder.isComMsg = isComMsg;
			  
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }


		Date timestamp = entity.getTimestamp();

		String formattedDate = sdf.format(timestamp);
		viewHolder.tvSendTime.setText(formattedDate);
	    viewHolder.tvUserName.setText(nowName);
	    if(!entity.getMediaUrl().equals("empty"))
		{
			// 显示图片，隐藏文本
			viewHolder.tvContent.setVisibility(View.GONE);
			viewHolder.msgImage.setVisibility(View.VISIBLE);


			Glide.with(ctx)
					.load(entity.getMediaUrl())  // 从 entity 获取图片 URL
//					.placeholder(R.drawable.placeholder) // 可选占位图
					.into(viewHolder.msgImage);
		}
	    else
		{
			// 显示文本，隐藏图片
			viewHolder.tvContent.setVisibility(View.VISIBLE);
			viewHolder.msgImage.setVisibility(View.GONE);
	    	viewHolder.tvContent.setText(entity.getMessageContent());
		}
		// 使用 Glide 加载头像
		Glide.with(ctx)
				.load(avatarUrl)
				.placeholder(R.drawable.xiaohei)
				.into(viewHolder.ivUserHead);
	    return convertView;
    }


    static class ViewHolder { 
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public ImageView ivUserHead;
        public ImageView msgImage;
        public int isComMsg ;
    }


}
