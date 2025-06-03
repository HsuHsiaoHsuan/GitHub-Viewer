package idv.hsu.githubviewer.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.githubviewer.R
import idv.hsu.githubviewer.core.AvatarUtils
import idv.hsu.githubviewer.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var repositoryListAdapter: RepositoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRepositoryListAdapter()
        binding.imageAvatar.setImageResource(AvatarUtils.getRandomAvatarPlaceholder())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stateFlow.collect { state ->
                    when (state) {
                        is ProfileListUiState.Idle -> Unit

                        is ProfileListUiState.LoadingRepository -> {
                            binding.progressBar.isVisible = true
                        }

                        is ProfileListUiState.SuccessUser -> {
                            Log.e("FREEMAN", "SuccessUser")
                            // FIXME: 切換深淺佈景主題，文字消失
                            Glide.with(binding.imageAvatar)
                                .load(state.user.avatarUrl)
                                .circleCrop()
                                .into(binding.imageAvatar)
                            binding.textName.text = state.user.name
                            binding.textLoginName.text =
                                getString(R.string.text_login_name_format, state.user.login)
                            binding.textFollowersCount.text = state.user.followers.toString()
                            binding.textFollowingCount.text = state.user.following.toString()
                        }

                        is ProfileListUiState.SuccessRepository -> {
                            Log.e("FREEMAN", "SuccessRepository")
                            binding.progressBar.isVisible = false
                            repositoryListAdapter.submitList(state.repositories)
                        }

                        is ProfileListUiState.ErrorUser -> Unit

                        is ProfileListUiState.ErrorRepository -> {
                            Log.e("FREEMAN", "ErrorRepository")
                            binding.progressBar.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun setRepositoryListAdapter() {
        repositoryListAdapter = RepositoryListAdapter { repository ->
            repository.htmlUrl.let { url ->
                // FIXME: 會自動打開 Chrome
                val customTabsIntent =
                    androidx.browser.customtabs.CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(requireContext(), url.toUri())
            }
        }
        binding.recyclerViewRepositories.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = repositoryListAdapter
        }
    }
}