package com.itexus.stretchingticketview


import android.content.Context
import android.graphics.*
import android.graphics.Paint.Style
import android.graphics.PorterDuff.Mode
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.RelativeLayout
import android.graphics.PorterDuffXfermode
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN



/**
 * Created by Rokalo Alexey on 07/22/19.
 */

class StretchingTicketView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        private val DEFAULT_RADIUS: Float = 9f
        private val NO_VALUE = -1
        const val DEFAULT_COLOR = "#C2C2C4"
    }

    private val eraser = Paint(Paint.ANTI_ALIAS_FLAG)

    private val borderPaint = Paint()
    private var borderColor: Int = 0

    private var topViewId: Int = 0
    private var bottomViewId: Int = 0

    private var circlesPath = Path()
    private var circlePosition: Float = 0f
    private var circleRadius: Float = 0f
    private var circleSpace: Float = 0f

    private var dashColor: Int = 0
    private var dashSize: Float = 0f
    private val dashPath = Path()
    private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        val a = context.obtainStyledAttributes(attrs, R.styleable.StretchingTicketView)
        try {
            circleRadius = a.getDimension(R.styleable.StretchingTicketView_tv_circleRadius, getDp(DEFAULT_RADIUS).toFloat())
            topViewId = a.getResourceId(R.styleable.StretchingTicketView_tv_anchor1, NO_VALUE)
            bottomViewId = a.getResourceId(R.styleable.StretchingTicketView_tv_anchor2, NO_VALUE)
            circleSpace = a.getDimension(R.styleable.StretchingTicketView_tv_circleSpace, getDp(15f).toFloat())
            dashColor = a.getColor(R.styleable.StretchingTicketView_tv_dashColor, Color.parseColor(DEFAULT_COLOR))
            borderColor = a.getColor(R.styleable.StretchingTicketView_tv_borderColor, Color.parseColor(DEFAULT_COLOR))
            dashSize = a.getDimension(R.styleable.StretchingTicketView_tv_dashSize, getDp(1.5f).toFloat())
        } finally {
            a.recycle()
        }

        setXfermode()
        setDashPaint()
        setBorderPaint()

        invalidate()
    }

    private fun setXfermode() {
        eraser.xfermode = PorterDuffXfermode(Mode.CLEAR)
        eraser.isAntiAlias = true
    }

    private fun setBorderPaint() {
        borderPaint.alpha = 0
        borderPaint.isAntiAlias = true
        borderPaint.color = borderColor
        borderPaint.strokeWidth = 5f
        borderPaint.style = Paint.Style.STROKE
    }

    private fun setDashPaint(){
        dashPaint.color = dashColor
        dashPaint.style = Style.STROKE
        dashPaint.strokeWidth = dashSize
        dashPaint.pathEffect = DashPathEffect(floatArrayOf(getDp(3f).toFloat(), getDp(3f).toFloat()), 0f)

    }

    fun setRadius(radius: Float) {
        this.circleRadius = radius
        postInvalidate()
    }


    fun setTopViewAnchor(top: View?) {
        val rectTop = Rect()
        top?.getDrawingRect(rectTop)
        offsetDescendantRectToMyCoords(top, rectTop)
        setSemicirclePosition(top, rectTop)

        postInvalidate()
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        val drawChild = super.drawChild(canvas, child, drawingTime)
        drawHoles(canvas!!)
        return drawChild
    }

    private fun setSemicirclePosition(topView: View?, recTop: Rect) {
        circlePosition = recTop.bottom.toFloat()
        topView?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            circlePosition =bottom.toFloat()
            dashPath.reset()
            postInvalidate()
        }
    }


    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.save()
        super.dispatchDraw(canvas)
        canvas?.restore()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (topViewId != NO_VALUE || bottomViewId != NO_VALUE) {
            val top = findViewById<View>(topViewId)
            val bottom = findViewById<View>(bottomViewId)
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (SDK_INT >= JELLY_BEAN) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else {
                        viewTreeObserver.removeGlobalOnLayoutListener(this)
                    }
                    setTopViewAnchor(top)
                }
            })
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawHoles(canvas)
        super.onDraw(canvas)
    }

    private fun drawHoles(canvas: Canvas) {
        circlesPath = Path()

        // add holes on the ticketView by erasing them
        with(circlesPath) {
            //anchor1
            addCircle(-circleRadius / 4, circlePosition, circleRadius, Path.Direction.CW) // bottom left hole
            addCircle(width + circleRadius / 4, circlePosition, circleRadius, Path.Direction.CW)// bottom right hole

        }

        with(dashPath) {
            //anchor1
            moveTo(circleRadius, circlePosition)
            quadTo(width - circleRadius, circlePosition, width - circleRadius, circlePosition)
        }

        with(canvas) {
            if (dashSize > 0)
                drawPath(dashPath, dashPaint)
            drawPath(circlesPath, eraser)
            drawPath(circlesPath, borderPaint)
        }
    }

    private fun getDp(value: Float): Int {
        return when (value) {
            0f -> 0
            else -> {
                val density = resources.displayMetrics.density
                Math.ceil((density * value).toDouble()).toInt()
            }
        }
    }


}
