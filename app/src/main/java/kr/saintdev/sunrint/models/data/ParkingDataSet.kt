package kr.saintdev.sunrint.models.data

import kr.saintdev.sunrint.models.manager.gps.GPSObject

data class ParkingMetaData(
        val id: String,             // 주차장 고유 번호
        val parkingName: String,    // 주차장 이름
        val parkingAddress: String, // 주차장 주소
        val isOpened: Boolean,      // 주차장 운영 상태
        val distance: Int,          // 남은 거리
        val position: GPSObject     // 위도 경도
)

data class ParkingDetailData(
        val id: String,                 // id 값
        val defaultCost: Array<Int?>,    // 기본 요금 [0] = 시간 [1] = 가격
        val moreCost: Array<Int?>,       // 추가 요금 [0] = 시간 [1] = 가격
        val weekdayTime: Array<String?>, // 평일 운영 시간 [0] = 오픈 [1] = 클로즈
        val satDayTime: Array<String?>,  // 토요일 운영 시간 [0] = 오픈 [1] = 클로즈
        val holidayTime: Array<String?>  // 공휴일 운영 시간 [0] = 오픈 [1] = 클로즈
)