package com.example.blockassessmentsurvey

import android.app.AlertDialog
import android.app.Dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment

// Class that creates the AlertDialog
class AlertDialogFragment : DialogFragment() {

    companion object {
        var toContinue = ""

        fun newInstance(prevSurvey: String): AlertDialogFragment {
            toContinue = prevSurvey
            return AlertDialogFragment()
        }
    }

    // Build AlertDialog using AlertDialog.Builder
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            .setMessage("Do you want to continue from previous survey?")

            // User cannot dismiss dialog by hitting back button
            .setCancelable(false)

            // Set up No Button
            .setNegativeButton(
                "No"
            ) { _, _ ->
                (activity as SurveyManager)
                    .continueSurvey(false, toContinue)
            }

            // Set up Yes Button
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                (activity as SurveyManager)
                    .continueSurvey(true, toContinue)
            }.create()
    }
}
