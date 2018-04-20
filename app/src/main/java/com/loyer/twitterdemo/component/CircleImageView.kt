package com.loyer.twitterdemo.component
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.widget.ImageView
/**
* Created by loyer on 20.03.2018.
*/
class CircleImageView: ImageView {

    //default variables
    /**
     *  it does that to create multiple static properties,
     *  I have to group it together inside companion object block
     */
    companion object {
        private val SCALE_TYPE = ImageView.ScaleType.CENTER_CROP

        private val BTMP_CNFG = Bitmap.Config.ARGB_8888
        private val DRAWABLE_DIMEN = 2

        private val DEFAULT_BORDER_WIDTH = 0
        private val DEFAULT_BORDER_COLOR = Color.BLUE
        private val DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT
    }

    private val mShaderMatrix = Matrix()
    //object to use draw
    private val mBitmapPaint = Paint()
    private val mBorderPaint = Paint()
    private val mCircleBackgroundPaint = Paint()


    private val mDrawableRect = RectF()
    private val mBorderRect = RectF()
    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null

    private var mBitmapWidth: Int = 0
    private var mBitmapHeight: Int = 0

    private var mDrawableRadius: Float = 0f
    private var mBorderRadius: Float = 0f

    private var mColorFilter: ColorFilter? = null

    private var mReady: Boolean = false
    private var mSetupPending: Boolean = false




    constructor(context: Context) : super(context) {

        initialize()
    }

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {


        initialize()
    }
    var isBorderOverlay: Boolean = false
        set(borderOverlay) {
            if (borderOverlay == isBorderOverlay) {
                return
            }

            field = borderOverlay
            setupCircle()
        }

    var isDisableCircularTransformation: Boolean = false
        set(disableCircularTransformation) {
            if (isDisableCircularTransformation == disableCircularTransformation) {
                return
            }

            field = disableCircularTransformation
            initBitmap()
        }

    /**
     * Sets the CircularImageView's basic border color.
     * @param borderColor The new color  to set the border.
     */
    var borderColor = DEFAULT_BORDER_COLOR
        set(@ColorInt borderColor) {
            if (borderColor == this.borderColor) {
                return
            }

            field = borderColor
            mBorderPaint.color = this.borderColor
            invalidate()
        }

    /**
     * Sets the CircularImageView's border width in pixels.
     * @param borderWidth Width in pixels for the border.
     */
    var borderWidth = DEFAULT_BORDER_WIDTH
        set(borderWidth) {
            if (borderWidth == this.borderWidth) {
                return
            }

            field = borderWidth
            setupCircle()
        }

    /**
     * Sets the CircularImageView's backgroundColor.
     * @param circleBackgroundColor The new color to set the background.
     */
    var circleBackgroundColor = DEFAULT_BACKGROUND_COLOR
        set(@ColorInt circleBackgroundColor) {
            if (circleBackgroundColor == this.circleBackgroundColor) {
                return
            }

            field = circleBackgroundColor
            mCircleBackgroundPaint.color = circleBackgroundColor
            invalidate()
        }



    private fun initialize() {
        super.setScaleType(SCALE_TYPE)
        mReady = true


        if (mSetupPending) {
            setupCircle()
            mSetupPending = false
        }
    }

    override fun getScaleType(): ImageView.ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException("ScaleType is not supported.")
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("adjustViewBounds is not supported.")
        }
    }



    // the canvas on which the background will be drawn
    override fun onDraw(canvas: Canvas) {
        if (isDisableCircularTransformation) {
            super.onDraw(canvas)
            return
        }
        //don't anything without a bitmap
        if (mBitmap == null) {
            return
        }
        //redraw if not transparent
        if (circleBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mCircleBackgroundPaint)
        }
        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint)
        //Draw the circular image itself if borderWith is greater than zero
        if (borderWidth > 0) {
            canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint)
        }
    }

    // override methods
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupCircle()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setupCircle()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setupCircle()
    }


    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initBitmap()
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf === mColorFilter) {
            return
        }

        mColorFilter = cf
        okColorFilter()
        invalidate()
    }

    override fun getColorFilter(): ColorFilter? {
        return mColorFilter
    }



    // if it is null set  the paint color filter
    private fun okColorFilter() {
        if (mBitmapPaint != null) {
            mBitmapPaint.colorFilter = mColorFilter
        }
    }

    /**
     * Convert a drawable object into a Bitmap.
     * @param drawable Drawable to extract a Bitmap from.
     * @return A Bitmap created from the drawable parameter.
     */
    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) { //// Don't do anything without a proper drawable
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        try {
            val bitmap: Bitmap
            if (drawable is ColorDrawable) { //// Use the createBitmap method instead if ColorDrawable
                bitmap = Bitmap.createBitmap(DRAWABLE_DIMEN, DRAWABLE_DIMEN, BTMP_CNFG)
            } else { //Create Bitmap object out of the drawable
                bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BTMP_CNFG)
            }
            //Construct a canvas with the specified bitmap to draw into.
            val canvas = Canvas(bitmap)
            // Specify a bounding rectangle for the Drawable.This is where the drawable will draw when its draw() method is called.
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            //Draw in its bounds
            drawable.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    private fun initBitmap() {
        if (isDisableCircularTransformation) {
            mBitmap = null
        } else {  //get photo from drawable
            mBitmap = getBitmapFromDrawable(drawable)
        }
        //and bring circle shape
        setupCircle()
    }

    //create bitmap and settings it
    private fun setupCircle() {
        if (!mReady) {
            mSetupPending = true
            return
        }

        if (width == 0 && height == 0) {
            return
        }

        if (mBitmap == null) {
            invalidate()
            return
        }
        //draw bitmap
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        //AntiAliasing smooths out the edges of what is being drawn
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.shader = mBitmapShader
        //
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = borderColor
        mBorderPaint.strokeWidth = borderWidth.toFloat()

        mCircleBackgroundPaint.style = Paint.Style.FILL
        mCircleBackgroundPaint.isAntiAlias = true
        mCircleBackgroundPaint.color = circleBackgroundColor
        //width and height of the selected bitmap
        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width
        //Copy the coordinates from src into this rectangle.
        mBorderRect.set(setBorder())
        //use min function for radius
        mBorderRadius = Math.min((mBorderRect.height() - borderWidth) / 2.0f, (mBorderRect.width() - borderWidth) / 2.0f)

        mDrawableRect.set(mBorderRect)
        //the sides are moved outwards, making the rectangle wider
        if (!isBorderOverlay && borderWidth > 0) {
            mDrawableRect.inset(borderWidth - 1.0f, borderWidth - 1.0f)
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f)

        okColorFilter()
        updateShaderMatrix()
        invalidate()
    }
    //Create a new rectangle with the specified coordinates.
    private fun setBorder(): RectF {
        val presentWidth = width - paddingLeft - paddingRight
        val presentHeight = height - paddingTop - paddingBottom

        val length = Math.min(presentWidth, presentHeight)

        val left = paddingLeft + (presentWidth - length) / 2f
        val top = paddingTop + (presentHeight - length) / 2f

        return RectF(left, top, left + length, top + length)
    }

    /**
     * Re-initializes the shader texture used to fill in
     * the Circle upon drawing.
     */
    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        // null this matrix
        mShaderMatrix.set(null)
        //re set if height and width not equal
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
        }
        //Set the matrix to scale by scale
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate((dx + 0.5f).toInt() + mDrawableRect.left, (dy + 0.5f).toInt() + mDrawableRect.top)

        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

}