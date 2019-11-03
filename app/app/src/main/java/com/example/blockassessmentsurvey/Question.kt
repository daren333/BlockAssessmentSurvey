package com.example.blockassessmentsurvey

data class Question (val id: String ="", val qText: String = "",
                     val answer: String = "", val next: String="",
                     val nextSub: String = "", val qType: String = "")