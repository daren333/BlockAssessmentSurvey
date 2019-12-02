package com.example.blockassessmentsurvey

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock.sleep
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.*
import java.time.LocalDateTime

class SurveyManager : AppCompatActivity() {
    private lateinit var userID: String
    private lateinit var databaseResults: DatabaseReference
    private lateinit var databaseQuestions: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private lateinit var databaseSurveyInfo: DatabaseReference

    private lateinit var blockButton: ImageView
    private lateinit var storesButton: ImageView
    private lateinit var industryButton: ImageView
    private lateinit var physicalDisorderButton: ImageView
    private lateinit var housingButton: ImageView
    private lateinit var servicesButton: ImageView
    private lateinit var publicTransitButton: ImageView
    private lateinit var healthButton: ImageView
    private var mDialog: DialogFragment? = null


    private lateinit var sInfo: MutableMap<String, Survey>
    private lateinit var sQuestions: MutableMap<String, Question>
    private lateinit var firstQuestion: String
    private lateinit var results: MutableMap<String, String>
    private lateinit var surveyStatus: MutableMap<String, Any>
    private lateinit var surveyNumToButton: MutableMap<Int, ImageView>
    private var restarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_manager)

        //get the current users ID
        userID = intent.getStringExtra("UserID")
        Log.i(TAG, "Got userID: $userID")


        //get reference to database
        databaseQuestions = FirebaseDatabase.getInstance().getReference("questions")
        databaseUser = FirebaseDatabase.getInstance().getReference("users").child(userID)
        databaseResults = FirebaseDatabase.getInstance().getReference("results")
        databaseSurveyInfo = FirebaseDatabase.getInstance().getReference("surveys")


        sInfo = HashMap()
        sQuestions = HashMap()
        results = HashMap()
        surveyStatus = HashMap()
        surveyNumToButton = HashMap()

        initializeViews()

        blockButton.setOnClickListener { startSurvey(S4_Q1) }
        surveyNumToButton.put(4, blockButton)

        storesButton.setOnClickListener { startSurvey(S8_Q1) }
        surveyNumToButton.put(8, storesButton)

        industryButton.setOnClickListener { startSurvey(S6_Q1)}
        surveyNumToButton.put(6, industryButton)

        physicalDisorderButton.setOnClickListener { startSurvey(S7_Q1) }
        surveyNumToButton.put(7, physicalDisorderButton)

        housingButton.setOnClickListener { startSurvey(S5_Q1) }
        surveyNumToButton.put(5, housingButton)

        servicesButton.setOnClickListener { startSurvey(S1_Q1) }
        surveyNumToButton.put(1, servicesButton)

        publicTransitButton.setOnClickListener { startSurvey(S3_Q1) }
        surveyNumToButton.put(3, publicTransitButton)

        healthButton.setOnClickListener { startSurvey(S2_Q1) }
        surveyNumToButton.put(2, healthButton)

    }

    private fun startSurvey(firstQ: String){
        if(sQuestions[firstQ] == null){ // error, didn't get the survey questions
            Toast.makeText(applicationContext, "Failed to access survey, please ensure you are connected to the internet and try again later.", Toast.LENGTH_LONG).show()
            return
        }
        //start by asking about GPS info and weather and get the time
        firstQuestion = firstQ
        results = HashMap() // clear the results from previous survey
        val prevSurvey = findPrevious(firstQ)
        if(prevSurvey != null && surveyStatus[prevSurvey] != "done"){
            // Should throw up a notice "hey do you want to continue or start over?"
            mDialog = AlertDialogFragment.newInstance(prevSurvey)
            mDialog!!.show(supportFragmentManager, "AlertDialog")
            //will be done in continue survey
        } else {
            // starting a new survey
            results["startTimestamp"] = LocalDateTime.now().toString() // get day and time
            results["sid"] = databaseResults.push().key.toString()
            val intent = Intent(this@SurveyManager, GpsLocationActivity::class.java)
            startActivityForResult(intent, GPS_RESULT)
        }
    }

    internal fun continueSurvey(cont: Boolean, prevSurvey: String){
        mDialog!!.dismiss()
        if(cont){
            // continuing from previous attempt
            val nextQ = sQuestions[prevSurvey]!!
            results["sid"] = surveyStatus[prevSurvey].toString() //restore the id of the survey we are doing
            databaseUser.child(prevSurvey).removeValue()
            sendQuestion(nextQ)
        } else {
            // starting a new survey
            databaseUser.child(prevSurvey).removeValue() // remove the old survey saved
            results["startTimestamp"] = LocalDateTime.now().toString() // get day and time
            results["sid"] = databaseResults.push().key.toString()
            val intent = Intent(this@SurveyManager, GpsLocationActivity::class.java)
            startActivityForResult(intent, GPS_RESULT)
        }

    }

    private fun findPrevious(firstQ: String): String? {
        for(qid in surveyStatus){
            Log.i(TAG, "findprevious: $qid")
            if(sQuestions[qid.key] != null){
                if(sQuestions[qid.key]!!.surveyNumber == sQuestions[firstQ]!!.surveyNumber){
                    return qid.key
                }
            }
        }
        return null
    }

    private fun sendQuestion(question: Question){
        // Send intent to correct question type along with question
        var intent: Intent? = null
        //logQuestion(question)
        val temp = question.qType
        Log.i(TAG, "Creating intent for $temp")

        if(question.qType == TYPE_CLICKER){
            intent = Intent(this@SurveyManager, ClickerActivity::class.java)
        } else if(question.qType == TYPE_MC || question.qType == TYPE_MC2
                || question.qType == TYPE_MC3){
            intent = Intent(this@SurveyManager, MultipleChoiceActivity::class.java)
        } else if(question.qType == TYPE_MA){
            intent = Intent(this@SurveyManager, MultipleAnswerActivity::class.java)
        } else if(question.qType == TYPE_FILL || question.qType == TYPE_FILL2){
            intent = Intent(this@SurveyManager, fillInTheBlankActivity::class.java)
        } else {
            saveToFire()
            return //error
        }
        // pack intent with necessary data in question
        intent = packQuestionAsExtra(question, intent!!)
        Log.i(TAG, "Sending question")
        results["lastQuestion"] = question.qid
        startActivityForResult(intent, QUESTION_RESULT)
    }

    private fun packQuestionAsExtra(q: Question, intent: Intent): Intent {
        intent.putExtra(QTEXT_STRING, q.qText)
        intent.putExtra(QID_STRING, q.qid)
        intent.putExtra(QANSWER_STRING, q.answer)

        // calculate current survey progress and pack
        if(!q.qid.equals("weather")) {
            val qInfo = q.qid.split(":")
            val snum = qInfo[0]
            val qnum = qInfo[1].toInt() * 100
            val totalQs = sInfo[snum]!!.numberQuestions.toInt()
            val progress = qnum / totalQs
            intent.putExtra(PROGRESS_STRING, progress.toString())
        }
        else{
            val progress = 0
            intent.putExtra(PROGRESS_STRING, progress.toString())
        }

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
        Log.i(TAG, "Got into result, result code $resultCode")
        Log.i(TAG, "Result, request code $requestCode")

        val t = data.toString()
        Log.i(TAG, "Result data is: $t")

        if(resultCode == Activity.RESULT_CANCELED && data != null){
            // back button was pressed, ask previous question
            val backedQ = data.getStringExtra(QID_STRING)
            val currNum = backedQ.split(":")[1]
            val prevNum = currNum.toInt() - 1
            val currSurvey = sQuestions[backedQ]!!.surveyNumber
            Log.i(TAG, "Current question $currNum, previous question $prevNum")
            if(prevNum > 0) {
                // ask the previous question
                val prevID = currSurvey + ":" + prevNum.toString()
                if(sQuestions[prevID] != null){
                    sendQuestion(sQuestions[prevID]!!)
                }
            } else {
                saveToFire()
            }
        }

        else if(resultCode != Activity.RESULT_OK){ // they stopped or pressed back or something went wrong
            // each user has a key value for each survey where key = qid of the question that was
            // left off and the value is the sid of the survey results
            Log.i(TAG, "Unexpected cancel")
            saveToFire()
        }

        // Result for GPS
        else if(resultCode == Activity.RESULT_OK && requestCode == GPS_RESULT) {
            val addr = data!!.getStringExtra(ADDR_STRING)
            Log.i(TAG, "Got into GPS result ok, got $addr")
            results["address"] = addr
            // ask weather question
            val weatherQuestion = Question("weather", "What best describes the current Weather Conditions?",
                    "Good or Fair,Extremely Cold or Extremely Hot,Overcast,Rainy", firstQuestion, "",
                    TYPE_MC, "0", "none", "0")

            sendQuestion(weatherQuestion)

        }

        // Result for Questions
        else if(resultCode == Activity.RESULT_OK && requestCode == QUESTION_RESULT) {
            Log.i(TAG, "Got into question result ok")
            // get answer of question and ask next question
            val answeredQID = data!!.getStringExtra(QID_STRING)
            val answer = data.getStringExtra(QANSWER_STRING)
            // add the answered question to the results
            results[answeredQID] = answer
            val nextQ: String?
            //if it is the weather question
            if (answeredQID == "weather"){
                nextQ = sQuestions[firstQuestion]!!.qid
            } else if (sQuestions[answeredQID]!!.nextSub != "") {
                //if the answered question has subquestions, check and see if conditions met to ask them
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
                postResults(HashMap<String, Any>(results))
                // mark survey that question belongs to as done
                val lastQuestion = results["lastQuestion"]
                if(lastQuestion != null) {
                    databaseUser.child(lastQuestion).setValue("done")
                    surveyStatus[lastQuestion] = "done"
                }
                results = HashMap() // clear results

                for(survey in surveyStatus.keys){
                    if(surveyStatus.get(survey) == "done"){
                        val surveyNum : Int = survey[0].toString().toInt()
                        surveyNumToButton[surveyNum]!!.setImageTintList(ColorStateList.valueOf(getColor(R.color.completed_survey)))
                    }
                }

                // show thank you message
                val intent = Intent(this@SurveyManager, ThankYouActivity::class.java)
                startActivity(intent)

            } else { // ask next question
                val temp = sQuestions[nextQ]?.qText
                Log.i(TAG, "Next question: $temp")

                if (sQuestions[nextQ] != null) {
                    sendQuestion(sQuestions[nextQ]!!)
                }
            }
        }
    }

    private fun saveToFire(){
        val lastQuestion = sQuestions[results["lastQuestion"]]
        val temp = lastQuestion.toString()
        Log.i(TAG, "Last question: $temp")
        if(lastQuestion != null) {
            postResults(HashMap<String, Any>(results))
            Log.i(TAG, "Putting survey status into $userID")
            clearPrevSave(lastQuestion.surveyNumber)
            databaseUser.child(lastQuestion.qid).setValue(results["sid"])
        }
    }

    // remove all entries from users SurveyStatus that are of the same survey
    private fun clearPrevSave(surveyNumber: String){
        for(surveyStat in surveyStatus){
            val temp = surveyStat.toString()
            Log.i(TAG, "checking value $temp")
            val prevSurvey = sQuestions[surveyStat.key]!!.surveyNumber
            if(prevSurvey == surveyNumber){
                databaseUser.child(surveyStat.key).removeValue()
            }
        }
    }

    private fun postResults(toPost: HashMap<String, Any>){
        // get the end timestamp
        toPost["endTimestamp"] = LocalDateTime.now().toString() // get day and time
        toPost["userID"] = userID

        //post results to firebase
        Log.i(TAG, "Putting answers into $userID")
        val temp = toPost.toString()
        Log.i(TAG, "Posting answers: $temp")
        databaseResults.child(toPost["sid"].toString()).updateChildren(toPost)
    }

    private fun initializeViews() {
        blockButton = findViewById(R.id.blockFeaturesView)
        storesButton = findViewById(R.id.storesView)
        industryButton = findViewById(R.id.industryView)
        physicalDisorderButton = findViewById(R.id.physicalDisorderView)
        housingButton = findViewById(R.id.housingView)
        servicesButton = findViewById(R.id.servicesView)
        publicTransitButton = findViewById(R.id.publicTransitView)
        healthButton = findViewById(R.id.healthView)
    }

    override fun onStart() {
        super.onStart()

        // get the users status
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                surveyStatus.clear()
                Log.i(TAG, "Starting get surveyStatus")

                for(post in dataSnapshot.children){
                    val survey = post.key!!
                    val status = post.getValue().toString()
                    //Log.i(TAG, "Got: $survey status is $status")
                    //add it to map
                    surveyStatus[survey] = status
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        // change a state variable in on restart
        if(!restarted){
            // get the survey questions
            databaseQuestions.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    sQuestions.clear()
                    Log.i(TAG, "Starting get questions")

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

            databaseSurveyInfo.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(surveyDataSnapshot: DataSnapshot) {
                    sInfo.clear()
                    Log.i(TAG, "Starting get surveys")

                    for(post in surveyDataSnapshot.children){
                        //get the question
                        val curr = post.toString()
                        //Log.i(TAG, "Got: $curr from database")
                        val survey = post.getValue<Survey>(Survey::class.java)!!
                        //add it to the list
                        sInfo[survey.surveyId] = survey
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    override fun onResume(){
        super.onResume()

        for(survey in surveyStatus.keys){
            if(surveyStatus.get(survey) == "done"){
                val surveyNum : Int = survey[0].toString().toInt()
                surveyNumToButton[surveyNum]!!.setImageTintList(ColorStateList.valueOf(getColor(R.color.completed_survey)))
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        restarted = true
    }

    companion object {
        private const val QID_STRING = "qid"
        private const val QANSWER_STRING = "qanswer"
        private const val PROGRESS_STRING = "progess"
        private const val TYPE_CLICKER = "clicker (default 0)"
        private const val TYPE_MC = "Radio button"
        private const val TYPE_MC2 = "Radio Button"
        private const val TYPE_MC3 = "Radio button (default 0)"
        private const val TYPE_MA = "Multiple choice"
        private const val TYPE_FILL = "fill in"
        private const val TYPE_FILL2 = "Fill in the blank"



        private const val S1_Q1 = "1:1"
        private const val S2_Q1 = "2:1"
        private const val S3_Q1 = "3:1"
        private const val S4_Q1 = "4:1"
        private const val S5_Q1 = "5:1"
        private const val S6_Q1 = "6:1"
        private const val S7_Q1 = "7:1"
        private const val S8_Q1 = "8:1"
        private const val QTEXT_STRING = "questionstring"
        private const val ADDR_STRING = "address"
        private const val GPS_RESULT = 2
        private const val WEATHER_RESULT = 3
        private const val QUESTION_RESULT = 1
        private const val TAG = "BAS-SurveyManager"
    }
}
