package com.example.dronevision.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.yandex_map.YandexMapFragment
import com.example.dronevision.presentation.ui.yandex_map.TechnicViewModel
import com.example.dronevision.presentation.ui.yandex_map.TechnicViewModelFactory
import javax.inject.Inject

open class MyMapFragment: Fragment() {

    protected lateinit var viewModel: TechnicViewModel

    @Inject
    lateinit var viewModelFactory: TechnicViewModelFactory

    protected fun inject(fragment: YandexMapFragment) {
        (requireContext().applicationContext as App).appComponent.inject(fragment)
        initViewModel()
    }

    protected fun inject(fragment: OsmdroidFragment) {
        (requireContext().applicationContext as App).appComponent.inject(fragment)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[TechnicViewModel::class.java]
    }
}
