package com.example.blockassessmentsurvey

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ClickerActivity : AppCompatActivity() {

    private var questionText: TextView? = null
    private var countText: EditText? = null
    private var addCount: ImageButton? = null
    private var removeCount: ImageButton? = null
    private var doneBtn: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clicker)

        questionText = findViewById(R.id.holder)
        val temp = intent.getStringExtra(QUESTION_STRING)
        Log.i(TAG, "Got question: $temp")
        questionText?.text = intent.getStringExtra(QUESTION_STRING)

        countText = findViewById(R.id.counter)

        addCount = findViewById(R.id.add)
        removeCount = findViewById(R.id.remove)

        doneBtn = findViewById(R.id.done)
        addCount?.setOnClickListener {
            val txt = countText?.text.toString()

            if(txt.compareTo("") == 0){
                countText?.setText(1.toString())
            }
            else {

                var cnt = 0

                try {
                    cnt = txt.toInt()
                } catch(e: NumberFormatException){
                    cnt = 0
                }
                cnt += 1
                countText?.setText(cnt.toString())
            }
        }

        removeCount?.setOnClickListener {
            val txt = countText?.text.toString()
            if(txt.compareTo("") == 0){
                countText?.setText("0")
            }
            else {
                var cnt = 0

                try {
                    cnt = txt.toInt()
                } catch(e: NumberFormatException){
                    cnt = 0
                }

                if(cnt <= 0){
                    cnt = 0
                }

                else {
                    cnt -= 1
                }
                countText?.setText(cnt.toString())
            }
        }

        //When the user clicks on the done button returns to survey manager
        doneBtn?.setOnClickListener {
            val data = Intent()

            // puts the counter in the intent
            var ans = 0

            //Checks to make sure a number was inputted
            try {
                ans = countText!!.text.toString().toInt()
            }catch(e: java.lang.NumberFormatException){
                val t = Toast.makeText(this,"Please put a number", Toast.LENGTH_LONG)
                t. show()
                countText?.setText("")
                return@setOnClickListener
            }

            //checks if the answer was bigger non negative
            if(ans < 0){
                val t = Toast.makeText(this,"Please put a number 0 or bigger", Toast.LENGTH_LONG)
                t. show()
                countText?.setText("")
                return@setOnClickListener
            }



//            data.putExtra(QANSWER_STRING,countText?.text.toString())
            data.putExtra(QANSWER_STRING,ans.toString())

            //put the question id in the intent
            data.putExtra(QID_STRING, intent.getStringExtra(QID_STRING))

            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    companion object {
        private val TAG = "BAS-Clicker"
        private val QUESTION_STRING = "questionstring"
        private val QANSWER_STRING = "qanswer"
        private val QID_STRING = "qid"
    }
}
