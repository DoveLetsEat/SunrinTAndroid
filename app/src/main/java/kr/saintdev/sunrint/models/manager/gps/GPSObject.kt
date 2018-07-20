package kr.saintdev.sunrint.models.manager.gps

import android.location.Location
import android.util.Log
import com.google.android.gms.common.config.GservicesValue.init

class GPSObject(lat: Double, lng: Double) {
    constructor(location: Location) : this(location.latitude, location.longitude)

    val latitude: Double = lat   // 위도
    val longitude: Double = lng  // 경도
}