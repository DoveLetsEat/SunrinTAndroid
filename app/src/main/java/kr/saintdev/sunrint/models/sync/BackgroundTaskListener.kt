package kr.saintdev.sunrint.models.sync

interface BackgroundTaskListener<T> {
    fun onSuccess(response: BackgroundTask<T>)
    fun onFailed(ex: Exception?)
}