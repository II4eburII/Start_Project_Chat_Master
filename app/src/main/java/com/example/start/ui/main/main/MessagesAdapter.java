package com.example.start.ui.main.main;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.daasuu.bl.ArrowDirection;
import com.example.start.data.Message;
import com.example.start.R;
import com.example.start.databinding.MessageItemBinding;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class MessagesAdapter extends RealmRecyclerViewAdapter<Message, MessagesAdapter.ViewHolder> {

    private final OrderedRealmCollection<Message> messages;
    private AdapterListener messagesListner;
    private Message mContextClickOperation;

    MessagesAdapter(OrderedRealmCollection<Message> messages){
        super(messages, true);
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MessageItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.message_item, parent, false);
        ViewHolder holder = new ViewHolder(MessageItemBinding.inflate(inflater));

        holder.binding.getRoot().setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) -> {
            mContextClickOperation = messages.get(holder.getAdapterPosition());
        });
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public Message getContextClickOperation() {
        return mContextClickOperation;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private final MessageItemBinding binding;
        public ViewHolder(@NonNull MessageItemBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Message message){
            binding.setMsg(message);
            binding.bubbleLayout.setArrowDirection(message.getSenderIsMe()?ArrowDirection.RIGHT:ArrowDirection.LEFT);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 121, 0, "DELETE");
            menu.add(this.getAdapterPosition(), 121, 1, "CHANGE");
        }
    }
    public void addMessage(Message msg){
        this.messages.add(msg);
        notifyItemInserted(this.messages.size() - 1);//мы добавили элемент на последнее место, поэтому можно не использовать notifyDataSetChanged, который обновляет весь список

    }
    public Message getItem(int position) {
        return messages.get(position);
    }
}
