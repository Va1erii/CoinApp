package com.valerii.coinapp.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import com.valerii.coinapp.databinding.ActivityMainBinding
import com.valerii.coinapp.extension.visibleIf
import com.valerii.coinapp.ui.adapter.CurrencySpinnerAdapter
import com.valerii.coinapp.ui.adapter.QuotesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val quotesAdapter = QuotesAdapter()
        binding.value.addTextChangedListener {
            it?.toString()?.let(viewModel::updateCurrencyValue)
        }
        binding.quotes.adapter = quotesAdapter
        binding.quotes.addItemDecoration(DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL))
        val spinnerAdapter = CurrencySpinnerAdapter {
            viewModel.selectCurrency(it)
            binding.currencySpinner.onDetachedFromWindow()
        }
        binding.currencySpinner.adapter = spinnerAdapter

        viewModel.defaultValue.observe(this) {
            binding.value.hint = it
        }

        viewModel.selected.observe(this) {
            spinnerAdapter.select(it)
        }

        viewModel.currencies.observe(this) {
            spinnerAdapter.update(it)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.indication.visibleIf(isLoading)
        }
        viewModel.quotes.observe(this) {
            quotesAdapter.update(it)
        }
        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
        lifecycle.addObserver(viewModel)
    }
}