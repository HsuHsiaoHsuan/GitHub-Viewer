package idv.hsu.githubviewer.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import idv.hsu.githubviewer.databinding.ItemRepositoryListBinding
import idv.hsu.githubviewer.domain.model.Repository

class RepositoryListAdapter(private val onClick: (Repository) -> Unit) :
    PagingDataAdapter<Repository, RepositoryListAdapter.RepositoryViewHolder>(RepositoryDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepositoryViewHolder {
        val binding =
            ItemRepositoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepositoryViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(
        holder: RepositoryViewHolder,
        position: Int
    ) {
        var repository = getItem(position)
        if (repository != null) {
            holder.bind(repository)
        }
    }

    class RepositoryViewHolder(
        private val binding: ItemRepositoryListBinding,
        val onClick: (Repository) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Repository) {
            binding.root.setOnClickListener {
                onClick(data)
            }
            binding.textRepositoryTitle.text = data.name
            binding.textRepositoryStarCount.text = data.stargazersCount.toString()
            binding.textRepositoryDescription.text = data.description
            binding.textRepositoryLanguage.text = data.language
        }

    }

    object RepositoryDiffCallback : DiffUtil.ItemCallback<Repository>() {
        override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
            return oldItem.id == newItem.id
        }
    }
}