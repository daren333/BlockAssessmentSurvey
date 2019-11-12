package com.example.blockassessmentsurvey

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup

class LikertActivity : AppCompatActivity() {

//    private var mPriorityRadioGroup: RadioGroup? = null
//
//    private val priority: Priority
//        get() {
//
//            when (mPriorityRadioGroup!!.checkedRadioButtonId) {
//                R.id.lowPriority -> {
//                    return Priority.LOW
//                }
//                R.id.highPriority -> {
//                    return Priority.HIGH
//                }
//                else -> {
//                    return Priority.MED
//                }
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_likert)
    }
}
