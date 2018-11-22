package satheesh.influxtask.widget

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import satheesh.influxtask.R
import satheesh.influxtask.data.TimingsItem
import java.util.*


class ExpandableGridView(private val mContext: Context) : LinearLayout(mContext, null) {

    private var expandableGridClickListner: ExpandableGridClickListner? = null

    private var mChildClickListener: CustomLayout.CustomChildClickListener? = null

    var bookingTimeList = ArrayList<TimingsItem>()
    private var viewHeight: Int = 0
    private val verticalViewWidth = 1
    private var mItemViewClickListener: ItemViewClickListener? = null
    private var mParentView: LinearLayout? = null
    private var mSuperChild: LinearLayout? = null
    private var mRoot: LinearLayout? = null
    private var swap = 0

    fun setExpandableClickListner(listner: ExpandableGridClickListner) {
        this.expandableGridClickListner = listner
    }

    companion object {
        val COLUMNUM = 3
        val CHILD_COLUMNUM = 1
    }

    init {
        orientation = LinearLayout.VERTICAL
        initData()
    }

    private fun initData() {

        mChildClickListener = object : CustomLayout.CustomChildClickListener {
            override fun onPreviewClicked() {

                return if (expandableGridClickListner == null) {
                } else {
                    expandableGridClickListner!!.onPreviewClick()
                }

            }

            override fun onBuyTicketClicked() {
                return if (expandableGridClickListner == null) {
                } else {
                    expandableGridClickListner!!.onBuyTicketClick()
                }
            }

        }
    }

    fun refreshIconInfoList(iconInfoList: List<TimingsItem>) {
        this.bookingTimeList.clear()
        this.bookingTimeList.addAll(iconInfoList)
        refreshViewUI()
    }

    private fun refreshViewUI() {
        removeAllViews()
        val rowNum =
            bookingTimeList.size / COLUMNUM + if (bookingTimeList.size % COLUMNUM > 0) 1 else 0
        val rowParam =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val verticalParams = LinearLayout.LayoutParams(verticalViewWidth, LinearLayout.LayoutParams.FILL_PARENT)
        val horizontalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, verticalViewWidth)

        for (rowIndex in 0 until rowNum) {
            val rowView = View.inflate(mContext, R.layout.gridview_above_rowview, null)

            val llRowContainer = rowView.findViewById<View>(R.id.gridview_rowcontainer_ll) as LinearLayout
            val ivOpenFlag = rowView.findViewById<View>(R.id.gridview_rowopenflag_iv) as ImageView
            val llBtm = rowView.findViewById<View>(R.id.gridview_rowbtm_ll) as LinearLayout
            val gridViewNoScroll = rowView.findViewById<View>(R.id.gridview_child_gridview) as CustomLayout

            if (mChildClickListener != null) {
                gridViewNoScroll.childClickListener = mChildClickListener
            }

            gridViewNoScroll.setParentView(llBtm)

            val itemParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemParam.weight = 1.0f
            val itemClickLitener = ItemViewClickListener(llBtm, ivOpenFlag, object : ItemViewClickInterface {

                override fun shoudInteruptViewAnimtion(listener: ItemViewClickListener, position: Int): Boolean {
                    var isInterupt = false
                    if (mItemViewClickListener != null && mItemViewClickListener != listener) {
                        mItemViewClickListener!!.closeExpandView()
                    }
                    mItemViewClickListener = listener
                    val iconInfo = bookingTimeList[position]
                    val childList = iconInfo

                    if (iconInfo.expand > 0) {
                        gridViewNoScroll.refreshDataSet(childList)
                    } else {
                        setViewCollaps()
                        isInterupt = true
                        if (expandableGridClickListner != null) {
                            expandableGridClickListner!!.onParentClick(childList.timing)
                        }
                    }

                    return isInterupt
                }

                override fun viewUpdateData(position: Int) {
                    gridViewNoScroll.notifyDataSetChange(true)
                }
            })

            for (columnIndex in 0 until COLUMNUM) {
                val itemView = View.inflate(mContext, R.layout.grid_item, null)
                val tvName = itemView.findViewById<View>(R.id.grid_item) as TextView
                val itemInfoIndex = rowIndex * COLUMNUM + columnIndex
                if (itemInfoIndex > bookingTimeList.size - 1) {
                    itemView.visibility = View.INVISIBLE
                } else {
                    val iconInfo = bookingTimeList[itemInfoIndex]
                    tvName.text = iconInfo.timing
                    itemView.id = itemInfoIndex
                    itemView.tag = itemInfoIndex
                    itemView.setOnClickListener(itemClickLitener)
                }
                llRowContainer.addView(itemView, itemParam)
            }
            addView(rowView, rowParam)
        }
    }

    fun setViewCollaps() {
        if (mItemViewClickListener != null) {
            mItemViewClickListener!!.closeExpandView()
        }
    }


    interface ItemViewClickInterface {
        fun shoudInteruptViewAnimtion(listener: ItemViewClickListener, position: Int): Boolean

        fun viewUpdateData(position: Int)
    }

    inner class ItemViewClickListener(
        val contentView: View,
        private val mViewFlag: View,
        private val animationListener: ItemViewClickInterface?
    ) : View.OnClickListener {
        private val INVALID_ID = -1000
        private var mLastViewID = INVALID_ID

        private var startX: Int = 0
        private var viewFlagWidth: Int = 0
        private var itemViewWidth: Int = 0

        override fun onClick(view: View) {
            val viewId = view.id
            var isTheSameView = false
            if (animationListener != null) {
                if (animationListener.shoudInteruptViewAnimtion(this, viewId)) {
                    return
                }
            }
            if (mLastViewID == viewId) {
                isTheSameView = true
            } else {
                mViewFlag.visibility = View.VISIBLE
                viewFlagWidth = AnimUtil.getViewFlagWidth(mViewFlag)
                itemViewWidth = view.width
                val endX = view.left + itemViewWidth / 2 - viewFlagWidth / 2
                if (mLastViewID == INVALID_ID) {
                    startX = endX
                    AnimUtil.xAxismoveAnim(mViewFlag, startX, endX)
                } else {
                    AnimUtil.xAxismoveAnim(mViewFlag, startX, endX)
                }
                startX = endX
            }
            val isVisible = contentView.visibility == View.VISIBLE
            if (isVisible) {
                if (isTheSameView) {
                    AnimUtil.animateCollapsing(contentView, mViewFlag)
                } else {
                    animationListener?.viewUpdateData(viewId)
                }
            } else {
                if (isTheSameView) {
                    mViewFlag.visibility = View.VISIBLE
                    AnimUtil.xAxismoveAnim(mViewFlag, startX, startX)
                }
                AnimUtil.animateExpanding(contentView)
            }
            mLastViewID = viewId
        }

        fun closeExpandView() {
            AnimUtil.closeExpandView(contentView, mViewFlag)
        }

    }


    fun setParentView(llBtm: LinearLayout, superChild: LinearLayout, root: LinearLayout) {
        this.mParentView = llBtm
        this.mSuperChild = superChild
        this.mRoot = root
    }


    fun showPreviewView() {
        AnimUtil.animateChildExpanding(mParentView!!, mSuperChild!!)
    }

}