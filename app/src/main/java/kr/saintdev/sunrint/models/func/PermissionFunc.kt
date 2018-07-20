package kr.saintdev.sunrint.models.func

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import kr.saintdev.sunrint.views.activitys.SuperActivity

object PermissionCheck {

    /**
     * GPS 권한이 있는지 확인합니다.
     * @return Boolean true if granted
     */
    fun isGPSPermissionGranted(context: Context) =
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /**
     * GPS 가 켜져있는지 확인한다.
     * @return Boolean true if gps is online
     */
    fun isGPSEnabled(context: Context): Boolean {
        var manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled( LocationManager.GPS_PROVIDER )
    }
}

object PermissionGrant {
    /**
     * GPS 권한을 요청합니다.
     */
    fun requestGPSPermission(activity: SuperActivity, requestCode: Int) {
        if(!PermissionCheck.isGPSPermissionGranted(activity)) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
        }
    }
}