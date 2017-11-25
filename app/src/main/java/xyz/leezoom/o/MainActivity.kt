package xyz.leezoom.o

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xyz.leezoom.o.view.OImageView
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val APP_DIR = "OImages"

    private var lastPress: Long = 0
    private val REQUEST_PICK_PICTURE = 0
    private val INIT_STATE = 1000
    private val LOADED_IMAGE = 1001

    private var imagePath = ""
    private var imageUri: Uri? = null
    private val pTag = "lastImage"
    private var preference: SharedPreferences? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        showView(INIT_STATE)
        preference = getSharedPreferences("OFUCK", MODE_PRIVATE)
        val lastImage = preference!!.getString(pTag, "")
        if (lastImage.isNotEmpty()) {
            imagePath = lastImage
            loadImage(lastImage)
            //imageUri = Uri.parse(lastImage)
            //loadImageFromUri(imageUri!!)
            showView(LOADED_IMAGE)
        }
    }


    private fun initView() {
        //setSupportActionBar(main_toolbar)
        val width = windowManager.defaultDisplay.width
        val height = windowManager.defaultDisplay.height
        //init the view size
        var mainLayoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(width, width)
        container.layoutParams = mainLayoutParams

        var ctrlLayoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(width, height - width - 150)
        ctrlLayoutParams.topMargin = width
        ctrl_container.layoutParams = ctrlLayoutParams

        //setup listeners
        cir_button.setOnClickListener {
            //saveImageToDir(getViewBitmap(photo_view), "OImage")
            photo_view.setStyle(OImageView.CIRCLE)
        }
        rec_button.setOnClickListener {
            photo_view.setStyle(OImageView.RECT)
        }
        container.setOnClickListener {
            Log.d(TAG, "Hello")
            pickUpPicture()
        }
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //Log.d(TAG, (progress + 50) + "")
                photo_view.setWhiteWidth(progress + 50)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQUEST_PICK_PICTURE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickUpPicture()
                } else {
                    toast("Permission denied.")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }

    private fun pickUpPicture() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PICK_PICTURE)
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PICK_PICTURE)
        } else {
            Log.d("image", "pick")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_PICTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_PICK_PICTURE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imagePath = getRealPathFromURI(data.data)
                    //val uri: Uri = data.data
                    saveImagePath(imagePath)
                    loadImage(imagePath)
                    Log.d(TAG, imagePath)
                    //saveImageUri(uri)
                    //loadImageFromUri(uri)
                    showView(LOADED_IMAGE)
                }
            }
        }
    }


    private fun saveImagePath(path: String) {
        preference = getSharedPreferences("OFUCK", MODE_PRIVATE)
        var editor :SharedPreferences.Editor = preference!!.edit()
        editor.putString(pTag, path)
        editor.apply()
    }


    private fun loadImage(path: String) {
        Glide.with(this)
                .load(path)
                .asBitmap()
                .skipMemoryCache(true)
                .into(photo_view)
    }

    /*
    private fun saveImageUri(uri: Uri) {
        preference = getSharedPreferences("OFUCK", MODE_PRIVATE)
        var editor :SharedPreferences.Editor = preference!!.edit()
        editor.putString(pTag, uri.toString())
        editor.apply()
    }

    private fun loadImageFromUri(uri: Uri) {
        photo_view.setImageURI(uri)
    }*/

    private fun showView(state: Int) {
        when(state) {
            LOADED_IMAGE -> {
                photo_view.visibility = VISIBLE
                hint_view.visibility = GONE
                container.isEnabled = false
            }
            INIT_STATE -> {
                photo_view.visibility = GONE
                hint_view.visibility = VISIBLE
            }

        }
    }

    override fun onBackPressed() {
        val nowPress = System.currentTimeMillis()
        if (nowPress - lastPress > 1000) {
            toast(getString(R.string.hint_press_again))
            lastPress = nowPress
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.about_menu -> {
                //about me
                Log.d(TAG, "about")
                startActivity<AboutActivity>()
            }
            R.id.save_menu -> {
                Log.d(TAG, "save")
                if (imagePath.isNotEmpty()) {
                    saveImageToDir(getViewBitmap(photo_view), APP_DIR)
                } else {
                    toast(getString(R.string.hint_select_image))
                }
            }
            R.id.open_menu -> {
                Log.d(TAG, "open")
                pickUpPicture()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
//        return File(result).absolutePath
        return result
    }
}

private operator fun Int.plus(s: String): String? {
    return this.toString() + s
}
