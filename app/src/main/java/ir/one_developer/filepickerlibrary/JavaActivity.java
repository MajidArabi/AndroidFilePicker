package ir.one_developer.filepickerlibrary;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.file_picker.FilePicker;
import com.github.file_picker.model.Media;

import java.util.ArrayList;
import java.util.List;

import ir.one_developer.filepickerlibrary.databinding.ActivityJavaBinding;

public class JavaActivity extends AppCompatActivity {

    private FileAdapter adapter;
    private ActivityJavaBinding binding;
    private final List<Media> selectedFiles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJavaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupViews();
    }

    private void setupViews() {
        adapter = new FileAdapter();
        binding.rvFiles.setAdapter(adapter);
        binding.fab.setOnClickListener(v -> finish());
        binding.btnOpenFiles.setOnClickListener(v -> showFiles());
    }

    private void showFiles() {
        new FilePicker.Builder(this)
                .setLimitItemSelection(3)
                .setAccentColor(Color.CYAN)
                .setCancellable(true)
                .setSelectedFiles(selectedFiles)
                .setOnSubmitClickListener(files -> {
                    adapter.submitList(files);
                    updateSelectedFiles(files);
                })
                .setOnItemClickListener((media, pos, adapter) -> {
                    if (!media.getFile().isDirectory()) {
                        adapter.setSelected(pos);
                    }
                })
                .buildAndShow();
    }

    private void updateSelectedFiles(List<Media> files) {
        selectedFiles.clear();
        selectedFiles.addAll(files);
    }

}