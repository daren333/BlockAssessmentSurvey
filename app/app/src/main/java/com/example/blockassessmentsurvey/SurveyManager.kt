package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class SurveyManager : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var databaseSurveys: DatabaseReference
    private lateinit var survey1Btn: Button
    private lateinit var survey2Btn: Button
    private lateinit var s1Questions: MutableList<String>
    private lateinit var s2Questions: MutableList<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_manager)

        //get reference to database
        database = FirebaseDatabase.getInstance().reference
        databaseSurveys = FirebaseDatabase.getInstance().getReference("Questions")
        s1Questions = ArrayList()
        s2Questions = ArrayList()


        //initialize views
        initializeViews()

        //depending on which button is pushed, query the database to get relevant questions
        survey1Btn.setOnClickListener {
            startSurvey(SURVEY1_STRING)
        }
        survey2Btn.setOnClickListener {
            startSurvey(SURVEY1_STRING)
        }
    }

    private fun startSurvey(survey: String){
        // for each question, send intent to correct question type along with question
        val intent = Intent(this@SurveyManager, HelloActivity::class.java)
        intent.putExtra("text", s1Questions.toString())
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
                s1Questions.clear()
                dataSnapshot.child(SURVEY1_STRING).children.mapNotNullTo(s1Questions) { it.key.toString() }
                s2Questions.clear()
                dataSnapshot.child(SURVEY1_STRING).children.mapNotNullTo(s2Questions) { it.toString() }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        private val SURVEY1_STRING = "Table_1_Public_Service"
        private val SURVEY2_STRING = "survey2"
        private val QUESTION_STRING = "questionstring"
        private val TAG = "BAS-SurveyManager"
    }
}
