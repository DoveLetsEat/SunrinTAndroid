package kr.saintdev.sunrint.models.sync.http

import kr.saintdev.sunrint.models.sync.BackgroundTask
import kr.saintdev.sunrint.models.sync.BackgroundTaskListener
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class HttpRequester(
        requestCode: Int,
        val url: String,                        // 요청 URL
        val args: HashMap<String, Any>? = null,       // 인자값
        listener: BackgroundTaskListener<HttpResponse>? = null
): BackgroundTask<HttpResponse> (requestCode, listener) {

    override fun script(): HttpResponse {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        val reqBuilder = FormBody.Builder()

        // 인자 값이 있다면 넣어줍니다.
        if (args != null) {
            for((key, value) in args) {
                reqBuilder.add(key, value.toString())
            }
        }

        val reqBody = reqBuilder.build()
        val request = Request.Builder().url(this.url).post(reqBody).build()

        val response = client.newCall(request).execute()
        val jsonScript = response.body()?.string()

        return if(jsonScript == null) throw Exception("Response is null")
        else HttpResponse(jsonScript)
    }
}