package com.valerii.coinapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valerii.coinapp.R
import com.valerii.coinapp.databinding.QuoteItemViewBinding
import com.valerii.coinapp.model.Quote

class QuotesAdapter : RecyclerView.Adapter<QuotesAdapter.QuoteVH>() {
    private val data = ArrayList<Quote>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.quote_item_view, parent, false)
        return QuoteVH(view)
    }

    override fun onBindViewHolder(holder: QuoteVH, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun update(newData: List<Quote>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    class QuoteVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = QuoteItemViewBinding.bind(itemView)

        fun bind(quote: Quote) {
            binding.currencyName.text = quote.quote
            binding.currencyValue.text = quote.rate
        }
    }
}