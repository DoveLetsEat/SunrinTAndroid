package kr.saintdev.sunrint.models.manager.gps

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kr.saintdev.sunrint.models.func.PermissionCheck
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent



class GPSManager(val context: Context, val listener: GPSUpdateListener): LocationListener {
    private val MIN_DISTANCE_CHAGE_DELAY = 10F   // 10 미터
    private val MIN_TIME_UPDATE_DELAY = 10000L   // 10 초

    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * 위치정보 수신을 시작합니다.
     */
    fun startListening() {
        // add listener
        try {
            if (PermissionCheck.isGPSPermissionGranted(context)) {
                // 권한이 있슴
                this.locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_UPDATE_DELAY,
                        MIN_DISTANCE_CHAGE_DELAY,
                        this
                )

                this.locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_UPDATE_DELAY,
                        MIN_DISTANCE_CHAGE_DELAY,
                        this
                )
            }
            listener.onStart()
        } catch(ex: SecurityException) {
            listener.onStartFailed(ex)
        }
    }

    /**
     * 위치정보 수신을 중지합니다.
     */
    fun stopListening() {
        this.locationManager.removeUpdates(this)
    }

    /**
     * 가장 최근 GPS 정보를 가져옵니다.
     */
    fun getLastLocation(): GPSObject? {
        val location = try {
            // GPS 를 통한 가장 최신 정보 가져오기
            var location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if(location == null) {
                // gps provider is null.
                location = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            location
        } catch(ex: SecurityException) { null }
        catch (ex2: Exception) { null }

        return if(location != null) {
            GPSObject(location)
        } else {
            null
        }
    }


    /**
     * Location Change Listener
     */
    private var isFirst = true
    override fun onLocationChanged(location: Location?) {
        if(location != null) {
            if(isFirst) {
                listener.onFirstUpdate()
                isFirst = false
            } else listener.onUpdated()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}
}

/**
 * GPS 활성화를 요청한다.
 */
fun openGPSSettings(context: Context) {
    val gpsOptionsIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(gpsOptionsIntent)
}