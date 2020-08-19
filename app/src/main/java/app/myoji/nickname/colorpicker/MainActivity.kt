package app.myoji.nickname.colorpicker

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // imageViewがタッチされているときこの中が実行される
        imageView.setOnTouchListener { view, motionEvent ->
            val image = view as ImageView
            // imageViewに設定されている画像本体(Bitmap)を取得
            val bitmap = Bitmap.createBitmap(image.drawable.toBitmap())

            // ImageViewの横幅をBitmapの大きさに合わせて正規化する
            val ratio = bitmap.width.toFloat() / image.width.toFloat()

            // タップしているポジションx, yを取得
            var x = (motionEvent.x * ratio).toInt()
            var y = (motionEvent.y * ratio).toInt()

            /*
             画像の最小または最大サイズをx, yが超える場合はエラーで落ちてしまうので
             上限, 下限の値を入れるようにする
             */
            if (x < 0) {
                x = 0
            } else if (x > bitmap.width - 1) {
                x = bitmap.width - 1
            }

            if (y < 0) {
                y = 0
            } else if (y > bitmap.height - 1) {
                y = bitmap.height - 1
            }

            // bitmap(画像本体)のx,yの地点のpixel情報を取得
            val pixel = bitmap.getPixel(x, y)
            // textViewにRGBの値をsetTextする
            textView.text = "(${pixel.red}, ${pixel.green}, ${pixel.blue})"
            // pixelが色情報を持っているため、文字や背景の色をpixelと同じにする
            textView.setTextColor(pixel)
            colorView.setBackgroundColor(pixel)
            // positionを表示
            positionTextView.text = "x: $x, y: $y"

            true

        }
    }
}