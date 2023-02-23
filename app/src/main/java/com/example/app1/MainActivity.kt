package com.example.app1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: ItemViewModel by viewModels()

        if (viewModel.fragmentToLoad == 0) {
            val infoFragment = GrabUserInfoFragment()
            loadFragment(infoFragment)
        } else {
            val displayFragment = DisplayUserInfoFragment()
            loadFragment(displayFragment)
        }
    }

    private fun loadFragment(fragment: Fragment): Unit {
        val fTrans = supportFragmentManager.beginTransaction()
        fTrans.replace(R.id.info_fragment_container, fragment, "info_frag")
        fTrans.commit()
    }
}

class ItemViewModel : ViewModel() {
    var thumbnailUrl = ""
    var firstName = ""
    var middleName = ""
    var lastName = ""
    var fragmentToLoad = 0
}

