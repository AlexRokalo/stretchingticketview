package com.itexus.stretchingticketview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readMore.text = "Stretching properly is a little more technical than just swinging your leg over a park bench. " +
                "There are methods and techniques that will maximize the benefits and minimize the risk of injury.\n" +
                "\n" +
                "In this article we’ll look at some of the most common questions people ask about how to stretch properly. " +
                "Questions like: What is flexibility and what is stretching? Which muscles should I stretch? When should I" +
                " stretch? Should I stretch every day? Plus a whole lot more"
    }
}
