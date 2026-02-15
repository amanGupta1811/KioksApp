package com.example.kioks.presentation.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kioks.databinding.FragmentControlPanelBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ControlPanelFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentControlPanelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentControlPanelBinding.inflate(inflater, container, false)

        setupClicks()

        return binding.root
    }

    private fun setupClicks() {

        val activity = requireActivity() as MainActivity

        binding.btnRefresh.setOnClickListener {
            activity.refreshAppFromPanel()
            dismiss()
        }

        binding.btnRestart.setOnClickListener {
            activity.restartAppFromPanel()
            dismiss()
        }

        binding.btnScreenshot.setOnClickListener {
            activity.takeScreenshotFromPanel()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
