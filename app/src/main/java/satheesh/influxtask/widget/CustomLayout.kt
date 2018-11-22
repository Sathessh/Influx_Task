package satheesh.influxtask.widget


import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import satheesh.influxtask.R
import satheesh.influxtask.data.TimingsItem
import java.util.*

class CustomLayout : LinearLayout {

    private var mContext: Context? = null
    private val mPlayList = ArrayList<TimingsItem>()
    private var viewHeight: Int = 0
    private var viewWidth: Int = 0
    private var mParentView: LinearLayout? = null
    private var rowNum: Int = 0
    private var verticalViewWidth: Int = 0
    var childClickListener: CustomChildClickListener? = null

    interface CustomChildClickListener {
        fun onPreviewClicked()
        fun onBuyTicketClicked()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        this.mContext = context
        verticalViewWidth = AnimUtil.dip2px(mContext!!, 1f)
        val root = View.inflate(mContext, R.layout.preview_layout, null)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        root.measure(widthSpec, heightSpec)
        viewHeight = root.measuredHeight
        viewWidth = (mContext!!.resources.displayMetrics.widthPixels - AnimUtil.dip2px(mContext!!, 2f)) /
                ExpandableGridView.CHILD_COLUMNUM
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    fun refreshDataSet(playList: TimingsItem) {
        mPlayList.clear()
        mPlayList.add(playList)
//        mPlayList.addAll(playList)
        notifyDataSetChange(false)
    }

    fun notifyDataSetChange(needAnim: Boolean) {
        removeAllViews()
        rowNum = mPlayList.size / ExpandableGridView.COLUMNUM +
                if (mPlayList.size % ExpandableGridView.COLUMNUM > 0) 1 else 0
        val rowParam =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val verticalParams = LinearLayout.LayoutParams(verticalViewWidth, LinearLayout.LayoutParams.FILL_PARENT)
        val horizontalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, verticalViewWidth)
        for (rowIndex in 0 until rowNum) {
            val llContainer = LinearLayout(mContext)
            llContainer.orientation = LinearLayout.HORIZONTAL
            val itemParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemParam.width = viewWidth
            for (columnIndex in 0 until ExpandableGridView.COLUMNUM) {
                val itemInfoIndex = rowIndex * ExpandableGridView.COLUMNUM + columnIndex
                var isValidateView = true
                if (itemInfoIndex >= mPlayList.size) {
                    isValidateView = false
                }
                val root = View.inflate(mContext, R.layout.preview_layout, null)
                val previewButton = root.findViewById<View>(R.id.btnOpenPanel) as RelativeLayout
                val buyTicketsButton = root.findViewById<View>(R.id.btnBuyTickets) as Button
                val superChild = root.findViewById<View>(R.id.super_child) as LinearLayout
                enablePreview(previewButton)
                if (isValidateView) {

                    previewButton.setOnClickListener {
                        if (childClickListener != null) {
                            showPreviewView(root, superChild)
                            disablePreview(previewButton)
                        }
                    }
                    buyTicketsButton.setOnClickListener {
                        if (childClickListener != null) {
                            childClickListener!!.onBuyTicketClicked()
                        }
                    }

                }
                llContainer.addView(root, itemParam)
            }
            addView(llContainer, rowParam)
            Log.e("animator", "" + height + "--" + rowNum * viewHeight)
            if (needAnim) {
                createHeightAnimator(mParentView, height, rowNum * viewHeight)
            }
        }
    }


    fun createHeightAnimator(view: View?, start: Int, end: Int) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int

            val layoutParams = view!!.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }

    fun createChildHeightAnimator(view: View?, start: Int, end: Int) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int

            val layoutParams = view!!.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }


    fun setParentView(llBtm: LinearLayout) {
        this.mParentView = llBtm
    }

    fun showPreviewView(root: View, superChild: LinearLayout) {
        superChild.visibility = View.VISIBLE
        Log.e("Child animator", "" + height + "--" + (root.measuredHeight * 2))

//        val recyclerView = superChild.findViewById<RecyclerView>(R.id.recyclerSeat);
//        val maaneger = GridLayoutManager(mContext!!, 10)
//        recyclerView.layoutManager = maaneger
//        val adapter = SeatAdapter()
//        recyclerView.adapter = adapter

        val lnrContent = superChild.findViewById<LinearLayout>(R.id.lnrRows)
        mPlayList[0].seats?.let {
            for (i in 0 until it.size) {
                val viewRows = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_rows,
                    lnrContent, false
                )
                val lnrSeats = viewRows.findViewById<LinearLayout>(R.id.lnrSeats)
                val lblRowStart = viewRows.findViewById<TextView>(R.id.lblRowStart)
                lblRowStart.text = it[i].rowName
                val lblRowEnd = viewRows.findViewById<TextView>(R.id.lblRowEnd)
                lblRowEnd.text = it[i].rowName
                it[i].seats?.let { seats ->
                    run {
                        for (j in 0 until seats.size) {
                            val viewSeat = LayoutInflater.from(mContext).inflate(
                                R.layout.seat,
                                lnrSeats, false
                            )
                            lnrSeats.addView(viewSeat)
                        }
                    }
                    lnrContent.addView(viewRows)

                }

            }
        }



        createChildHeightAnimator(mParentView, height, (root.measuredHeight * 6))
    }

    fun disablePreview(previewButton: RelativeLayout) {
        previewButton.isEnabled = false
    }

    fun enablePreview(previewButton: RelativeLayout) {
        previewButton.isEnabled = true
    }

}

