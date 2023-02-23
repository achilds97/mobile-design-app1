package com.example.app1

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels


/**
 * A simple [Fragment] subclass.
 * Use the [DisplayUserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DisplayUserInfoFragment : Fragment() {
    private val viewModel: ItemViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_user_info, container, false)

        val messageTextView: TextView? = view.findViewById(R.id.login_message_tv)
        val imageHolder: ImageView? = view.findViewById<ImageView>(R.id.profile_pic_iv)

        messageTextView!!.setText(viewModel.firstName + " " + viewModel.lastName + " is logged in!")
        val thumbnail = BitmapFactory.decodeFile(viewModel.thumbnailUrl)
        imageHolder!!.setImageBitmap(thumbnail)
        return view
    }
}