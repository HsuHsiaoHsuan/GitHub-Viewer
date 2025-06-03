package idv.hsu.githubviewer.ui.userlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.githubviewer.databinding.FragmentUserListBinding
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stateFlow.collect { state ->
                    when (state) {
                        is UserListUiState.Idle -> {
                            binding.progressBar.isVisible = false
                        }

                        is UserListUiState.Loading -> {
                            binding.progressBar.isVisible = true
                        }

                        is UserListUiState.Success -> {
                            binding.progressBar.isVisible = false
                            userListAdapter.submitList(state.users)
                        }

                        is UserListUiState.Error -> {
                            binding.progressBar.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun setupMainRecyclerView() {
        userListAdapter = UserListAdapter { user ->
            val action = UserListFragmentDirections.actionUserListFragmentToProfileFragment(user)
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = userListAdapter
        }
    }

    private fun setupSearchFunction() {
        binding.searchView.setupWithSearchBar(binding.searchBar)
    }
}