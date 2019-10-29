package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class SurveyManager : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var databaseSurveys: DatabaseReference
    private lateinit var survey1Btn: Button
    private lateinit var survey2Btn: Button
    private lateinit var surveyQuestions: MutableList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_manager)

        //get reference to database
        database = FirebaseDatabase.getInstance().reference
        databaseSurveys = FirebaseDatabase.getInstance().getReference("surveys")

        //initialize views
        initializeViews()

        //depending on which button is pushed, query the database to get relevant questions
        survey1Btn.setOnClickListener {
            getSurveyQuestions(SURVEY1_STRING)
        }
        survey2Btn.setOnClickListener {
            getSurveyQuestions(SURVEY2_STRING)
        }
    }

    private fun getSurveyQuestions(survey: String){
        var questions = databaseSurveys.child(survey)
        val intent = Intent(this@SurveyManager, HelloActivity::class.java)
        intent.putExtra("text", questions.child("q1").child("q1a").toString())
        startActivity(intent)
    }

    private fun initializeViews(){
        survey1Btn = findViewById(R.id.survey1_button)
        survey2Btn = findViewById(R.id.survey2_button)
    }

    override fun onStart() {
        super.onStart()
        databaseSurveys.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "Data changed")
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        private val SURVEY1_STRING = "survey1"
        private val SURVEY2_STRING = "survey2"
        private val QUESTION_STRING = "questionstring"
        private val TAG = "BAS-SurveyManager"
    }
}
