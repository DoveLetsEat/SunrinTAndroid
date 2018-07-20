package kr.saintdev.sunrint.models.manager.gps

interface GPSUpdateListener {
    fun onStart()                   // 위치 정보 트랙킹 시작
    fun onUpdated()                 // 트랙킹 시작 후 GPS 정보 업데이트 됨
    fun onFirstUpdate()             // 트랙킹 시작 후 처음으로 정보가 업데이트 됨
    fun onStartFailed(ex: Exception) // 위치정보 트랙킹 실패
}