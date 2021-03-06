package com.cmpe451.platon.util

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import com.cmpe451.platon.R
import java.util.*


/**
 * Class to keep definitive and serial data.
 */
class Definitions {

    companion object {
        const val API_URL="http://18.185.75.161:5000/"
    }

    //class User(val id: Int, val name: String, val surname: String, val rating: Double, val bio: String)


    enum class USERSTATUS {
        FOLLOWING, NOT_FOLLOWING, REQUESTED
    }

    fun getRealPathFromUri( context:Context,  contentUri: Uri):String {
        var cursor:Cursor? = null
        try {
            cursor = context.contentResolver.query(contentUri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
            cursor!!.moveToFirst()
            return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        } finally {
            cursor?.close()
        }
    }


    fun vibrate(ms: Long = 50, activity: Activity){
        val vib = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun createProgressBar(context: Context):AlertDialog{
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(R.layout.progress_bar)
        return dialogBuilder.create()
    }
}
