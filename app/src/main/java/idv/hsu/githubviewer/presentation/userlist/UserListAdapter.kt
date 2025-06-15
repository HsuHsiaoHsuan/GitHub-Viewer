package idv.hsu.githubviewer.presentation.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import idv.hsu.githubviewer.core.AvatarUtils
import idv.hsu.githubviewer.databinding.ItemUserListBinding
import idv.hsu.githubviewer.domain.model.User

class UserListAdapter(private val onClick: (User, Int) -> Unit) :
    PagingDataAdapter<User, UserListAdapter.UserViewHolder>(UserDiffCallback) {

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
        if (user != null) {
            holder.bind(user)
        }
    }

    class UserViewHolder(
        private val binding: ItemUserListBinding,
        val onClick: (User, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentUser: User? = null
        private var avatarPlaceholder: Int = 0

        init {
            binding.root.setOnClickListener {
                currentUser?.let { user ->
                    onClick(user, avatarPlaceholder)
                }
            }
        }

        fun bind(data: User) {
            currentUser = data
            avatarPlaceholder = AvatarUtils.getRandomAvatarPlaceholder()
            Glide.with(binding.imageAvatar)
                .load(data.avatarUrl)
                .centerCrop()
                .placeholder(avatarPlaceholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageAvatar)
            binding.textUserName.text = data.login
        }
    }
}

object UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}