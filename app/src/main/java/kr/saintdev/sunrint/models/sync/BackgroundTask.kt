package kr.saintdev.sunrint.models.sync

import android.os.AsyncTask

/**
 * script 를 재정의 하여 비동기 작업을 처리합니다.
 * listener 는 정의 할 필요 없으나 응답을 받을 수 없습니다.
 */

abstract class BackgroundTask<T>(
        val requestCode: Int,
       val listener: BackgroundTaskListener<T>? = null) : AsyncTask<Void, Void, T>() {

    private var isErrorOccurred = false     // 오류 발생 여부
    private var errorException: Exception? = null   // 오류 발생 시
    private var result: T?= null            // 응답 객체

    override fun doInBackground(vararg params: Void?): T? {
        return try {
            this.isErrorOccurred = false
            script()     // 스크립트 처리
        } catch(ex: Exception) {
            this.result = null          // 결과 값 null 처리
            this.isErrorOccurred = true // 오류 발생 정의
            this.errorException = ex    // 오류 메세지 정의
            null
        }
    }

    override fun onPostExecute(result: T) {
        super.onPostExecute(result)

        if(this.listener != null) {
            if (this.isErrorOccurred) {
                // 오류 발생
                listener.onFailed(this.errorException)
            } else {
                this.result = result
                listener.onSuccess(this)
            }
        }
    }

    /**
     * 07.20 13:30
     * 이 함수를 재정의 하여 필요한 기능을 정의한다.
     */
    abstract fun script(): T

    /**
     * Functions
     */

    fun isErrorOccurred() = this.isErrorOccurred

    fun getResult() = this.result
}