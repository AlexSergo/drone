package com.example.dronevision.presentation.ui.subscribers

import com.example.dronevision.presentation.model.Subscriber

interface SubscriberListCallback {
    fun select(subscriber: Subscriber)
}