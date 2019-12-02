package com.example.blockassessmentsurvey

import android.app.Activity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThankYouActivity : AppCompatActivity() {

    private var message: TextView? = null
    private var doneBtn: ImageButton? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)

        message = findViewById(R.id.QuestionText)
        doneBtn = findViewById(R.id.submit)

        //Sets the Question to be passed in
        message?.text = MESSAGE_STRING


        //returns the response from the Edit Text
        doneBtn?.setOnClickListener({

            setResult(Activity.RESULT_OK)
            finish()

        })
    }


    companion object {
        private val MESSAGE_STRING = "Thank you for using the Community Block Assessment Tool. Your responses have been recorded."
    }
}
