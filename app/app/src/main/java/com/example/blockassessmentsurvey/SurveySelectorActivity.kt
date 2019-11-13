package com.example.blockassessmentsurvey

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_survey_selector.*

class SurveySelectorActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_selector)

        val blockButton: ImageView = findViewById(R.id.blockFeaturesView)
        val storesButton: ImageView = findViewById(R.id.storesView)
        val industryButton: ImageView = findViewById(R.id.industryView)
        val physicalDisorderButton: ImageView = findViewById(R.id.physicalDisorderView)
        val housingButton: ImageView = findViewById(R.id.housingView)
        val servicesButton: ImageView = findViewById(R.id.servicesView)
        val publicTransitButton: ImageView = findViewById(R.id.publicTransitView)
        val healthButton: ImageView = findViewById(R.id.healthView)

        blockButton!!.setOnClickListener {
            val intent = Intent(this@SurveySelectorActivity, HelloActivity::class.java)
                    .putExtra("surveyType", "Block Features Survey")
            startActivity(intent)
        }

        storesButton!!.setOnClickListener {
            val intent = Intent(this@SurveySelectorActivity, HelloActivity::class.java)
                    .putExtra("surveyType", "Stores Survey")
            startActivity(intent)
        }

        industryButton!!.setOnClickListener {
            val intent = Intent(this@SurveySelectorActivity, HelloActivity::class.java)
                    .putExtra("surveyType", "Industry Survey")
            startActivity(intent)
        }

        physicalDisorderButton!!.setOnClickListener {
            val intent = Intent(this@SurveySelectorActivity, HelloActivity::class.java)
                    .putExtra("surveyType", "Physical Disorder Survey")
            startActivity(intent)
        }

        housingButton!!.setOnClickListener {
            val intent = Intent(this@SurveySelectorActivity, HelloActivity::class.java)
                    .putExtra("surveyType", "Housing Survey")
            startActivity(intent)
        }

        servicesButton!!.setOnClickListener {
            val intent = Intent(this@SurveySelectorActivity, HelloActivity::class.java)
                    .putExtra("surveyType", "Services Survey")
            startActivity(intent)
        }

        publicTransitButton!!.setOnClickListener {
            val intent = Intent(this@SurveySelectorActivity, HelloActivity::class.java)
                    .putExtra("surveyType", "Public Transit Survey")
            startActivity(intent)
        }

        healthButton!!.setOnClickListener {
            val intent = Intent(this@SurveySelectorActivity, HelloActivity::class.java)
                    .putExtra("surveyType", "Health Survey")
            startActivity(intent)
        }

        /*setSupportActionBar(toolbar)


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
    }

}
