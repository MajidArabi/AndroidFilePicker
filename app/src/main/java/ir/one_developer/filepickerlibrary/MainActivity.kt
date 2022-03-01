package ir.one_developer.filepickerlibrary

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.file_picker.model.Media
import com.github.file_picker.showFilePicker
import ir.one_developer.filepickerlibrary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FileAdapter
    private val selectedFiles = arrayListOf<Media>()
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
    }

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
    }

    private fun showFiles(): Unit = showFilePicker(
        limitItemSelection = 2,
        selectedFiles = selectedFiles,
        accentColor = ContextCompat.getColor(this@MainActivity, R.color.purple_700),
        titleTextColor = ContextCompat.getColor(this@MainActivity, R.color.purple_700)
    ) {
        adapter.submitList(it)
        updateSelectedFiles(it)
        Log.i(TAG, "FilePicker:SelectedItems: $selectedFiles")
    }

    private fun updateSelectedFiles(files: List<Media>) {
        selectedFiles.clear()
        selectedFiles.addAll(files)
    }

}