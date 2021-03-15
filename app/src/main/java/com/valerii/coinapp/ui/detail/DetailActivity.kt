package com.valerii.coinapp.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.valerii.coinapp.model.Currency
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var source: String

    @Inject
    lateinit var detailViewModelFactory: DetailViewModel.AssistedFactory
    val viewModel: DetailViewModel by viewModels {
        DetailViewModel.provideFactory(detailViewModelFactory, source)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = intent.getStringExtra(EXTRA_SOURCE)!!
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    companion object {
        const val EXTRA_SOURCE = "EXTRA_SOURCE"

        fun startActivity(context: Context, currency: Currency) {
            val intent: Intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_SOURCE, currency.name)
            context.startActivity(intent)
        }
    }
}