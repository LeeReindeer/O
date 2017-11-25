package xyz.leezoom.o

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

/**
 * Request permission
 */
fun Activity.requestPermission(permission: String, requestCOde: Int) {
    ActivityCompat.requestPermissions(this,  arrayOf(permission), requestCOde)
}

/**
 * Get view pic from cache
 */
fun Activity.getViewBitmap(v: View): Bitmap {
    v.isDrawingCacheEnabled = true
    v.buildDrawingCache()
    val bitmap = Bitmap.createBitmap(v.drawingCache, 0, 0, v.measuredWidth, v.measuredHeight)
    v.isDrawingCacheEnabled = false
    v.destroyDrawingCache()
    return bitmap
}

/**
 * Save bitmap to app dir
 */
fun Activity.saveImageToDir(bitmap: Bitmap, dir:String){
    var save = false
    Log.d("File: ", Environment.getExternalStorageDirectory().absolutePath)
    val pictureDir  = File(Environment.getExternalStorageDirectory().absolutePath, "Pictures")
    val appDir = File(pictureDir, dir)
    if (!pictureDir.exists()) {
        pictureDir.mkdir()
    }
    if(!appDir.exists()){
        appDir.mkdir()
    }
    val fileName: String = System.currentTimeMillis().toString() + ".jpg"
    val file = File(appDir, fileName)
    try {
        val out = FileOutputStream(file)
        save = bitmap.compress(Bitmap.CompressFormat.JPEG,100, out)
        out.flush()
        out.close()
    }catch (e: Exception){
        save = false
        e.printStackTrace()
    }

    if(save){
        Toast.makeText(applicationContext, "Saved to $appDir", Toast.LENGTH_SHORT).show()
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.path)))
    }else{
        Toast.makeText(applicationContext, getString(R.string.hint_save_fail), Toast.LENGTH_SHORT).show()
    }
}