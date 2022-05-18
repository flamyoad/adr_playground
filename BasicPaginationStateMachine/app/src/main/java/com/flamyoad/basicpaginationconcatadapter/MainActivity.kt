package com.flamyoad.basicpaginationconcatadapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.basicpaginationconcatadapter.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity(), NumberAdapter.PaginationListener {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private val numberAdapter: NumberAdapter by lazy { NumberAdapter(this) }

    private val llm by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding.recyclerView) {
            adapter = numberAdapter
            layoutManager = llm
        }
        binding.btnReload.setOnClickListener {
            viewModel.onEvent(MainViewModelEvent.LoadNext)
        }
        bindViewModel()
    }

    private fun bindViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.currentState().collectLatest {
                // wtf is this? lol maybe try Mavericks
                when (it) {
                    is MainViewModelState.Initial -> {
                        binding.progressBar.isVisible = true
                        binding.btnReload.isVisible = false
                    }
                    is MainViewModelState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.btnReload.isVisible = true
                    }
                    is MainViewModelState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.btnReload.isVisible = false
                    }
                    is MainViewModelState.Progress -> {
                        binding.progressBar.isVisible = false
                        binding.btnReload.isVisible = false
                        numberAdapter.submitList(it.items.toList())
                    }
                }
            }
        }
    }

    override fun fetchNextPage() {
        viewModel.onEvent(MainViewModelEvent.LoadNext)
    }

}