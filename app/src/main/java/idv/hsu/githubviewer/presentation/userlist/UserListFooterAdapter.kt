package idv.hsu.githubviewer.presentation.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import idv.hsu.githubviewer.databinding.ItemLoadStateFooterBinding

class UserListFooterAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<UserListFooterAdapter.UserListFooterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): UserListFooterViewHolder {
        val binding =
            ItemLoadStateFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserListFooterViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: UserListFooterViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class UserListFooterViewHolder(private val binding: ItemLoadStateFooterBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.retryButton.isEnabled = loadState !is LoadState.Loading
            binding.errorMsg.isVisible = loadState is LoadState.Error
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage ?: "Unknown Error"
            }
        }
    }
}