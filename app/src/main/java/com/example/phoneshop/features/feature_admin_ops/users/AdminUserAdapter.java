package com.example.phoneshop.features.feature_admin_ops.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.User;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onBlockUnblockClick(User user);
    }

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (userList != null) {
            holder.bind(userList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView tvUsername, tvRules, tvStatus;
        final Button btnBlockUnblock;

        UserViewHolder(View itemView, OnUserActionListener listener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_user_name);
            tvRules = itemView.findViewById(R.id.tv_user_rules);
            tvStatus = itemView.findViewById(R.id.tv_user_status);
            btnBlockUnblock = itemView.findViewById(R.id.btn_block_unblock);

            btnBlockUnblock.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && userList != null && position != RecyclerView.NO_POSITION) {
                    listener.onBlockUnblockClick(userList.get(position));
                }
            });
        }

        public void bind(User user) {
            tvUsername.setText(String.format("Tên: %s", user.getUsername()));
            tvRules.setText(String.format("Vai trò: %s", user.getRules()));

            boolean isBlocked = user.isBlocked();
            if (isBlocked) {
                tvStatus.setText("Trạng thái: Đã bị chặn");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                btnBlockUnblock.setText("BỎ CHẶN");
            } else {
                tvStatus.setText("Trạng thái: Hoạt động");
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                btnBlockUnblock.setText("CHẶN");
            }
        }
    }
}