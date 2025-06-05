package idv.hsu.githubviewer.presentation.userlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.githubviewer.databinding.FragmentUserListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserListViewModel by viewModels()

    private lateinit var userListAdapter: UserListAdapter
//    private lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMainRecyclerView()
        setupSearchFunction()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collectLatest { pagingData ->
                userListAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                userListAdapter.loadStateFlow.collectLatest { loadStates ->
                    binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading
//                    binding.retryButton.isVisible = loadStates.refresh is LoadState.Error
                    // 可以在畫面加上 retryButton

                    val errorState = loadStates.source.append as? LoadState.Error
                        ?: loadStates.source.prepend as? LoadState.Error
                        ?: loadStates.append as? LoadState.Error
                        ?: loadStates.prepend as? LoadState.Error
                        ?: loadStates.refresh as? LoadState.Error // 也包含 refresh 錯誤
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

        viewModel.sendIntent(UserListUiIntent.FetchData())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMainRecyclerView() {
        userListAdapter = UserListAdapter { user, avatarPlaceholder ->
            val action = UserListFragmentDirections.actionUserListFragmentToProfileFragment(
                user,
                avatarPlaceholder
            )
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = userListAdapter
        }
        binding.recyclerView.adapter = userListAdapter.withLoadStateFooter(
            footer = UserLoadStateAdapter { userListAdapter.retry() }
        )
    }

    private fun setupSearchFunction() {
        binding.searchView.setupWithSearchBar(binding.searchBar)
    }
}