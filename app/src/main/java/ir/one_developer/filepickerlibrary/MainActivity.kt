package ir.one_developer.filepickerlibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.file_picker.FileType
import com.github.file_picker.adapter.ItemAdapter
import com.github.file_picker.extension.showFilePicker
import com.github.file_picker.listener.OnItemClickListener
import com.github.file_picker.listener.OnSubmitClickListener
import com.github.file_picker.model.Media
import ir.one_developer.filepickerlibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FileAdapter
    private val selectedFiles = arrayListOf<Media>()
    private lateinit var binding: ActivityMainBinding

    private var fileType: FileType = FileType.IMAGE
    private var accentColor: Int = R.color.purple_500
    private var spanCount: Int = 2
    private var limit: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() = binding.apply {
        adapter = FileAdapter()
        rvFiles.adapter = adapter
        btnOpenFiles.setOnClickListener {
            showFiles()
        }
        radioGroups.setOnCheckedChangeListener { _, i ->
            fileType = when (i) {
                1 -> {
                    spanCount = 2
                    limit = 7
                    accentColor = R.color.purple_500
                    FileType.IMAGE
                }
                2 -> {
                    spanCount = 2
                    limit = 3
                    accentColor = R.color.pink_500
                    FileType.VIDEO
                }
                3 -> {
                    spanCount = 3
                    limit = 1
                    accentColor = R.color.blue_500
                    FileType.AUDIO
                }
                else -> FileType.IMAGE
            }
        }
    }

    private fun showFiles(): Unit = showFilePicker(
        fileType = fileType,
        limitItemSelection = limit,
        gridSpanCount = spanCount,
        selectedFiles = selectedFiles,
        accentColor = ContextCompat.getColor(this@MainActivity, accentColor),
        titleTextColor = ContextCompat.getColor(this@MainActivity, accentColor),
        onSubmitClickListener = object : OnSubmitClickListener {
            override fun onClick(files: List<Media>) {
                adapter.submitList(files)
                updateSelectedFiles(files)
            }
        },
        onItemClickListener = object : OnItemClickListener {
            override fun onClick(media: Media, position: Int, adapter: ItemAdapter) {
                if (!media.file.isDirectory) {
                    adapter.setSelected(position)
                }
            }
        }
    )

    private fun updateSelectedFiles(files: List<Media>) {
        selectedFiles.clear()
        selectedFiles.addAll(files)
    }

}