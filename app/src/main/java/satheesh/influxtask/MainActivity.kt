package satheesh.influxtask

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.date.view.*
import kotlinx.android.synthetic.main.lnr_container.view.*
import org.json.JSONObject
import satheesh.influxtask.data.DatesItem
import satheesh.influxtask.data.Info
import satheesh.influxtask.widget.ExpandableGridClickListner
import satheesh.influxtask.widget.ExpandableGridView
import java.io.IOException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), ExpandableGridClickListner {

    val bookingTime = ArrayList<Info>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }


    private fun init() {
        val infos = generateData()
        for (i in 0 until infos.size) {
            val viewMovies = LayoutInflater.from(this).inflate(
                R.layout.lnr_container, container,
                false
            )
            viewMovies.lblMovieName.text = infos[i].timing.moviewName
            val gridView = ExpandableGridView(this)
            gridView.setExpandableClickListner(this)
            val timings = infos[i].timing.timings!!
            for (i in 0 until timings.size) {
                timings[i].expand = 1
            }
            gridView.refreshIconInfoList(timings)
            viewMovies.lnrExpand.addView(gridView)
            container.addView(viewMovies)
        }

        val datesArr = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd", Locale.US)
        for (i in 0 until 15) {
            datesArr.add(
                calendar.getDisplayName(
                    Calendar.DAY_OF_WEEK,
                    Calendar.SHORT,
                    Locale.getDefault()
                ).toUpperCase() + "qq" + calendar.get(Calendar.DATE).toString()
            )
            calendar.add(Calendar.DATE, 1)
        }
        viewpagerDate.adapter = DatePagerAdapter(this, datesArr)
    }


    private fun generateData(): ArrayList<Info> {
        val testData = JSONObject(loadJSONFromAsset())
        if (testData.has("result")) {
            val resultObj = testData.getJSONObject("result")
            if (resultObj.has("dates")) {
                val datesArrays = resultObj.getJSONArray("dates")
                val dateObj = Gson().fromJson(
                    datesArrays.getJSONObject(0).toString(),
                    DatesItem::class.java
                )
                dateObj.timings?.let {
                    for (i in 0 until it.size) {
                        val timing = it[i]
                        bookingTime.add(Info(i, timing, 1))
                    }
                }
            }
        }
        return bookingTime
    }

    override fun onParentClick(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    }

    override fun onPreviewClick() {
        Toast.makeText(this, "onPreviewClick", Toast.LENGTH_SHORT).show()

    }

    override fun onBuyTicketClick() {
        Toast.makeText(this, "onBuyTicketClick", Toast.LENGTH_SHORT).show()
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val inputStream = assets.open("test_data.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }


    class DatePagerAdapter(val context: Context, val datesArr: List<String>) : PagerAdapter() {

        @SuppressLint("SetTextI18n")
        override fun instantiateItem(container: ViewGroup, position: Int): View {

            val inflater: LayoutInflater? = LayoutInflater.from(context)
            val itemView = inflater!!.inflate(R.layout.date, container, false)

            val txt = datesArr[position].split("qq")
            itemView.lblDate.text = txt[0] + "\n" + txt[1]  //datesArr[position]
            container.addView(itemView)
            return itemView
        }

        override fun getCount(): Int {
            return datesArr.size
        }

        override fun isViewFromObject(container: View, obj: Any): Boolean {
            return container == obj as TextView
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View?)
        }
    }

}
