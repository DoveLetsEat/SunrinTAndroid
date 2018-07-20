package kr.saintdev.sunrint.models.func

import android.content.Context
import android.graphics.BitmapFactory

/**
 * extend functions
 */
fun Int.strCompat(context: Context) = context.getString(this)
fun Int.colorCompat(context: Context) = context.resources.getColor(this)
fun Int.strArrayCompat(context: Context) = context.resources.getStringArray(this)
fun Int.bitmapCompat(context: Context) = BitmapFactory.decodeResource(context.resources, this)