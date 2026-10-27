package com.example.reelbin

import android.content.Context
import android.graphics.*
import android.view.View

/** A tiny dustbin that fills up (green -> amber -> red) as the count nears capacity, with the number beside it. */
class BinView(ctx: Context) : View(ctx) {
    private var count = 0
    private var capacity = 50
    private val d = ctx.resources.displayMetrics.density

    private val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = Color.WHITE; strokeWidth = 1.6f * d
        strokeJoin = Paint.Join.ROUND
    }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; textSize = 13f * d; typeface = Typeface.DEFAULT_BOLD
        setShadowLayer(3f * d, 0f, 0f, Color.BLACK)
    }
    private val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(110, 0, 0, 0) }

    fun set(count: Int, capacity: Int) {
        this.count = count; this.capacity = capacity.coerceAtLeast(1); invalidate()
    }

    override fun onMeasure(w: Int, h: Int) = setMeasuredDimension((62 * d).toInt(), (24 * d).toInt())

    override fun onDraw(c: Canvas) {
        val h = height.toFloat()
        c.drawRoundRect(0f, 0f, width.toFloat(), h, h / 2, h / 2, bg)

        val s = h * 0.7f                 // bin icon box
        val ox = h * 0.22f; val oy = h * 0.15f
        val body = Path().apply {
            moveTo(ox + s * 0.18f, oy + s * 0.30f)
            lineTo(ox + s * 0.82f, oy + s * 0.30f)
            lineTo(ox + s * 0.74f, oy + s * 1.00f)
            lineTo(ox + s * 0.26f, oy + s * 1.00f)
            close()
        }
        val frac = (count.toFloat() / capacity).coerceIn(0f, 1f)
        fill.color = when {
            frac < 0.5f -> Color.rgb(76, 217, 100)
            frac < 0.85f -> Color.rgb(255, 193, 7)
            else -> Color.rgb(255, 69, 58)
        }
        c.save(); c.clipPath(body)
        val top = oy + s * 0.30f; val bottom = oy + s * 1.00f
        c.drawRect(ox, bottom - (bottom - top) * frac, ox + s, bottom, fill)
        c.restore()

        c.drawPath(body, outline)
        c.drawLine(ox + s * 0.10f, oy + s * 0.22f, ox + s * 0.90f, oy + s * 0.22f, outline) // lid
        c.drawLine(ox + s * 0.38f, oy + s * 0.10f, ox + s * 0.62f, oy + s * 0.10f, outline) // handle

        c.drawText(count.toString(), h * 1.05f, h * 0.7f, text)
    }
}
