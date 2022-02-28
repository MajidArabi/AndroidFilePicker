package com.github.file_picker

import android.Manifest
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.file_picker.adapter.ItemAdapter
import com.github.file_picker.extension.getStorageFiles
import com.github.file_picker.extension.hasPermission
import com.github.file_picker.model.Media
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.one_developer.file_picker.R
import ir.one_developer.file_picker.databinding.FilePickerBinding
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

    private var selectedFiles = arrayListOf<Media>()
    private var limitCount: Int = DEFAULT_LIMIT_COUNT
    private var fileType: FileType = DEFAULT_FILE_TYPE
    private var gridSpanCount: Int = DEFAULT_SPAN_COUNT
    private var cancellable: Boolean = DEFAULT_CANCELABLE
    private var listDirection: ListDirection = DEFAULT_LIST_DIRECTION

    private var title: String = DEFAULT_TITLE
    private var titleTextColor: Int = DEFAULT_TITLE_TEXT_COLOR

    private var submitText: String = DEFAULT_SUBMIT_TEXT
    private var submitTextColor: Int = DEFAULT_SUBMIT_TEXT_COLOR

    private var accentColor: Int = DEFAULT_ACCENT_COLOR

    private var requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) loadFiles()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
        arguments?.let {
            cancellable = it.getBoolean(CANCELLABLE_KEY)
            gridSpanCount = it.getInt(GRID_SPAN_COUNT_KEY)

            title = it.getString(TITLE_KEY) ?: DEFAULT_TITLE
            titleTextColor = it.getInt(TITLE_TEXT_COLOR_KEY)

            submitText = it.getString(SUBMIT_TEXT_KEY) ?: DEFAULT_SUBMIT_TEXT
            submitTextColor = it.getInt(SUBMIT_TEXT_COLOR_KEY)
            accentColor = it.getInt(ACCENT_COLOR_KEY)

            limitCount = it.getInt(LIMIT_ITEM_SELECTION_COUNT_KEY)
            fileType = it.getParcelable(FILE_TYPE_KEY) ?: DEFAULT_FILE_TYPE
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
        val readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (!hasPermission(readStoragePermission)) {
            requestPermission(readStoragePermission)
            return
        }
        loadFiles()
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
        requestPermission.unregister()
    }

    private fun requestPermission(permission: String) = requestPermission.launch(permission)

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

        btnSubmit.text = submitText
        cardLine.setCardBackgroundColor(ColorStateList.valueOf(accentColor))
        progress.indeterminateTintList = ColorStateList.valueOf(accentColor)
        tvTitle.apply {
            text = title
            setTextColor(titleTextColor)
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
            setAccentColor(accentColor)
        }
        rvFiles.apply {
            layoutDirection = when (listDirection) {
                ListDirection.LTR -> RecyclerView.LAYOUT_DIRECTION_LTR
                ListDirection.RTL -> RecyclerView.LAYOUT_DIRECTION_RTL
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
                    setTextColor(submitTextColor)
                    setBackgroundColor(accentColor)
                } else {
                    setTextColor(Color.GRAY)
                    setBackgroundColor(Color.LTGRAY)
                }
            }
        }
    }

    private fun loadFiles() = CoroutineScope(Dispatchers.IO).launch {
        val files = getStorageFiles(fileType = fileType)
            .map { Media(it) }
            .sortedByDescending { it.file.lastModified() }
        requireActivity().runOnUiThread {
            itemAdapter?.submitList(files)
            binding.progress.isVisible = false
            setFixedSubmitButton()
            showSelectedCount()
        }
    }

    private fun submitList() = getSelectedItems()?.let { selectedFilesListener(it) }

    private fun getSelectedItems(): List<Media>? =
        itemAdapter?.currentList?.filter { it.isSelected }

    private fun hasSelectedItem(): Boolean = !getSelectedItems().isNullOrEmpty()

    companion object {
        private const val TAG = "FilePicker"

        // Defaults
        const val DEFAULT_SPAN_COUNT = 2
        const val DEFAULT_LIMIT_COUNT = 1
        const val DEFAULT_CANCELABLE = true
        val DEFAULT_FILE_TYPE = FileType.IMAGE
        val DEFAULT_LIST_DIRECTION = ListDirection.LTR

        const val DEFAULT_ACCENT_COLOR = Color.BLACK
        const val DEFAULT_TITLE = "Choose File"
        const val DEFAULT_TITLE_TEXT_COLOR = DEFAULT_ACCENT_COLOR

        const val DEFAULT_SUBMIT_TEXT = "Submit"
        const val DEFAULT_SUBMIT_TEXT_COLOR = Color.WHITE

        // Keys
        private const val TITLE_KEY = "title"
        private const val TITLE_TEXT_COLOR_KEY = "title.text.color"

        private const val SUBMIT_TEXT_KEY = "submit.text"
        private const val SUBMIT_TEXT_COLOR_KEY = "submit.text.color"

        private const val FILE_TYPE_KEY = "file.type"
        private const val CANCELLABLE_KEY = "cancelable"
        private const val SELECTED_FILES_KEY = "selected"

        private const val ACCENT_COLOR_KEY = "accent.color"
        private const val GRID_SPAN_COUNT_KEY = "span.count"
        private const val LIST_DIRECTION_KEY = "list.direction"
        private const val LIMIT_ITEM_SELECTION_COUNT_KEY = "limit"

        private var selectedFilesListener: (files: List<Media>) -> Unit = { }
        private var isShown: Boolean = false

        /**
         * Show file picker
         *
         * @param activity
         * @param title
         * @param fileType
         * @param gridSpanCount
         * @param submitText
         * @param cancellable
         * @param limitItemSelection
         * @param selectedFiles
         * @param listDirection
         * @param selectedFilesListener
         * @receiver
         */
        @JvmStatic
        fun show(
            activity: AppCompatActivity,
            title: String = DEFAULT_TITLE,
            titleTextColor: Int = DEFAULT_TITLE_TEXT_COLOR,
            submitText: String = DEFAULT_SUBMIT_TEXT,
            submitTextColor: Int = DEFAULT_SUBMIT_TEXT_COLOR,
            accentColor: Int = DEFAULT_ACCENT_COLOR,
            cancellable: Boolean = DEFAULT_CANCELABLE,
            gridSpanCount: Int = DEFAULT_SPAN_COUNT,
            limitItemSelection: Int = DEFAULT_LIMIT_COUNT,
            fileType: FileType = DEFAULT_FILE_TYPE,
            listDirection: ListDirection = DEFAULT_LIST_DIRECTION,
            selectedFiles: ArrayList<Media> = arrayListOf(),
            selectedFilesListener: (files: List<Media>) -> Unit,
        ) {
            if (isShown) return
            this.selectedFilesListener = selectedFilesListener
            FilePicker().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, title)
                    putInt(TITLE_TEXT_COLOR_KEY, titleTextColor)
                    putString(SUBMIT_TEXT_KEY, submitText)
                    putInt(SUBMIT_TEXT_COLOR_KEY, submitTextColor)
                    putInt(ACCENT_COLOR_KEY, accentColor)
                    putInt(GRID_SPAN_COUNT_KEY, gridSpanCount)
                    putBoolean(CANCELLABLE_KEY, cancellable)
                    putParcelable(FILE_TYPE_KEY, fileType)
                    putParcelable(LIST_DIRECTION_KEY, listDirection)
                    putParcelableArrayList(SELECTED_FILES_KEY, selectedFiles)
                    putInt(LIMIT_ITEM_SELECTION_COUNT_KEY, limitItemSelection)
                }
            }.show(activity.supportFragmentManager, TAG)
        }
    }

}

