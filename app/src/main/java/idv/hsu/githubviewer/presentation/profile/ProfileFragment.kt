package idv.hsu.githubviewer.presentation.profile

import android.os.Bundle
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
import idv.hsu.githubviewer.domain.model.User
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private val args: ProfileFragmentArgs by navArgs()

    private val repositoryListAdapter by lazy {
        RepositoryListAdapter { repository ->
            repository.htmlUrl.let { url ->
                val customTabsIntent =
                    androidx.browser.customtabs.CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(requireContext(), url.toUri())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageAvatar.setImageResource(args.avatarPlaceholder)
        binding.imageAvatarCollapsed.setImageResource(args.avatarPlaceholder)

        observeData()
        setRepositoryListAdapter()
        startFetchData()
        setOnOffsetChangedListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateFlow.collect { state ->
                        when (state) {
                            is ProfileListUiState.Idle -> Unit

                            is ProfileListUiState.SuccessUser -> {
                                updateUserInfo(state.user)
                            }

                            is ProfileListUiState.ErrorUser -> Unit
                        }
                    }
                }

                launch {
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
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.repositories.collectLatest { pagingData ->
                repositoryListAdapter.submitData(pagingData)
            }
        }
    }

    private fun updateUserInfo(user: User) {
        Glide.with(this)
            .load(user.avatarUrl)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageAvatar)
        Glide.with(this)
            .load(user.avatarUrl)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageAvatarCollapsed)
        binding.textName.text = user.name
        binding.textNameCollapsed.text = user.name
        binding.textLoginName.text = getString(R.string.format_login_name, user.login)
        binding.textFollowersCount.text = user.followers.toString()
        binding.textFollowingCount.text = user.following.toString()
    }

    private fun setRepositoryListAdapter() {
        binding.recyclerViewRepositories.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = repositoryListAdapter
        }
    }

    private fun startFetchData() {
        viewModel.sendIntent(ProfileUiIntent.FetchUserById)
        viewModel.sendIntent(ProfileUiIntent.FetchRepository())
    }

    private fun setOnOffsetChangedListener() {
        binding.appBarLayout.addOnOffsetChangedListener { appBar, verticalOffset ->
            val totalScrollRange = appBar.totalScrollRange
            if (totalScrollRange == 0) return@addOnOffsetChangedListener

            val percentage = (kotlin.math.abs(verticalOffset).toFloat() / totalScrollRange)
            val showCollapsedHeader = percentage >= COLLAPSED_HEADER_THRESHOLD
            val expandedComponentAlpha = if (showCollapsedHeader) 0f else 1f - percentage

            if (showCollapsedHeader) {
                if (binding.collapsedProfileHeader.visibility != View.VISIBLE) {
                    binding.collapsedProfileHeader.alpha = 0f
                    binding.collapsedProfileHeader.visibility = View.VISIBLE
                    binding.collapsedProfileHeader.animate().alpha(1f).duration =
                        HEADER_ANIMATION_DURATION
                }
            } else {
                if (binding.collapsedProfileHeader.visibility != View.GONE) {
                    binding.collapsedProfileHeader.animate().alpha(0f).withEndAction {
                        binding.collapsedProfileHeader.visibility = View.GONE
                    }.duration = HEADER_ANIMATION_DURATION
                }
            }

            setComponentAlpha(expandedComponentAlpha.coerceIn(0f, 1f))
        }
    }

    private fun setComponentAlpha(alpha: Float) {
        binding.imageAvatar.alpha = alpha
        binding.textName.alpha = alpha
        binding.textLoginName.alpha = alpha
        binding.layoutUserFollowers.alpha = alpha
        binding.layoutUserFollowing.alpha = alpha
        binding.textRepositoryTitle.alpha = alpha
    }

    companion object {
        private const val COLLAPSED_HEADER_THRESHOLD = 0.8f
        private const val HEADER_ANIMATION_DURATION = 200L
    }
}