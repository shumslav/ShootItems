package com.example.myapplication3.UIelements.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication3.R
import com.example.myapplication3.UIelements.Fragments.Game
import com.example.myapplication3.UIelements.Fragments.Menu

class Main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.alpha_one,
                R.anim.alpha_zero,
                R.anim.alpha_one,
                R.anim.alpha_zero
            )
            .add(R.id.fragments_container,Menu(),null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount==0)
            super.onBackPressed()
        else
            supportFragmentManager.popBackStack()
    }
}