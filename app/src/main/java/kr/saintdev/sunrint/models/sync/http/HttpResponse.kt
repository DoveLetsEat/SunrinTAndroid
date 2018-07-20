package kr.saintdev.sunrint.models.sync.http

import org.json.JSONException
import org.json.JSONObject

/**
 * {
    header {
        token: String
        response: Boolean
        errorMessage: String // When error occurred
    }

    data {
        key: Value
        key: Value
    }
    }

    header 가 null 일 경우 서버 응답 오류 또는 파싱 오류
    data 가 null 일경우 반환 값 없슴
 */

class HttpResponse(json: String) {
    private var responseHeader: JSONObject? = null  // 응답 해더
    private var responseBody: JSONObject? = null    // 응답 바디
    private var errorException: Exception? = null   // 파싱 실패 오류 메세지

    init {
        try {
            val jsonData = JSONObject(json)

            this.responseHeader = jsonData.getJSONObject("header")  // 해더 파싱

            if(!jsonData.isNull("data")) {
                this.responseBody = jsonData.getJSONObject("data")
            }
        } catch(ex: JSONException) {
            // 파싱 실패
            this.responseHeader = null
            this.responseBody = null
            this.errorException = ex
        }
    }

    /**
     * 응답 값 parse 성공 여부
     * @return true 는 파싱 성공 및 서버 응답이 정상임을 나타낸다.
     */
    fun isParseSuccess() =
            responseHeader != null && this.errorException == null

    /**
     * header 의 토큰 값 파싱
     * @return client token
     */
    fun getToken(): String? =
            this.responseHeader?.getString("token")

    /**
     * header 의 response 토큰 값 파싱
     * 이 값이 true 이면 서버 처리가 정상임을 나타낸다.
     * @return server execute response
     */
    fun isResponseSuccess() =
            this.responseHeader?.getBoolean("response")

    /**
     * header 의 errorMessage 파싱
     * @return errorMessage
     */
    fun getServerErrorMessage(): String? {
        val tmpResponse = this.responseHeader

        return if(tmpResponse != null && tmpResponse.isNull("errorMessage")) {
            // 오류 메세지를 가져온다
            tmpResponse.getString("errorMessage")
        } else {
            null
        }
    }

    /**
     * key 의 대한 data 를 가져온다.
     * @return Any?
     */
    fun getDataUnit(key: String): Any? =
        responseBody?.get(key)
}