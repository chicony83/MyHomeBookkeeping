package com.chico.myhomebookkeeping.icons

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.chico.myhomebookkeeping.R

class MaterialSymbolDrawable(context: Context, private val iconKey: String) : Drawable() {
    private val density = context.resources.displayMetrics.density
    private val defaultColor = context.obtainStyledAttributes(
        intArrayOf(android.R.attr.colorControlNormal)
    ).let { attributes ->
        try {
            attributes.getColor(0, Color.DKGRAY)
        } finally {
            attributes.recycle()
        }
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = defaultColor
        textAlign = Paint.Align.CENTER
        fontFeatureSettings = "liga"
        typeface = ResourcesCompat.getFont(context, R.font.material_symbols_rounded)
            ?: Typeface.DEFAULT
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        paint.textSize = bounds.height().toFloat()
        val metrics = paint.fontMetrics
        val baseline = bounds.centerY() - (metrics.ascent + metrics.descent) / 2f
        canvas.drawText(iconKey, bounds.centerX().toFloat(), baseline, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = (24 * density).toInt()

    override fun getIntrinsicHeight(): Int = (24 * density).toInt()

    override fun getDirtyBounds(): Rect = bounds
}
