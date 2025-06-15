package idv.hsu.githubviewer.presentation.userlist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.paging.PagingData
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.githubviewer.databinding.FragmentUserListBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserListViewModel by viewModels()
    private val userListAdapter by lazy {
        UserListAdapter { user, avatarPlaceholder ->
            val action = UserListFragmentDirections.actionUserListFragmentToProfileFragment(
                user,
                avatarPlaceholder
            )
            findNavController().navigate(action)
        }
    }
    private var debounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setupMainRecyclerView()
        startPaging()
        setupSearchFunction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        debounceJob?.cancel()
        _binding = null
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userListAdapter.loadStateFlow.collectLatest { loadStates ->
                    binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading

                    val errorState = loadStates.source.append as? LoadState.Error
                        ?: loadStates.source.prepend as? LoadState.Error
                        ?: loadStates.append as? LoadState.Error
                        ?: loadStates.prepend as? LoadState.Error
                        ?: loadStates.refresh as? LoadState.Error
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

    private fun setupMainRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = userListAdapter
        }
        binding.recyclerView.adapter = userListAdapter.withLoadStateFooter(
            footer = UserLoadStateAdapter { userListAdapter.retry() }
        )
    }

    private fun startPaging() {
        debounceJob?.cancel()
        debounceJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collectLatest { pagingData ->
                userListAdapter.submitData(pagingData)
            }
        }
        viewModel.sendIntent(UserListUiIntent.FetchData())
    }

    private fun setupSearchFunction() {
        binding.textInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString().trim()
                debounceJob?.cancel()
                debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    if (keyword.isEmpty()) {
                        binding.groupSearchEmpty.isVisible = false
                        startPaging()
                    } else {
                        debounceJob?.cancel()
                        val filteredList = userListAdapter.snapshot()
                            .filterNotNull()
                            .filter { it.login.contains(keyword, ignoreCase = true) }

                        binding.groupSearchEmpty.isVisible = filteredList.isEmpty()

                        lifecycleScope.launch {
                            userListAdapter.submitData(PagingData.from(filteredList))
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}