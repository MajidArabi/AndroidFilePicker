package ir.one_developer.file_picker.adapter

import android.util.Size
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.load
import coil.request.videoFrameMillis
import ir.one_developer.file_picker.databinding.ItemLayoutBinding
import ir.one_developer.file_picker.isVideo
import ir.one_developer.file_picker.model.Media
import ir.one_developer.file_picker.size
import java.io.File

class ItemVH(
    private val listener: ((Int) -> Unit)?,
    private val binding: ItemLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var imageLoader: ImageLoader

    init {
        binding.card.setOnClickListener {
            listener?.invoke(bindingAdapterPosition)
        }
        val context = binding.root.context
        imageLoader = ImageLoader.Builder(context)
            .componentRegistry {
                add(VideoFrameFileFetcher(context))
                add(VideoFrameUriFetcher(context))
                add(VideoFrameDecoder(context))
            }
            .build()
    }

    fun bind(item: Media) = binding.apply {
        ivImage.alpha = .75f
        ivImage.load(item.file, imageLoader = imageLoader) {
            crossfade(enable = true)
            if (item.file.name.isVideo()) {
                videoFrameMillis(1000)
            }
        }
        tvPath.text = getFilePathName(item.file)
        tvFileSize.text = item.file.size()

        ivChecked.isVisible = item.isSelected
        playIconContainer.isVisible = item.file.name.isVideo()

    }

    private fun getFilePathName(file: File): CharSequence {
        val paths = file.path.split("/")
        paths.forEachIndexed { index, title ->
            if (index == paths.lastIndex - 1) {
                return title
            }
        }
        return ""
    }

}