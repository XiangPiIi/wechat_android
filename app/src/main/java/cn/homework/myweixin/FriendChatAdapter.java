package cn.homework.myweixin;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FriendChatAdapter extends RecyclerView.Adapter<FriendChatAdapter.ViewHolder> {
    private List<FriendEntity> friendChatList;
    private static final int REQUEST_CODE_CHAT = 1; // 定义请求代码

    public FriendChatAdapter(List<FriendEntity> friendChatList) {
        this.friendChatList = friendChatList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_chat_item, parent, false);
        return new ViewHolder(view);
    }

    // 添加更新数据的方法
    public void updateData(List<FriendEntity> newFriendList) {
        this.friendChatList.clear();
        this.friendChatList.addAll(newFriendList);
//        System.out.println("大小： "+newFriendList.size());
        notifyDataSetChanged(); // 通知适配器数据已更改
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendEntity chat = friendChatList.get(position);
        holder.userName.setText(chat.getFriendName());//userId2作为用户的好友

        holder.lastMessage.setText(chat.getLastMessage()!=null?chat.getLastMessage():"");

        if (chat.getLastTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(chat.getLastTime());
            // 假设你有一个格式化时间的方法，例如 formatTimestamp
            holder.lastTime.setText(formattedDate);
        } else {
            holder.lastTime.setText("无消息");  // 也可以选择不显示，或者显示默认文本
        }
        // 处理头像
        String avatarUrl = "http://10.0.2.2:9090/"+chat.getAvatarUrl();
        if (avatarUrl != null) {
            // 使用图片加载库加载头像（如 Glide 或 Picasso）
            Glide.with(holder.head.getContext()).load(avatarUrl).into(holder.head);

            //暂时默认
//            holder.head.setImageResource(R.drawable.xiaohei);
        } else {
            // 为空时使用默认头像
            holder.head.setImageResource(R.drawable.xiaohei);  // 替换为你项目中的默认头像资源
        }
//        // 使用Glide加载头像
//        Glide.with(holder.itemView.getContext())
//                .load(chat.getAvatarUrl())  // 假设你在FriendEntity中有avatarUrl字段
//                .placeholder(R.drawable.xiaohei)  // 设置默认头像
//                .error(R.drawable.xiaohei)  // 设置加载错误时的默认头像
//                .into(holder.head);  // 将图片加载到ImageView中
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            // 启动聊天界面

            FriendEntity myfriend = friendChatList.get(position);
            String receiverId=myfriend.getFriendId();
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("Sender_ID", chat.getSenderId());
            intent.putExtra("Receiver_ID", receiverId);
            intent.putExtra("ReceiverName",myfriend.getFriendName());
            intent.putExtra("ReceiverAvatur",myfriend.getAvatarUrl());
            ((Activity) v.getContext()).startActivityForResult(intent, REQUEST_CODE_CHAT);
        });
    }

    @Override
    public int getItemCount() {
        return friendChatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView lastMessage;
        public TextView lastTime;
        public ImageView head;  // 添加ImageView字段，用于加载头像
        public ViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.userName);
            lastMessage = view.findViewById(R.id.lastMessage);
            lastTime = view.findViewById(R.id.lastTime);
            head = view.findViewById(R.id.head);  // 初始化头像ImageView
        }
    }
}