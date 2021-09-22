package com.example.myapplication3.UIelements.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.myapplication3.R

class Menu : Fragment() {

    lateinit var play: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        play = view.findViewById(R.id.play)

        play.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.alpha_one,
                    R.anim.alpha_zero,
                    R.anim.alpha_one,
                    R.anim.alpha_zero
                )
                .replace(R.id.fragments_container, Game(), null)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}