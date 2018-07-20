package kr.saintdev.sunrint.views.activitys

import android.app.ProgressDialog.show
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.maps.model.Marker
import kr.saintdev.sunrint.R

/**
 * DO NOT OPEN THIS ACTIVITY!!
 */

open class SuperActivity : AppCompatActivity() {
    /**
     * extend functions
     */
    fun Int.str() = getString(this)
    fun Int.color() = resources.getColor(this)
    fun Int.strArray() = resources.getStringArray(this)
    fun Int.bitmap() = BitmapFactory.decodeResource(resources, this)


    private var progressDialog: MaterialDialog? = null
    fun openProgressDialog(message: String = "Loading") {
        val builder = MaterialDialog.Builder(this)
        builder.title("Loading ...")
        builder.content(message)
        builder.progress(true, 0)
        this.progressDialog = builder.show()
    }

    fun closeProgressDialog() {
        val tmpDialog = this.progressDialog

        if(tmpDialog != null && tmpDialog.isShowing) {
            // dialog 를 닫는다.
            tmpDialog.dismiss()
            this.progressDialog = null
        }
    }

    fun openMessageDialog(titie: String, content: String, listener: DialogInterface.OnDismissListener? = null) {
        val builder = MaterialDialog.Builder(this)
                .title(titie)
                .content(content)
                .positiveText("OK")

        if(listener != null) {
            builder.dismissListener(listener)
        }

        builder.show()
    }
}