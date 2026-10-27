package com.example.reelbin

import android.content.Context
import java.time.LocalDate

object Store {
    private fun p(c: Context) = c.getSharedPreferences("reelbin", Context.MODE_PRIVATE)

    fun prefs(c: Context) = p(c)

    /** Count for today; resets automatically when the date changes. */
    fun count(c: Context): Int {
        val today = LocalDate.now().toString()
        if (p(c).getString("day", "") != today) {
            p(c).edit().putString("day", today).putInt("count", 0).apply()
        }
        return p(c).getInt("count", 0)
    }

    fun increment(c: Context): Int {
        val n = count(c) + 1
        p(c).edit().putInt("count", n).apply()
        return n
    }

    fun reset(c: Context) = p(c).edit().putInt("count", 0).apply()

    var Context.capacity: Int
        get() = p(this).getInt("capacity", 50)
        set(v) = p(this).edit().putInt("capacity", v).apply()

    var Context.offsetX: Int   // dp right of screen centre
        get() = p(this).getInt("x", 28)
        set(v) = p(this).edit().putInt("x", v).apply()

    var Context.offsetY: Int   // dp below top edge
        get() = p(this).getInt("y", 4)
        set(v) = p(this).edit().putInt("y", v).apply()
}
