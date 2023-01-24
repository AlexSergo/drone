package com.example.dronevision.presentation.ui.subscribers

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dronevision.R
import com.example.dronevision.databinding.SubscriberListItemBinding
import com.example.dronevision.presentation.model.Subscriber

class SubscriberRecyclerViewAdapter(private val callback: SubscriberListCallback? = null)
    : RecyclerView.Adapter<SubscriberRecyclerViewAdapter.ItemHolder>() {

    private var subscribers = mutableListOf<Subscriber>()

    fun setData(subscribers: List<Subscriber>){
        this.subscribers = subscribers.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        subscribers.getOrNull(position)?.let {
            holder.setData(it, callback)
        }
    }

    override fun getItemCount(): Int {
        return subscribers.size
    }

    class ItemHolder(view: View): RecyclerView.ViewHolder(view){

        private val binding = SubscriberListItemBinding.bind(view)

        fun setData(subscriber: Subscriber, callback: SubscriberListCallback?){
            binding.subscriberName.text = subscriber.name
            binding.subscriberAddress.text = subscriber.id
            binding.item.setOnClickListener {
                callback?.select(subscriber)
            }
        }

        companion object{
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.subscriber_list_item, parent, false)
                )
            }
        }
    }

}