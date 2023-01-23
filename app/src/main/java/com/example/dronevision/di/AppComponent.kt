package com.example.dronevision.di

import com.example.dronevision.presentation.ui.MainActivity
import com.example.dronevision.presentation.ui.subscribers.SubscriberDialogFragment
import com.example.dronevision.presentation.ui.subscribers.SubscriberListDialog
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.targ.TargetFragment
import dagger.Component

@Component(modules = [AppModule::class, DataModule::class, DomainModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(osmdroidFragment: OsmdroidFragment)
    fun inject(subscriberDialogFragment: SubscriberDialogFragment)
    fun inject(subscriberListDialog: SubscriberListDialog)
    fun inject(targetFragment: TargetFragment)
}