package com.example.dronevision.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dronevision.R
import com.example.dronevision.databinding.BluetoothListItemBinding

class BluetoothRecyclerViewAdapter()
    : RecyclerView.Adapter<BluetoothRecyclerViewAdapter.ItemHolder>() {

    private var items = mutableListOf<BluetoothListItem>()

    fun setData(items: List<BluetoothListItem>){
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view){

        private val binding = BluetoothListItemBinding.bind(view)

        fun setData(item: BluetoothListItem){
            binding.name.text = item.name
            binding.mac.text = item.mac

            binding.cardView.setOnClickListener {
            }
        }

        companion object{
            fun create(parent: ViewGroup): ItemHolder{
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.bluetooth_list_item, parent, false))
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<BluetoothListItem>(){
        override fun areItemsTheSame(
            oldItem: BluetoothListItem,
            newItem: BluetoothListItem
        ): Boolean {
            return oldItem.mac == newItem.mac
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: BluetoothListItem,
            newItem: BluetoothListItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        items.getOrNull(position)?.let {
            holder.setData(it)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}