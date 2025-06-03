package idv.hsu.githubviewer.ui.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import idv.hsu.githubviewer.databinding.ItemUserListBinding
import idv.hsu.githubviewer.domain.model.User

class UserListAdapter(private val onClick: (User) -> Unit) :
    ListAdapter<User, UserListAdapter.UserViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val binding =
            ItemUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        val user = getItem(position)
        holder.bind(user)
    }

    class UserViewHolder(private val binding: ItemUserListBinding, val onClick: (User) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: User) {
            binding.root.setOnClickListener {
                onClick(data)
            }
            binding.imageAvatar.setImageDrawable(null)
            Glide.with(binding.imageAvatar)
                .load(data.avatarUrl)
                .centerCrop()
                .into(binding.imageAvatar)
            binding.textUserName.text = data.login
        }
    }
}

object UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }
}