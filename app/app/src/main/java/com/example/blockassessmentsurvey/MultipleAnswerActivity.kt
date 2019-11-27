package com.example.blockassessmentsurvey

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


import kotlinx.android.synthetic.main.activity_multiple_answer.*

private lateinit var checkBoxContainer : RadioGroup

class MultipleAnswerActivity : AppCompatActivity() {

    private var questionText: TextView? = null
    private var doneBtn: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_answer)

        val toAsk = intent.getStringExtra(QUESTION_STRING)

        questionText = findViewById(R.id.QuestionText)

        questionText?.text = toAsk

        val questions = intent.getStringExtra(QANSWER_STRING)

        checkBoxContainer = findViewById<RadioGroup>(R.id.radioGroup2)
        //chipContainer.setOrientation(LinearLayout.VERTICAL)

        for (option in questions.split(",")){

            val toAdd = CheckBox(this)
            toAdd.id = View.generateViewId()
            //toAdd.gravity = Gravity.CENTER

            toAdd.text = option
            // Flip isChecked property when
            //toAdd.setOnClickListener { onChipClicked(toAdd) }
            Log.i(TAG,option)



            checkBoxContainer.addView(toAdd)
        }

        doneBtn = findViewById(R.id.done)

        doneBtn?.setOnClickListener{ submit() }



    }

    private fun submit() {

        val answers = mutableListOf<String>()
        //gets the selected radio buttons text
        for (checkBox in checkBoxContainer.iterator()) {
            val checkedAns= findViewById<CheckBox>(checkBox.id)
            if(checkedAns.isChecked()){
                answers.add(checkedAns.text.toString())
            }
        }
        //val ans = findViewById<Chip>(chipContainer.checkedChipId)

        if(answers.isEmpty()) {
            Toast.makeText(applicationContext, "Please select an answer to continue.", Toast.LENGTH_LONG).show()
            return
        }
        else {

            val data = Intent()

            for(ans in answers) {
                // puts the ans text into the intent
                data.putExtra(QANSWER_STRING, ans)

                //put the question id in the intent
                data.putExtra(QID_STRING, intent.getStringExtra(QID_STRING))
                Log.i(TAG, ans)
            }

            setResult(Activity.RESULT_OK, data)
            finish()


            return
        }
    }


    companion object {
        private val TAG = "BAS-MultipleChoice"
        private val QUESTION_STRING = "questionstring"
        private val QANSWER_STRING = "qanswer"
        private val QID_STRING = "qid"

    }

}
