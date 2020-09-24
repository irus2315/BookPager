package kr.twothumb.bookpager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil.inflate
import kr.twothumb.bookpager.databinding.LayoutPageBinding
import kr.twothumb.lib.logger.DevLog

class PageLayout: ConstraintLayout {

    val bind: LayoutPageBinding = inflate(LayoutInflater.from(context),  R.layout.layout_page, this, true)

    val right: View
    val left: View
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        right = bind.pageRight
        left = bind.pageLeft
        right.post {
            right.pivotX = 0f
        }
        DevLog.e("left.widht : " , left.width)
        left.post {

            left.pivotX = left.width.toFloat()
        }
    }
}