package com.vtb.hackaton.ui.followed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vtb.hackaton.databinding.FragmentFollowedBinding

class FollowedFragment : Fragment() {

    private var _binding: FragmentFollowedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(FollowedViewModel::class.java)

        _binding = FragmentFollowedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFollowed
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}