package com.example.reelbin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import com.example.reelbin.Store.capacity
import com.example.reelbin.Store.offsetX
import com.example.reelbin.Store.offsetY

class MainActivity : Activity() {
    private lateinit var countText: TextView

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val d = resources.displayMetrics.density
        val pad = (20 * d).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(pad, pad * 2, pad, pad)
        }
        countText = TextView(this).apply {
            textSize = 48f; gravity = Gravity.CENTER; setTextColor(Color.BLACK)
        }
        root.addView(countText)
        root.addView(TextView(this).apply {
            text = "reels watched today"; gravity = Gravity.CENTER
        })

        root.addView(Button(this).apply {
            text = "1. Enable ReelBin in Accessibility settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        })

        fun slider(label: String, min: Int, max: Int, get: () -> Int, set: (Int) -> Unit) {
            val t = TextView(this).apply { text = "$label: ${get()}"; setPadding(0, pad, 0, 0) }
            val s = SeekBar(this).apply {
                this.max = max - min; progress = get() - min
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(sb: SeekBar, p: Int, u: Boolean) {
                        set(p + min); t.text = "$label: ${p + min}"
                    }
                    override fun onStartTrackingTouch(sb: SeekBar) {}
                    override fun onStopTrackingTouch(sb: SeekBar) {}
                })
            }
            root.addView(t); root.addView(s)
        }
        slider("Bin full at (reels)", 10, 200, { capacity }) { capacity = it }
        slider("Move right from centre (dp)", -150, 150, { offsetX }) { offsetX = it }
        slider("Move down from top (dp)", 0, 80, { offsetY }) { offsetY = it }

        root.addView(Button(this).apply {
            text = "Empty the bin (reset count)"
            setOnClickListener { Store.reset(context); onResume() }
        })
        setContentView(ScrollView(this).apply { addView(root) })
    }

    override fun onResume() {
        super.onResume()
        countText.text = Store.count(this).toString()
    }
}
