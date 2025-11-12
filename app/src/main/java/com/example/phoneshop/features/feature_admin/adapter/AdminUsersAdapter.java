package com.example.phoneshop.features.feature_admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phoneshop.R;
import com.example.phoneshop.data.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying users in admin user management
 */
public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.UserViewHolder> {

    private List<User> users;
    private OnUserActionListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnUserActionListener {
        void onUserClick(User user);
        void onDeleteUserClick(User user);
        void onViewOrdersClick(User user);
    }

    public AdminUsersAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public void updateUsers(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    public void addUsers(List<User> newUsers) {
        if (this.users != null && newUsers != null) {
            int startPosition = this.users.size();
            this.users.addAll(newUsers);
            notifyItemRangeInserted(startPosition, newUsers.size());
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardUser;
        private ImageView ivUserAvatar;
        private TextView tvUserId;
        private TextView tvUserName;
        private TextView tvUserEmail;
        private TextView tvUserUsername;
        private TextView tvUserCreatedAt;
        private TextView tvUserStatus;
        private MaterialButton btnViewOrders;
        private MaterialButton btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardUser = itemView.findViewById(R.id.cardUser);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserUsername = itemView.findViewById(R.id.tvUserUsername);
            tvUserCreatedAt = itemView.findViewById(R.id.tvUserCreatedAt);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            btnViewOrders = itemView.findViewById(R.id.btnViewOrders);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);

            setupClickListeners();
        }

        private void setupClickListeners() {
            cardUser.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onUserClick(users.get(getAdapterPosition()));
                }
            });

            btnViewOrders.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onViewOrdersClick(users.get(getAdapterPosition()));
                }
            });

            btnDeleteUser.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDeleteUserClick(users.get(getAdapterPosition()));
                }
            });
        }

        public void bind(User user) {
            tvUserId.setText("ID: " + user.getId());
            tvUserName.setText(user.getFullName());
            tvUserEmail.setText(user.getEmail());
            tvUserUsername.setText("@" + user.getUsername());
            
            // Format created date
            try {
                String formattedDate = formatDate(user.getCreatedAt());
                tvUserCreatedAt.setText("Tham gia: " + formattedDate);
            } catch (Exception e) {
                tvUserCreatedAt.setText("Tham gia: " + user.getCreatedAt());
            }
            
            // Set status
            boolean isActive = user.isActive();
            tvUserStatus.setText(isActive ? "Hoạt động" : "Không hoạt động");
            setStatusColor(isActive);
            
            // Set avatar (placeholder)
            setUserAvatar(user);
            
            // Update button states
            updateButtonStates(user);
        }

        private String formatDate(String dateString) {
            try {
                // Try to parse as timestamp first
                long timestamp = Long.parseLong(dateString);
                Date date = new Date(timestamp);
                return dateFormat.format(date);
            } catch (NumberFormatException e) {
                try {
                    // Try to parse as ISO date
                    Date date = new Date(dateString);
                    return dateFormat.format(date);
                } catch (Exception ex) {
                    return dateString;
                }
            }
        }

        private void setStatusColor(boolean isActive) {
            int colorResId = isActive ? R.color.status_completed : R.color.status_cancelled;
            try {
                int color = itemView.getContext().getResources().getColor(colorResId, null);
                tvUserStatus.setTextColor(color);
            } catch (Exception e) {
                // Fallback to default colors
            }
        }

        private void setUserAvatar(User user) {
            // Set a placeholder avatar based on first letter of name
            String firstLetter = user.getFullName().substring(0, 1).toUpperCase();
            // For now, just use a default avatar
            // You can implement letter-based avatar generation here
            ivUserAvatar.setImageResource(R.drawable.ic_person);
        }

        private void updateButtonStates(User user) {
            // Enable/disable buttons based on user status
            boolean isActive = user.isActive();
            
            btnViewOrders.setEnabled(true);
            btnDeleteUser.setEnabled(isActive);
            
            // Update delete button text
            btnDeleteUser.setText(isActive ? "Vô hiệu hóa" : "Đã vô hiệu hóa");
        }
    }
}
