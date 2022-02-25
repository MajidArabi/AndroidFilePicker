package com.github.file_picker

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.github.file_picker.adapter.ItemAdapter
import ir.one_developer.file_picker.databinding.FilePickerBinding
import com.github.file_picker.model.Media
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    FilePicker.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class FilePicker : BottomSheetDialogFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FilePickerBinding? = null

    private var itemAdapter: ItemAdapter? = null

    private var title: String = DEFAULT_TITLE
    private var selectedFiles = arrayListOf<Media>()
    private var limitCount: Int = DEFAULT_LIMIT_COUNT
    private var fileType: FileType = DEFAULT_FILE_TYPE
    private var gridSpanCount: Int = DEFAULT_SPAN_COUNT
    private var submitText: String = DEFAULT_SUBMIT_TEXT
    private var cancellable: Boolean = DEFAULT_CANCELABLE
    private var listDirection: ListDirection = DEFAULT_LIST_DIRECTION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cancellable = it.getBoolean(CANCELLABLE_KEY)
            gridSpanCount = it.getInt(GRID_SPAN_COUNT_KEY)
            title = it.getString(TITLE_KEY) ?: DEFAULT_TITLE
            limitCount = it.getInt(LIMIT_ITEM_SELECTION_COUNT_KEY)
            fileType = it.getParcelable(FILE_TYPE_KEY) ?: DEFAULT_FILE_TYPE
            submitText = it.getString(SUBMIT_TEXT_KEY) ?: DEFAULT_SUBMIT_TEXT
            listDirection = it.getParcelable(LIST_DIRECTION_KEY) ?: DEFAULT_LIST_DIRECTION
            selectedFiles = it.getParcelableArrayList(SELECTED_FILES_KEY) ?: arrayListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setCancellableDialog(cancellable)
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val files = loadFiles()
            requireActivity().runOnUiThread {
                itemAdapter?.submitList(files)
                binding.progress.isVisible = false
                setFixedSubmitButton()
                showSelectedCount()
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isShown) return
        super.show(manager, tag)
        isShown = true
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShown = false
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCancellableDialog(cancellable: Boolean) {
        dialog?.setCancelable(cancellable)
        dialog?.setCanceledOnTouchOutside(cancellable)
    }

    private fun setFixedSubmitButton() {
        val behavior: BottomSheetBehavior<*> = (dialog as BottomSheetDialog).behavior
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val button = binding.buttonContainer
                button.y =
                    ((bottomSheet.parent as View).height - bottomSheet.top - button.height).toFloat()
            }
        }.apply {
            val root = binding.root
            root.post { onSlide(root.parent as View, 0f) }
        })
    }

    private fun setupViews() = binding.apply {
        setupRecyclerView(rvFiles)
        setFixedSubmitButton()
        showSelectedCount()
        setButtonEnabled()
        tvTitle.text = title
        btnSubmit.text = submitText
        ivClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
        btnSubmit.setOnClickListener {
            submitList()
            dismissAllowingStateLoss()
        }
    }

    private fun showSelectedCount() {
        val selectedCount = getSelectedItems()?.size ?: 0
        binding.tvTitle.text = "$title ($selectedCount/$limitCount)"
    }

    private fun setupRecyclerView(rvFiles: RecyclerView) {
        itemAdapter = ItemAdapter().apply {
            setOnItemClickListener { itemPosition ->
                itemAdapter?.setSelect(itemPosition)
                showSelectedCount()
                setButtonEnabled()
            }
            setLimitCount(limitCount)
        }
        rvFiles.apply {
            layoutDirection = when (listDirection) {
                is ListDirection.LTR -> RecyclerView.LAYOUT_DIRECTION_LTR
                is ListDirection.RTL -> RecyclerView.LAYOUT_DIRECTION_RTL
            }
            layoutManager = GridLayoutManager(requireContext(), gridSpanCount)
            adapter = itemAdapter
        }
    }

    private fun setButtonEnabled() = view?.post {
        binding.apply {
            val hasSelected = hasSelectedItem()
            btnSubmit.apply {
                isEnabled = hasSelected
                if (isEnabled) {
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.BLACK)
                } else {
                    setTextColor(Color.GRAY)
                    setBackgroundColor(Color.LTGRAY)
                }
            }
        }
    }

    private fun loadFiles(): List<Media> = requireActivity()
        .getStorageFiles(fileType = fileType)
        .map { Media(it) }
        .sortedByDescending { it.file.lastModified() }

    private fun submitList() = getSelectedItems()?.let { selectedFilesListener(it) }

    private fun getSelectedItems(): List<Media>? =
        itemAdapter?.currentList?.filter { it.isSelected }

    private fun hasSelectedItem(): Boolean = !getSelectedItems().isNullOrEmpty()

    companion object {
        private const val TAG = "FilePicker"
        const val DEFAULT_LIMIT_COUNT = 1
        private const val DEFAULT_SPAN_COUNT = 2
        private const val DEFAULT_CANCELABLE = true
        private const val DEFAULT_TITLE = "Choose File"
        private const val DEFAULT_SUBMIT_TEXT = "Submit"
        private val DEFAULT_FILE_TYPE = FileType.IMAGE
        private val DEFAULT_LIST_DIRECTION = ListDirection.LTR

        private const val TITLE_KEY = "title"
        private const val FILE_TYPE_KEY = "file.type"
        private const val CANCELLABLE_KEY = "cancelable"
        private const val SELECTED_FILES_KEY = "selected"
        private const val SUBMIT_TEXT_KEY = "submit.title"
        private const val GRID_SPAN_COUNT_KEY = "span.count"
        private const val LIST_DIRECTION_KEY = "list.direction"
        private const val LIMIT_ITEM_SELECTION_COUNT_KEY = "limit"

        private var selectedFilesListener: (files: List<Media>) -> Unit = { }
        private var isShown: Boolean = false
        fun show(
            activity: AppCompatActivity,
            title: String = DEFAULT_TITLE,
            fileType: FileType = DEFAULT_FILE_TYPE,
            gridSpanCount: Int = DEFAULT_SPAN_COUNT,
            submitText: String = DEFAULT_SUBMIT_TEXT,
            cancellable: Boolean = DEFAULT_CANCELABLE,
            limitItemSelection: Int = DEFAULT_LIMIT_COUNT,
            selectedFiles: ArrayList<Media> = arrayListOf(),
            listDirection: ListDirection = DEFAULT_LIST_DIRECTION,
            selectedFilesListener: (files: List<Media>) -> Unit,
        ) {
            if (isShown) return
            Companion.selectedFilesListener = selectedFilesListener
            FilePicker().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, title)
                    putParcelable(FILE_TYPE_KEY, fileType)
                    putString(SUBMIT_TEXT_KEY, submitText)
                    putBoolean(CANCELLABLE_KEY, cancellable)
                    putInt(GRID_SPAN_COUNT_KEY, gridSpanCount)
                    putParcelable(LIST_DIRECTION_KEY, listDirection)
                    putParcelableArrayList(SELECTED_FILES_KEY, selectedFiles)
                    putInt(LIMIT_ITEM_SELECTION_COUNT_KEY, limitItemSelection)
                }
            }.show(activity.supportFragmentManager, TAG)
        }
    }

}