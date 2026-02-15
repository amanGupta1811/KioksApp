//package com.example.kioks.presentation.main
//
//import android.app.DialogFragment
//import com.example.kioks.R
//
//class ExitDialogFragment : DialogFragment() {
//
//        override fun onStart() {
//            super.onStart()
//            dialog?.window?.attributes?.windowAnimations =
//                R.style.DialogAnimation
//        }
//    }

package com.example.kioks.presentation.main

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ExitDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Exit Application?")
            .setPositiveButton("YES") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("NO", null)
            .create()
    }
}


