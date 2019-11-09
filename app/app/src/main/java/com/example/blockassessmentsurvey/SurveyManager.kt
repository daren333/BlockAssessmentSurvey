package com.example.blockassessmentsurvey

import android.app.Activity
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
    private lateinit var sQuestions: MutableMap<String, Question>
    private lateinit var results: MutableMap<String, String>

    private var currQ: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_manager)

        //get reference to database
        database = FirebaseDatabase.getInstance().reference
        databaseSurveys = FirebaseDatabase.getInstance().getReference("questions")
        sQuestions = HashMap()
        results = HashMap()

        initializeViews()

        //depending on which button is pushed, query the database to get relevant questions
        survey1Btn.setOnClickListener {
            startSurvey(SURVEY1_STRING)
        }
        survey2Btn.setOnClickListener {
            startSurvey(SURVEY2_STRING)
        }
    }

    private fun startSurvey(survey: String){
        //depending on what survey they chose, get the first question to ask
        //val temp = sQuestions["1:1"]?.qText
        //Log.i(TAG, "First question for survey 1: $temp")
        var firstQuestion: Question? = null
        if(survey == SURVEY1_STRING){
            firstQuestion = sQuestions[S1_Q1]
        } else if(survey == SURVEY2_STRING){
            firstQuestion = sQuestions[S2_Q1]
        }
        // Send intent to correct question type along with question
        var intent: Intent? = null
        logQuestion(firstQuestion!!)
        if(firstQuestion?.qType == TYPE_CLICKER){
            intent = Intent(this@SurveyManager, CounterActivity::class.java)
        }
        val tosend = firstQuestion?.qText
        Log.i(TAG, "Sending first question: $tosend")
        intent?.putExtra(QUESTION_STRING, firstQuestion?.qText)
        packQuestionAsExtra(firstQuestion!!, intent!!)
        startActivityForResult(intent, 1)
    }

    private fun packQuestionAsExtra(q: Question, intent: Intent){
        intent.putExtra(QTEXT_STRING, q.qText)
        intent.putExtra(QID_STRING, q.qid)
        intent.putExtra(QANSWER_STRING, q.answer)
    }

    private fun logQuestion(q: Question){
        val qid = q.qid
        val qtype = q.qType
        Log.i(TAG, "qid = $qid")
        Log.i(TAG, "qtype = $qtype")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "Got into result")
        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            Log.i(TAG, "Got into activity result ok")
        }
        // get answer of question and ask next question
        val answeredQID = data!!.getStringExtra(QID_STRING)
        val answer = data.getStringExtra(QANSWER_STRING)
        // add the answered question to the results
        results[answeredQID] = answer
        val nextQ: String? = null
        //if the answered question has subquestions, check and see if conditions met to ask them
        if(sQuestions[answeredQID]!!.nextSub != "none"){
            //only check for no or null or 0
            //if(answer == sQuestions[answeredQ]!!.subAnswer)
            //nextQ = next subquestions
        }

        //if(nextQ == end of survey)
        //find which survey the question belongs to, mark survey as finished
        //post results to firebase for this user

        //else get intent ready to ask next question
        var intent: Intent? = null
        if(sQuestions[nextQ]!!.qType == TYPE_CLICKER){
            intent = Intent(this@SurveyManager, HelloActivity::class.java)
        }
        // else if type == multiple choice
        val tosend = sQuestions[nextQ]!!.qText
        Log.i(TAG, "Sending first question: $tosend")
        intent!!.putExtra("text", sQuestions[nextQ]!!.qText)
        startActivityForResult(intent, 1)
    }

    private fun initializeViews(){
        survey1Btn = findViewById(R.id.survey1_button)
        survey2Btn = findViewById(R.id.survey2_button)
    }

    override fun onStart() {
        super.onStart()
        databaseSurveys.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sQuestions.clear()
                Log.i(TAG, "Starting get questions")
                //dataSnapshot.child(SURVEY1_STRING).children.mapNotNullTo(s2Questions) { it.toString() }
                for(post in dataSnapshot.children){
                    //get the question
                    val curr = post.toString()
                    Log.i(TAG, "Got: $curr from database")
                    val question = post.getValue<Question>(Question::class.java)!!
                    //add it to the list
                    sQuestions[question.qid] = question
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        private val SURVEY1_STRING = "survey1"
        private val SURVEY2_STRING = "survey1"
        private val QTEXT_STRING = "qtext"
        private val QID_STRING = "qid"
        private val QANSWER_STRING = "qanswer"
        private val QNEXTQ_STRING = "qnextq"
        private val TYPE_CLICKER = "clicker (default 0)"
        private val S1_Q1 = "1:1"
        private val S2_Q1 = "2:1"
        private val QUESTION_STRING = "questionstring"
        private val TAG = "BAS-SurveyManager"
    }
}
