package com.example.dronevision.presentation.ui.subscribers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dronevision.domain.use_cases.GetSubscribersUseCase
import com.example.dronevision.domain.use_cases.SaveSubscriberUseCase
import com.example.dronevision.presentation.mapper.SubscribersMapperUI
import com.example.dronevision.presentation.model.Subscriber
import com.example.dronevision.utils.FindTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubscriberViewModel(
    private val saveSubscriberUseCase: SaveSubscriberUseCase,
    private val getSubscribersUseCase: GetSubscribersUseCase
): ViewModel() {

    private val _subscribersLiveData: MutableLiveData<List<Subscriber>> = MutableLiveData()
    val subscribersLiveData: MutableLiveData<List<Subscriber>> get() = _subscribersLiveData

    fun saveSubscriber(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val sub = SubscribersMapperUI.mapSubscriberUItoDTO(subscriber)
        saveSubscriberUseCase.execute(sub)
    }

    fun getSubscribers() = viewModelScope.launch(Dispatchers.IO) {
        val subs = SubscribersMapperUI.mapSubscribersDTOtoSubscribersUI(getSubscribersUseCase.execute())
        _subscribersLiveData.postValue(subs)
    }
}