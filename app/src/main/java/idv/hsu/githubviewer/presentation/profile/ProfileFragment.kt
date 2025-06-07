package idv.hsu.githubviewer.presentation.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.githubviewer.R
import idv.hsu.githubviewer.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private val args: ProfileFragmentArgs by navArgs()

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
        binding.imageAvatar.setImageResource(args.avatarPlaceholder)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stateFlow.collect { state ->
                    when (state) {
                        is ProfileListUiState.Idle -> Unit

                        is ProfileListUiState.SuccessUser -> {
                            Log.e("FREEMAN", "SuccessUser")
                            // FIXME 切換深淺佈景主題，文字消失
                            Glide.with(binding.imageAvatar)
                                .load(state.user.avatarUrl)
                                .circleCrop()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(binding.imageAvatar)
                            binding.textName.text = state.user.name
                            binding.textLoginName.text =
                                getString(R.string.text_login_name_format, state.user.login)
                            binding.textFollowersCount.text = state.user.followers.toString()
                            binding.textFollowingCount.text = state.user.following.toString()
                        }

                        is ProfileListUiState.ErrorUser -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.repositories.collectLatest { pagingData ->
                repositoryListAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                repositoryListAdapter.loadStateFlow.collectLatest { loadStates ->
                    binding.progressBar.isVisible =
                        loadStates.refresh is androidx.paging.LoadState.Loading

                    val errorState = loadStates.source.append as? androidx.paging.LoadState.Error
                        ?: loadStates.source.prepend as? androidx.paging.LoadState.Error
                        ?: loadStates.append as? androidx.paging.LoadState.Error
                        ?: loadStates.prepend as? androidx.paging.LoadState.Error
                        ?: loadStates.refresh as? androidx.paging.LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${it.error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        viewModel.sendIntent(ProfileUiIntent.FetchUserById)
        viewModel.sendIntent(ProfileUiIntent.FetchRepository())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRepositoryListAdapter() {
        repositoryListAdapter = RepositoryListAdapter { repository ->
            repository.htmlUrl.let { url ->
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