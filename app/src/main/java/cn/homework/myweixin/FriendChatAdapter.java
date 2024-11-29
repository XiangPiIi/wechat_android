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
    private static final int REQUEST_CODE_CHAT = 1; // �����������

    public FriendChatAdapter(List<FriendEntity> friendChatList) {
        this.friendChatList = friendChatList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_chat_item, parent, false);
        return new ViewHolder(view);
    }

    // ��Ӹ������ݵķ���
    public void updateData(List<FriendEntity> newFriendList) {
        this.friendChatList.clear();
        this.friendChatList.addAll(newFriendList);
//        System.out.println("��С�� "+newFriendList.size());
        notifyDataSetChanged(); // ֪ͨ�����������Ѹ���
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendEntity chat = friendChatList.get(position);
        holder.userName.setText(chat.getFriendName());//userId2��Ϊ�û��ĺ���

        holder.lastMessage.setText(chat.getLastMessage()!=null?chat.getLastMessage():"");

        if (chat.getLastTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(chat.getLastTime());
            // ��������һ����ʽ��ʱ��ķ��������� formatTimestamp
            holder.lastTime.setText(formattedDate);
        } else {
            holder.lastTime.setText("����Ϣ");  // Ҳ����ѡ����ʾ��������ʾĬ���ı�
        }
        // ����ͷ��
        String avatarUrl = "http://10.0.2.2:9090/"+chat.getAvatarUrl();
        if (avatarUrl != null) {
            // ʹ��ͼƬ���ؿ����ͷ���� Glide �� Picasso��
            Glide.with(holder.head.getContext()).load(avatarUrl).into(holder.head);

            //��ʱĬ��
//            holder.head.setImageResource(R.drawable.xiaohei);
        } else {
            // Ϊ��ʱʹ��Ĭ��ͷ��
            holder.head.setImageResource(R.drawable.xiaohei);  // �滻Ϊ����Ŀ�е�Ĭ��ͷ����Դ
        }
//        // ʹ��Glide����ͷ��
//        Glide.with(holder.itemView.getContext())
//                .load(chat.getAvatarUrl())  // ��������FriendEntity����avatarUrl�ֶ�
//                .placeholder(R.drawable.xiaohei)  // ����Ĭ��ͷ��
//                .error(R.drawable.xiaohei)  // ���ü��ش���ʱ��Ĭ��ͷ��
//                .into(holder.head);  // ��ͼƬ���ص�ImageView��
        // ���õ���¼�
        holder.itemView.setOnClickListener(v -> {
            // �����������

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
        public ImageView head;  // ���ImageView�ֶΣ����ڼ���ͷ��
        public ViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.userName);
            lastMessage = view.findViewById(R.id.lastMessage);
            lastTime = view.findViewById(R.id.lastTime);
            head = view.findViewById(R.id.head);  // ��ʼ��ͷ��ImageView
        }
    }
}