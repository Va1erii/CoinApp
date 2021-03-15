package com.valerii.coinapp.ui.main

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.valerii.coinapp.R
import com.valerii.coinapp.extension.visibleIf
import com.valerii.coinapp.ui.adapter.QuotesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner = findViewById<AppCompatSpinner>(R.id.currency_spinner)
        val indication = findViewById<View>(R.id.indication)
        val value: EditText = findViewById(R.id.value)
        value.addTextChangedListener {
            it?.toString()?.let(viewModel::updateCurrencyValue)
        }

        val rates: RecyclerView = findViewById(R.id.rates)
        val adapter = QuotesAdapter()
        rates.adapter = adapter

        viewModel.currencies.observe(this) {
            val spinnerAdapter = ArrayAdapter(
                this,
                R.layout.spinner_item_view,
                it
            )
            spinner.adapter = spinnerAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    spinnerAdapter.getItem(position)?.let {
                        viewModel.selectCurrency(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            indication.visibleIf(isLoading)
        }
        viewModel.quotes.observe(this) {
            adapter.update(it)
        }
        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
        lifecycle.addObserver(viewModel)
    }
}