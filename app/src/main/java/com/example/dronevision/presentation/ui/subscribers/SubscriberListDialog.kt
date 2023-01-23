package com.example.dronevision.presentation.ui.subscribers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.databinding.FragmentSubscriberListDialogBinding
import com.example.dronevision.presentation.model.Subscriber
import javax.inject.Inject


class SubscriberListDialog(private val subscriberCallback: SubscriberListCallback? = null,
                           private val subscribersType: SubscribersType) : DialogFragment() {

    private lateinit var binding: FragmentSubscriberListDialogBinding
    private lateinit var viewModel: SubscriberViewModel
    private lateinit var adapter: SubscriberRecyclerViewAdapter

    @Inject
    lateinit var viewModelFactory: SubscriberViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setLayout(500, 300)

        initModelView()
        binding = FragmentSubscriberListDialogBinding.inflate(layoutInflater)
        adapter = SubscriberRecyclerViewAdapter(object : SubscriberListCallback{
            override fun select(subscriber: Subscriber) {
                subscriberCallback?.select(subscriber)
                dialog?.dismiss()
            }

        })
        binding.recyclerView.adapter = adapter

        viewModel.getSubscribers()
        viewModel.subscribersLiveData.observe(this, Observer {
            it?.let {subs ->
                val result = mutableListOf<Subscriber>()
                subs.forEach { sub ->
                    when(subscribersType){
                        SubscribersType.Internet -> {
                            if (sub.id != "")
                                result.add(sub)
                        }
                        SubscribersType.Radio ->{
                            if (sub.IP != "")
                                result.add(sub)
                        }
                        else -> {
                            result.add(sub)
                        }
                    }
                }
                adapter.setData(result)
            }
        })
        return binding.root
    }

    private fun initModelView() {
        (requireContext().applicationContext as App).appComponent.inject(this)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[SubscriberViewModel::class.java]
    }

}