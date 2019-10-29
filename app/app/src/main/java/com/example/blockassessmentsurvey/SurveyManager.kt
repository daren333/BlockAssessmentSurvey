package com.example.blockassessmentsurvey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SurveyManager : AppCompatActivity() {
    private lateinit var databaseQuestions: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_manager)

        //get survey value from extra
        val intent = intent
        val survey = intent.getStringExtra(SURVEY_STRING)

        //get reference to database
        databaseQuestions = FirebaseDatabase.getInstance().getReference("surveys").child(survey)

    }

    companion object {
        private val SURVEY_STRING = "surveystring"
        private val QUESTION_STRING = "questionstring"
        private val TAG = "BAS-SurveyManager"
    }
}
