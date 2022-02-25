package ir.one_developer.filepickerlibrary

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ir.one_developer.file_picker.FilePicker
import ir.one_developer.file_picker.FileType
import ir.one_developer.file_picker.ListDirection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var video = false
        findViewById<TextView>(R.id.textView).setOnClickListener {
            FilePicker.show(
                activity = this,
                gridSpanCount = 3,
                limitItemSelection = 3,
                fileType = if (video) FileType.VIDEO else FileType.IMAGE,
                listDirection = ListDirection.RTL,
            ) {
                Log.e("F_PATH", "$it")
            }
            video = !video
        }

    }
}