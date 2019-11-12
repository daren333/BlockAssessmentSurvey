package com.example.blockassessmentsurvey

data class Question (val qid: String = "", val qText: String = "",
                     val answer: String = "", val next: String="",
                     val nextSub: String = "", val qType: String = "",
                     val surveyNumber: String = "", val skipLogic: String = "",
                     val qNum: String = "")