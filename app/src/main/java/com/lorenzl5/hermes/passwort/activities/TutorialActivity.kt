package com.lorenzl5.hermes.passwort.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lorenzl5.hermes.passwort.R
import com.lorenzl5.hermes.passwort.helpers.PreferenceKeys

class TutorialActivity : AppCompatActivity() {

    private val titles = arrayOf(
        R.string.tutorial_slide_1_title,
        R.string.tutorial_slide_2_title,
        R.string.tutorial_slide_3_title
    )
    private val bodies = arrayOf(
        R.string.tutorial_slide_1_body,
        R.string.tutorial_slide_2_body,
        R.string.tutorial_slide_3_body
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        supportActionBar?.hide()

        findViewById<ViewPager>(R.id.view_pager).adapter = SlideAdapter()
    }

    private fun finishTutorial() {
        // Mark the welcome tutorial as done, but keep the main tutorial
        // re-openable from the navigation drawer without resetting the flag.
        PreferenceKeys.setTutorialDone(this, true)
        startActivity(android.content.Intent(this, MasterPWTutorialActivity::class.java))
        finish()
    }

    inner class SlideAdapter : PagerAdapter() {
        override fun getCount(): Int = titles.size
        override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(container.context)
                .inflate(R.layout.tutorial_slide, container, false)
            view.findViewById<android.widget.TextView>(R.id.tutorialTitle)
                .setText(titles[position])
            view.findViewById<android.widget.TextView>(R.id.tutorialBody)
                .setText(bodies[position])

            // Dots
            val dotsLayout = view.findViewById<android.widget.LinearLayout>(R.id.layoutDots)
            dotsLayout.removeAllViews()
            for (i in 0 until titles.size) {
                val dot = android.widget.TextView(container.context)
                dot.text = "•"
                dot.textSize = 20f
                dot.setTextColor(
                    if (i == position)
                        android.graphics.Color.parseColor("#1976D2")
                    else
                        android.graphics.Color.parseColor("#BDBDBD")
                )
                dotsLayout.addView(dot)
            }

            // Buttons
            val btnSkip = view.findViewById<android.widget.Button>(R.id.btn_skip)
            val btnNext = view.findViewById<android.widget.Button>(R.id.btn_next)
            btnSkip.visibility = android.view.View.VISIBLE
            btnSkip.setOnClickListener { finishTutorial() }

            if (position == titles.size - 1) {
                btnNext.setText(R.string.got_it)
                btnNext.setOnClickListener { finishTutorial() }
            } else {
                btnNext.setText(R.string.next)
                btnNext.setOnClickListener {
                    var v: View? = view
                    while (v != null && v !is ViewPager) { v = (v.parent as? View) }
                    (v as? ViewPager)?.currentItem = position + 1
                }
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }
}
