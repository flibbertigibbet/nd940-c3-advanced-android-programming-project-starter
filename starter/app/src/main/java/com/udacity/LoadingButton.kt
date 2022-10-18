package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()
    private var progress = 0
    private val path = Path()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        invalidate()
    }

    private val paint = Paint()

    init {
        isClickable = true
    }

    fun setIsLoading(isLoading: Boolean) {
        buttonState = if (isLoading) ButtonState.Loading else ButtonState.Completed
    }

    fun setProgress(value: Int) {
        if (progress in 0..100) progress = value else Log.w("Button", "invalid progress $value received")
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(resources.getColor(R.color.colorPrimary, context.theme))

        paint.color = resources.getColor(R.color.white, context.theme)
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = resources.getDimension(R.dimen.default_text_size)

        val label = when(buttonState) {
            ButtonState.Loading -> resources.getString(R.string.button_loading)
            else -> resources.getString(R.string.button_name)
        }

        canvas?.drawText(
            label,
            width / 2F,
            (height / 2F) - ((paint.ascent() + paint.descent()) / 2F),
            paint
        )

        if (progress in 1..99) {
        paint.color = resources.getColor(R.color.colorAccent, context.theme)
        paint.style = Paint.Style.STROKE
        val strokeWidth = 50F
        paint.strokeWidth = strokeWidth


        val arcLeft = (width / 2F) + 240
        val arcTop = (height / 2F) - 30


        path.addArc(arcLeft, arcTop, arcLeft + 50F, arcTop + 50F, 190F, 280F)
        canvas?.drawPath(path, paint)
        } else {
            canvas?.save()
            canvas?.restoreToCount(1)
        }
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}