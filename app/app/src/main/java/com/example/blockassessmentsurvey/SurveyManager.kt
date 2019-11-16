package com.example.blockassessmentsurvey

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.time.LocalDateTime

class SurveyManager : AppCompatActivity() {
    private lateinit var userID: String
    private lateinit var database: DatabaseReference
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var databaseUsers: DatabaseReference
    private lateinit var survey1Btn: Button
    private lateinit var survey2Btn: Button
    private lateinit var sQuestions: MutableMap<String, Question>
    private lateinit var firstQuestion: String
    private lateinit var results: MutableMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_manager)

        //get the current users ID
        userID = intent.getStringExtra("UserID")
        Log.i(TAG, "Got userID: $userID")


        //get reference to database
        database = FirebaseDatabase.getInstance().reference
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")
        databaseUsers = FirebaseDatabase.getInstance().getReference("users")

        sQuestions = HashMap()
        results = HashMap()

        initializeViews()

        //depending on which button is pushed, query the database to get relevant questions
        survey1Btn.setOnClickListener {
           startSurvey(S1_Q1)
        }
        survey2Btn.setOnClickListener {
            startSurvey(S2_Q1)
        }
    }

    private fun startSurvey(firstQ: String){
        //start by asking about GPS info and weather and get the time
        firstQuestion = firstQ
        results = HashMap() // clear the results from previous survey
        results["startTimestamp"] = LocalDateTime.now().toString() // get day and time
        val intent = Intent(this@SurveyManager, GpsLocationActivity::class.java)
        startActivityForResult(intent, GPS_RESULT)
    }

    private fun sendQuestion(question: Question){
        // Send intent to correct question type along with question
        var intent: Intent? = null
        //logQuestion(question)
        if(question.qType == TYPE_CLICKER){
            intent = Intent(this@SurveyManager, ClickerActivity::class.java)
        }
        // pack intent with necessary data in question
        intent = packQuestionAsExtra(question, intent!!)
        Log.i(TAG, "Sending question")
        startActivityForResult(intent, 1)
    }

    private fun packQuestionAsExtra(q: Question, intent: Intent): Intent {
        intent.putExtra(QTEXT_STRING, q.qText)
        intent.putExtra(QID_STRING, q.qid)
        intent.putExtra(QANSWER_STRING, q.answer)
        return intent
    }

    // for debugging
    private fun logQuestion(q: Question){
        val qid = q.qid
        val qtype = q.qType
        Log.i(TAG, "qid = $qid")
        Log.i(TAG, "qtype = $qtype")
    }

    // runs when question activity completes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "Got into result")

        if(resultCode != Activity.RESULT_OK) {
            return // there was an error
        }

        // Result for GPS
        else if(resultCode == Activity.RESULT_OK && requestCode == GPS_RESULT) {
            val addr = data!!.getStringExtra(ADDR_STRING)
            Log.i(TAG, "Got into GPS result ok, got $addr")
            results["address"] = addr
            // ask weather question
            // val weatherQuestion = Question("weather", "What best describes the current Weather Conditions?",
            //        "Good/Fair, Extremely Cold or Extremely Hot, Overcast, Rainy", firstQuestion, "",
            //        TYPE_MC, "0", "none", "0")
            //val intent = Intent(this@SurveyManager, )
            val toAsk = sQuestions[firstQuestion]
            if(toAsk == null){
                return //there was an error
            } else {
                sendQuestion(toAsk)
            }
        }

        // Result for Questions
        else if(resultCode == Activity.RESULT_OK && requestCode == QUESTION_RESULT) {
            Log.i(TAG, "Got into question result ok")
            // get answer of question and ask next question
            val answeredQID = data!!.getStringExtra(QID_STRING)
            val answer = data.getStringExtra(QANSWER_STRING)
            // add the answered question to the results
            results[answeredQID] = answer
            var nextQ: String?
            //if the answered question has subquestions, check and see if conditions met to ask them
            if (sQuestions[answeredQID]!!.nextSub != "") {
                //only check for no or null or 0
                if (answer == sQuestions[answeredQID]!!.skipLogic) {
                    nextQ = sQuestions[answeredQID]!!.next
                } else {
                    nextQ = sQuestions[answeredQID]!!.nextSub
                }
            } else {
                nextQ = sQuestions[answeredQID]!!.next
            }


            Log.i(TAG, "Next question is : $nextQ")
            if (nextQ == "done") {
                // get the end timestamp
                results["endTimestamp"] = LocalDateTime.now().toString() // get day and time

                // mark survey that question belongs to as done
                //post results to firebase for this user
                val childUpdates: HashMap<String, Any> = HashMap(results)
                Log.i(TAG, "Putting answers into $userID")
                val temp = childUpdates.toString()
                Log.i(TAG, "Answers: $temp")
                databaseUsers.child(userID).updateChildren(childUpdates)
            } else { // ask next question
                val temp = sQuestions[nextQ]?.qText
                Log.i(TAG, "Next question: $temp")

                if (sQuestions[nextQ] != null) {
                    sendQuestion(sQuestions[nextQ]!!)
                }
            }
        }
    }

    private fun initializeViews(){
        survey1Btn = findViewById(R.id.survey1_button)
        survey2Btn = findViewById(R.id.survey2_button)
    }

    override fun onStart() {
        super.onStart()
        databaseQuestions.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sQuestions.clear()
                Log.i(TAG, "Starting get questions")
                //dataSnapshot.child(SURVEY1_STRING).children.mapNotNullTo(s2Questions) { it.toString() }
                for(post in dataSnapshot.children){
                    //get the question
                    val curr = post.toString()
                    //Log.i(TAG, "Got: $curr from database")
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
        private const val SURVEY1_STRING = "survey1"
        private const val SURVEY2_STRING = "survey2"
        private const val QID_STRING = "qid"
        private const val QANSWER_STRING = "qanswer"
        private const val TYPE_CLICKER = "clicker (default 0)"
        private const val TYPE_MC = "Radio button"
        private const val S1_Q1 = "1:1"
        private const val S2_Q1 = "2:1"
        private const val QTEXT_STRING = "questionstring"
        private const val ADDR_STRING = "address"
        private const val GPS_RESULT = 2
        private const val WEATHER_RESULT = 3
        private const val QUESTION_RESULT = 1
        private const val TAG = "BAS-SurveyManager"
    }
}
