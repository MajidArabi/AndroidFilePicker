package ir.one_developer.filepickerlibrary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.file_picker.model.Media
import ir.one_developer.filepickerlibrary.databinding.FileLayoutBinding
import java.io.File

class FileAdapter : ListAdapter<Media, FileAdapter.VH>(Comparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(FileLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position).file)
    }

    inner class VH(
        private val binding: FileLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.ivImagePreview.setImageURI(file.toUri())
        }
    }

    companion object {
        private val Comparator = object : DiffUtil.ItemCallback<Media>() {
            override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
                return oldItem == newItem
            }
        }
    }

}