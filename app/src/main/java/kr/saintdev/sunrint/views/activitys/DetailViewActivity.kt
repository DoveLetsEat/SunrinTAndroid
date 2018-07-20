package kr.saintdev.sunrint.views.activitys

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_details.*
import kr.saintdev.sunrint.R
import kr.saintdev.sunrint.models.APIUrl
import kr.saintdev.sunrint.models.APIUrl.MORE_INFO_FOR_PARKING
import kr.saintdev.sunrint.models.data.ParkingDetailData
import kr.saintdev.sunrint.models.sync.BackgroundTask
import kr.saintdev.sunrint.models.sync.BackgroundTaskListener
import kr.saintdev.sunrint.models.sync.http.HttpRequester
import kr.saintdev.sunrint.models.sync.http.HttpResponse

class DetailViewActivity : SuperActivity() {
    private val REQUESET_MORE_INFO = 0x0
    private lateinit var id: String
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        this.id = intent.getStringExtra("id")
        this.title = intent.getStringExtra("title")
        details_title.text = title

        // 서버에 요청한다.
        openProgressDialog("상세 정보를 불러오는 중...")
        val args = hashMapOf<String, Any>("id" to this.id)
        val requester = HttpRequester(REQUESET_MORE_INFO, MORE_INFO_FOR_PARKING, args, OnHttpReponseListener())
        requester.execute()
    }

    inner class OnHttpReponseListener : BackgroundTaskListener<HttpResponse> {
        override fun onSuccess(response: BackgroundTask<HttpResponse>) {
            closeProgressDialog()

            val httpResponse = response.getResult()
            if(httpResponse != null && httpResponse.isParseSuccess()) {
                val data = ParkingDetailData(
                    id,
                        arrayOf(httpResponse.getDataUnit("startTime") as? Int?, httpResponse.getDataUnit("startCost") as? Int?),
                        arrayOf(httpResponse.getDataUnit("moreTime") as? Int?, httpResponse.getDataUnit("moreCost") as? Int?),
                        arrayOf(httpResponse.getDataUnit("weekdayStartTime") as? String?, httpResponse.getDataUnit("weekdayEndTime") as? String?),
                        arrayOf(httpResponse.getDataUnit("satDayStartTime") as? String?, httpResponse.getDataUnit("satDayEndTime") as? String?),
                        arrayOf(httpResponse.getDataUnit("holiDayStartTime") as? String?, httpResponse.getDataUnit("holiDayEndTime") as? String?)
                )

                details_weekend_time.text = data.weekdayTime[0] + "~" + data.weekdayTime[1]
                details_satday_time.text = data.satDayTime[0] + "~" + data.satDayTime[1]
                details_holiday_time.text = data.holidayTime[0] + "~" + data.holidayTime[1]

                if(data.defaultCost[0] == null || data.defaultCost[1] == null) {
                    details_cost_default.text = "자료 없슴"
                } else {
                    details_cost_default.text = "${data.defaultCost[0]}분 / ${data.defaultCost[1]}원"
                }

                if(data.moreCost[0] == null || data.moreCost[1] == 1) {
                    details_cost_more.text = "자료 없슴"
                } else {
                    details_cost_more.text = "${data.moreCost[0]}분 / ${data.moreCost[1]}원"
                }
            } else {
                onFailed(Exception("Response failed"))
            }
        }

        override fun onFailed(ex: Exception?) {
            closeProgressDialog()
            openMessageDialog("An error occurred", "상세 정보를 요청 할 수 없습니다.\n${ex?.message}")
        }
    }
}