/**
 * Show file picker
 *
 * @param title
 * @param submitText
 * @param fileType
 * @param listDirection
 * @param cancellable
 * @param gridSpanCount
 * @param limitItemSelection
 * @param selectedFiles
 * @param selectedFilesListener
 * @receiver
 */
fun AppCompatActivity.showFilePicker(
    title: String = FilePicker.DEFAULT_TITLE,
    titleTextColor: Int = FilePicker.DEFAULT_TITLE_TEXT_COLOR,
    submitText: String = FilePicker.DEFAULT_SUBMIT_TEXT,
    submitTextColor: Int = FilePicker.DEFAULT_SUBMIT_TEXT_COLOR,
    accentColor: Int = FilePicker.DEFAULT_ACCENT_COLOR,
    fileType: FileType = FilePicker.DEFAULT_FILE_TYPE,
    listDirection: ListDirection = FilePicker.DEFAULT_LIST_DIRECTION,
    cancellable: Boolean = FilePicker.DEFAULT_CANCELABLE,
    gridSpanCount: Int = FilePicker.DEFAULT_SPAN_COUNT,
    limitItemSelection: Int = FilePicker.DEFAULT_LIMIT_COUNT,
    selectedFiles: ArrayList<Media> = arrayListOf(),
    selectedFilesListener: (files: List<Media>) -> Unit,
) {
    FilePicker.show(
        activity = this,
        title = title,
        titleTextColor = titleTextColor,
        submitText = submitText,
        submitTextColor = submitTextColor,
        accentColor = accentColor,
        fileType = fileType,
        listDirection = listDirection,
        cancellable = cancellable,
        gridSpanCount = gridSpanCount,
        limitItemSelection = limitItemSelection,
        selectedFiles = selectedFiles,
        selectedFilesListener = selectedFilesListener
    )
}

/**
 * Show file picker
 *
 * @param title
 * @param submitText
 * @param fileType
 * @param listDirection
 * @param cancellable
 * @param gridSpanCount
 * @param limitItemSelection
 * @param selectedFiles
 * @param selectedFilesListener
 * @receiver
 */
fun Fragment.showFilePicker(
    title: String = FilePicker.DEFAULT_TITLE,
    titleTextColor: Int = FilePicker.DEFAULT_TITLE_TEXT_COLOR,
    submitText: String = FilePicker.DEFAULT_SUBMIT_TEXT,
    submitTextColor: Int = FilePicker.DEFAULT_SUBMIT_TEXT_COLOR,
    accentColor: Int = FilePicker.DEFAULT_ACCENT_COLOR,
    fileType: FileType = FilePicker.DEFAULT_FILE_TYPE,
    listDirection: ListDirection = FilePicker.DEFAULT_LIST_DIRECTION,
    cancellable: Boolean = FilePicker.DEFAULT_CANCELABLE,
    gridSpanCount: Int = FilePicker.DEFAULT_SPAN_COUNT,
    limitItemSelection: Int = FilePicker.DEFAULT_LIMIT_COUNT,
    selectedFiles: ArrayList<Media> = arrayListOf(),
    selectedFilesListener: (files: List<Media>) -> Unit,
) {
    if (requireActivity() !is AppCompatActivity) {
        throw IllegalAccessException("Fragment host must be extend AppCompatActivity")
    }
    (requireActivity() as AppCompatActivity).showFilePicker(
        title = title,
        titleTextColor = titleTextColor,
        submitText = submitText,
        submitTextColor = submitTextColor,
        accentColor = accentColor,
        fileType = fileType,
        listDirection = listDirection,
        cancellable = cancellable,
        gridSpanCount = gridSpanCount,
        limitItemSelection = limitItemSelection,
        selectedFiles = selectedFiles,
        selectedFilesListener = selectedFilesListener
    )
}