package kr.twothumb.bookpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.Px
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kr.twothumb.lib.logger.DevLog


class MainActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DevLog.getInstance().init("TestPager")

        val list: List<Int> = listOf(
            R.color.colorAccent,
            R.color.colorWhite,
            R.color.colorPrimaryDark,
            R.color.colorRed
        )
        pager = findViewById(R.id.pager)
        pager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 3
            clipToPadding = false
            clipChildren = false
            isNestedScrollingEnabled= false
            adapter = PagerRecyclerAdapter(list)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                var currentPosition = 0
                override fun onPageScrolled(position: Int, positionOffset: Float,@Px positionOffsetPixels: Int) {
                    DevLog.i("positionOffset : ", positionOffset, " , ", position)
                    /**
                     * todo
                     * positionOffset . 5단위로 넘어갈때마다 포지션 계산해서 위아래 바꿔줘야,
                     */

                    if(currentPosition != position) {
                        currentPosition = position
                        DevLog.d("position : ", position)
                    }
                }

                override fun onPageSelected(position: Int) {
                    currentPosition = position
                    DevLog.e("position : ", position)
                }
            })
            setPageTransformer(BookPageTransformer(this))
        }.post {
            pager.adapter?.notifyDataSetChanged()
        }

    }

    class PagerRecyclerAdapter(private val bgColors: List<Int>) : RecyclerView.Adapter<PagerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
                PagerViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_pager,
                        parent,
                        false
                    )
                )

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            holder.bind(bgColors[position])
        }

        override fun getItemCount(): Int = bgColors.size
    }


    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bind(@ColorRes bgColor: Int) {
            DevLog.i("is here : ", itemView is PageLayout)
            (itemView as PageLayout).apply {
                bind.pageRight.setBackgroundColor(ContextCompat.getColor(itemView.context, bgColor))
                bind.pageLeft.setBackgroundColor(ContextCompat.getColor(itemView.context, bgColor))
                bind.pageLeft.alpha = .8f
            }
        }
    }

    class BookPageTransformer(private val pager2: ViewPager2) : ViewPager2.PageTransformer {

        var currentProgress = 0f
        var position = 0f

        override fun transformPage(view: View, position: Float) {

            /**
             * 다른페이지 설정
             * ScaleX 설정
             * 다음페이지 하위에 위치시키기
             */
            (view as PageLayout).apply {
                val pageWidth = width
                when {
                    //이전 페이지
                    position < -1 -> {
                    }
                    //현재 페이지 -> 포지션이 0보다 작으면
                    position <= 0 -> { // [-1,0]
                        alpha = 1f

                        //왼쪽으로 슬라이드 움직일때
                        bind.pageRight.rotationY = position * 180
                        bind.pageLeft.rotationY = 0f

                        pager2.bringChildToFront(this)
                        this.requestLayout()
                        pager2.requestLayout()

////                        DevLog.i("abs((1 - position * 2) : ", abs((1 - position * 2) / 2))
//                        bind.pageRight.scaleX = abs((1 - position * 2) /2)
//                        DevLog.i(Math.tan(position.toDouble()))

//                        DevLog.d(Math.cos(position.toDouble()))
//                        bind.pageRight.scaleX = 1 * Math.tan(position.toDouble())
//                        bind.pageLeft.rotationY = position * +90

//                        bind.pageRight.rotationY = position * 90
//                        rotationY = position * 90
//                        scaleX = -position * 1.5f

                    }
                    //다음페이지는 제자리
                    position <= 1 -> { // (0,1]
                        alpha = 1f
//                        alpha = 0f
//                        alpha = position
//                        bind.pageLeft.alpha = 0f
//                        bind.pageRight.alpha = 0f
                        bind.pageLeft.rotationY = position * 180

                        // Counteract the default slide transition
//                        bind.pageRight.rotationY = position * 90 + 90
//                        bind.pageLeft.rotationY = position * +90

                        // Scale the page down (between MIN_SCALE and 1)
                    }
                    else -> { // (1,+Infinity]
                        alpha = 0f

                    }

                }
                view.translationX = -1 * view.width * position
            }
        }
    }
}