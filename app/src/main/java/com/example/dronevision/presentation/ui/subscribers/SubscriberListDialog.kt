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
import javax.inject.Inject


class SubscriberListDialog(private val subscriberCallback: SubscriberListCallback? = null) : DialogFragment() {

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
        adapter = SubscriberRecyclerViewAdapter(subscriberCallback)
        binding.recyclerView.adapter = adapter

        viewModel.getSubscribers()
        viewModel.subscribersLiveData.observe(this, Observer {
            it?.let {
                adapter.setData(it)
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