package com.example.reelbin

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Build
import android.os.SystemClock
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.reelbin.Store.capacity
import com.example.reelbin.Store.offsetX
import com.example.reelbin.Store.offsetY

class ReelService : AccessibilityService() {

    private lateinit var wm: WindowManager
    private lateinit var bin: BinView
    private lateinit var lp: WindowManager.LayoutParams
    private var lastCounted = 0L

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> refresh() }

    // View-id fragments that identify a full-screen reel/short player.
    // App updates can rename these, so adjust here if counting stops working.
    private val markers = listOf("clips_viewer", "reel_player", "reel_recycler", "shorts_")

    override fun onServiceConnected() {
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        bin = BinView(this)
        lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            if (Build.VERSION.SDK_INT >= 28) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        wm.addView(bin, lp)
        Store.prefs(this).registerOnSharedPreferenceChangeListener(listener)
        refresh()
    }

    private fun refresh() {
        val d = resources.displayMetrics.density
        lp.x = (offsetX * d).toInt()
        lp.y = (offsetY * d).toInt()
        bin.set(Store.count(this), capacity)
        if (bin.isAttachedToWindow) wm.updateViewLayout(bin, lp)
    }

    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        if (e.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return
        val now = SystemClock.elapsedRealtime()
        if (now - lastCounted < 900) return          // one swipe = a burst of events
        if (!inReels(rootInActiveWindow)) return
        lastCounted = now
        Store.increment(this)                         // triggers refresh via listener
    }

    private fun inReels(root: AccessibilityNodeInfo?): Boolean {
        if (root == null) return false
        val stack = mutableListOf(root)
        var visited = 0
        while (stack.isNotEmpty() && visited < 400) {
            val n = stack.removeAt(stack.size - 1); visited++
            val id = n.viewIdResourceName
            if (id != null && n.isVisibleToUser && markers.any { id.contains(it) }) return true
            for (i in 0 until n.childCount) n.getChild(i)?.let { stack.add(it) }
        }
        return false
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        Store.prefs(this).unregisterOnSharedPreferenceChangeListener(listener)
        if (::bin.isInitialized && bin.isAttachedToWindow) wm.removeView(bin)
        super.onDestroy()
    }
}
