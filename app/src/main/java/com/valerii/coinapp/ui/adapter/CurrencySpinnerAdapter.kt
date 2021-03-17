package com.valerii.coinapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.valerii.coinapp.R
import com.valerii.coinapp.databinding.SpinnerItemViewBinding

class CurrencySpinnerAdapter(
    private val onItemSelected: (String) -> Unit,
) : BaseAdapter() {
    private var selected: String = ""
    private val data = ArrayList<String>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.spinner_item_view, parent, false)
        val itemVH: CurrencyItemVH = view.tag as? CurrencyItemVH ?: CurrencyItemVH(view)
        view.tag = itemVH
        itemVH.bind(selected)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.spinner_item_view, parent, false)
        val itemVH: CurrencyDropDownItemVH =
            view.tag as? CurrencyDropDownItemVH ?: CurrencyDropDownItemVH(view)
        view.tag = itemVH
        itemVH.bind(data[position])
        view.setOnClickListener {
            selected = data[position]
            onItemSelected(data[position])
        }
        return view
    }

    override fun getItem(position: Int): Any = data[position]

    override fun getCount(): Int = data.size

    override fun getItemId(position: Int): Long = position.toLong()

    fun select(selected: String) {
        this.selected = selected
        notifyDataSetChanged()
    }

    fun update(newData: List<String>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    private class CurrencyItemVH(itemView: View) {
        private val binding = SpinnerItemViewBinding.bind(itemView)

        fun bind(source: String) {
            binding.spinnertext.text = source
        }
    }

    private class CurrencyDropDownItemVH(itemView: View) {
        private val binding = SpinnerItemViewBinding.bind(itemView)

        fun bind(source: String) {
            binding.spinnertext.text = source
        }
    }
}