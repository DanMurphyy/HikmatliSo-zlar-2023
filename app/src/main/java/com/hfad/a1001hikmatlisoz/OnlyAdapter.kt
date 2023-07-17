package com.hfad.a1001hikmatlisoz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.a1001hikmatlisoz.databinding.RowLayoutBinding

class OnlyAdapter : RecyclerView.Adapter<OnlyAdapter.MyViewHolder>() {

    var dataList = emptyList<Quotes>()

    class MyViewHolder(internal val binding: RowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position % dataList.size]
        val binding = holder.binding

        binding.actualQuote.text = currentItem.quote
        binding.author.text = currentItem.author
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    fun getItemPosition(position: Int): Int {
        return position % dataList.size
    }

    fun setData(quoteData: List<Quotes>) {
        this.dataList = quoteData
        notifyDataSetChanged()
    }
}