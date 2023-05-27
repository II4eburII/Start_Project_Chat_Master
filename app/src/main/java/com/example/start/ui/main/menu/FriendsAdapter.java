package com.example.start.ui.main.menu;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.start.data.Friend;
import com.example.start.R;
import com.example.start.databinding.FriendItemBinding;
import com.example.start.databinding.MessageItemBinding;

import io.getstream.avatarview.AvatarShape;
import io.getstream.avatarview.AvatarView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class FriendsAdapter extends RealmRecyclerViewAdapter<Friend, FriendsAdapter.ViewHolder> {

    private final OrderedRealmCollection<Friend> friends;
    private Friend mContextClickOperation;
    private MutableLiveData<Friend> clickFriendItem;

    FriendsAdapter(OrderedRealmCollection<Friend> friends){
        super(friends, true);
        this.friends = friends;
        clickFriendItem = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FriendItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.friend_item, parent, false);

        ViewHolder holder = new ViewHolder(binding);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFriendItem.setValue(friends.get(holder.getAdapterPosition()));
            }
        });

        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(friends.get(position));
    }
    @Override
    public int getItemCount() {
        return friends.size();
    }

    public Friend getContextClickOperation() {
        return mContextClickOperation;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FriendItemBinding binding;
        public ViewHolder(@NonNull FriendItemBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Friend friend){
            binding.setFriend(friend);
        }
    }
    public void addFriend(Friend friend){
        this.friends.add(friend);
        notifyItemInserted(this.friends.size() - 1);//мы добавили элемент на последнее место, поэтому можно не использовать notifyDataSetChanged, который обновляет весь список

    }
    public Friend getItem(int position) {
        return friends.get(position);
    }
    public LiveData<Friend> getClickFriendItem() {
        return clickFriendItem;
    }
}
