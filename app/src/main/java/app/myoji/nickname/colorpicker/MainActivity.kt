package app.myoji.nickname.colorpicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val CAMERA_REQUEST_CODE = 1
        const val CAMERA_PERMISSION_REQUEST_CODE = 2
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // imageViewがタッチされているときこの中が実行される
        imageView.setOnTouchListener { view, motionEvent ->
            val image = view as ImageView
            // imageViewに設定されている画像本体(Bitmap)を取得
            val bitmap = Bitmap.createBitmap(image.drawable.toBitmap())

            // ImageViewの横幅をBitmapの大きさに合わせて正規化する
            val ratio =
                if (bitmap.width >= bitmap.height) {
                    bitmap.width.toFloat() / image.width.toFloat()
                } else {
                    bitmap.width.toFloat() / image.width.toFloat()
                }

            // タップしているポジションx, yを取得
            val x = (motionEvent.x * ratio).toInt()
            val y = (motionEvent.y * ratio).toInt()

            /*
             画像の最小または最大サイズをx, yが超える場合はエラーで落ちてしまうので
             黒色を取得したとして処理する
             */
            if (x < 0 || x > bitmap.width - 1 || y < 0 || y > bitmap.height - 1) {
                val blackColor = Color.parseColor("#000000")
                rgbTextView.text = "(0, 0, 0)"
                rgbTextView.setTextColor(blackColor)
                codeTextView.text = "#000000"
                codeTextView.setTextColor(blackColor)
                colorView.setBackgroundColor(blackColor)
                return@setOnTouchListener true
            }

            // bitmap(画像本体)のx,yの地点のpixel情報を取得
            val pixel = bitmap.getPixel(x, y)
            // textViewにRGBの値をsetTextする
            rgbTextView.text = "(${pixel.red}, ${pixel.green}, ${pixel.blue})"
            // pixelが色情報を持っているため、文字や背景の色をpixelと同じにする
            rgbTextView.setTextColor(pixel)
            colorView.setBackgroundColor(pixel)
            // カラーコードをsetTextする
            codeTextView.text = String.format("#%06X", (0xFFFFFF and pixel))
            codeTextView.setTextColor(pixel)

            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.camera -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                    if (checkCameraPermission()) {
                        takePicture()
                    } else {
                        grantCameraPermission()
                    }
                } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val image = data?.extras?.get("data")?.let {
                imageView.setImageBitmap(it as Bitmap)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)


    private fun grantCameraPermission() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                takePicture()
            }
        }
    }
}