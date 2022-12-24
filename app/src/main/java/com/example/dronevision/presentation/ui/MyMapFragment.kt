package com.example.dronevision.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.yandex_map.YandexMapFragment
import com.example.dronevision.presentation.ui.yandex_map.TechnicViewModel
import com.example.dronevision.presentation.ui.yandex_map.TechnicViewModelFactory
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

open class MyMapFragment<T>: Fragment() {

    protected lateinit var viewModel: TechnicViewModel
    protected lateinit var databaseRef: DatabaseReference
    protected val listOfTechnic = mutableListOf<T>()

    @Inject
    lateinit var viewModelFactory: TechnicViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database =
            Firebase.database("https://drone-6c66c-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseRef = database.getReference("message")
    }

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
