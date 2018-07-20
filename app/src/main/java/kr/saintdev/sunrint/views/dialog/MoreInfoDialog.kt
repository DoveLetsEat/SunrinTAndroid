package kr.saintdev.sunrint.views.dialog

import android.content.Context
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.javiersantos.bottomdialogs.BottomDialog
import kr.saintdev.sunrint.R
import kr.saintdev.sunrint.models.data.ParkingMetaData
import kr.saintdev.sunrint.models.func.Convert
import kr.saintdev.sunrint.models.func.colorCompat
import kr.saintdev.sunrint.views.activitys.DetailViewActivity
import kr.saintdev.sunrint.views.activitys.SuperActivity

class MoreInfoDialog(activity: SuperActivity, val data: ParkingMetaData) {
    val bottomDialog = BottomDialog.Builder(activity)

    init {
        bottomDialog.setTitle(data.parkingName)
        bottomDialog.setContent(data.parkingAddress)
        bottomDialog.setCustomView(makeView(activity))
        bottomDialog.setCancelable(true)
    }

    private fun makeView(context: Context): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_bottom_metainfo, null, false)

        val statusView = view.findViewById<TextView>(R.id.metainfo_now_status)
        val distanceView = view.findViewById<TextView>(R.id.metainfo_distance)

        if(data.isOpened) {
            statusView.text = "영업 중"
            statusView.setTextColor(R.color.colorGreen.colorCompat(context))
        } else {
            statusView.text = "영업 종료"
            statusView.setTextColor(R.color.colorRed.colorCompat(context))
        }

        distanceView.text = Convert.convertIntToDistance(data.distance)

        val detailButton = view.findViewById<FloatingActionButton>(R.id.metainfo_goto_detail)
        detailButton.setOnClickListener {
            val intent = Intent(context, DetailViewActivity::class.java)
            intent.putExtra("id", data.id)
            intent.putExtra("title", data.parkingName)
            context.startActivity(intent)
        }

        return view
    }

    fun showDialog() {
        bottomDialog.show()
    }
}