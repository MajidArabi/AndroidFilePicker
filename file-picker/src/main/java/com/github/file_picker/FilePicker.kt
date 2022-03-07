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
import com.github.file_picker.listener.OnItemClickListener
import com.github.file_picker.listener.OnSubmitClickListener
import com.github.file_picker.model.Media
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.one_developer.file_picker.R
import ir.one_developer.file_picker.databinding.FilePickerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    FilePicker.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class FilePicker private constructor(
    builder: Builder
) : BottomSheetDialogFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FilePickerBinding? = null

    private var itemAdapter: ItemAdapter? = null

    private var title: String
    private var titleTextColor by Delegates.notNull<Int>()
    private var submitText: String
    private var submitTextColor by Delegates.notNull<Int>()
    private var selectedFiles: List<Media>
    private var fileType: FileType
    private var listDirection: ListDirection
    private var cancellable by Delegates.notNull<Boolean>()
    private var gridSpanCount by Delegates.notNull<Int>()
    private var limitCount by Delegates.notNull<Int>()
    private var accentColor by Delegates.notNull<Int>()

    private var onItemClickListener: OnItemClickListener?
    private var onSubmitClickListener: OnSubmitClickListener?

    init {
        this.title = builder.title
        this.titleTextColor = builder.titleTextColor
        this.submitText = builder.submitText
        this.submitTextColor = builder.submitTextColor
        this.selectedFiles = builder.selectedFiles
        this.fileType = builder.fileType
        this.listDirection = builder.listDirection
        this.cancellable = builder.cancellable
        this.gridSpanCount = builder.gridSpanCount
        this.limitCount = builder.limitCount
        this.accentColor = builder.accentColor
        this.onItemClickListener = builder.onItemClickListener
        this.onSubmitClickListener = builder.onSubmitClickListener
    }

    private var requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) loadFiles()
            else dismissAllowingStateLoss()
        }

    class Builder(
        private val appCompatActivity: AppCompatActivity
    ) {
        var title: String = DEFAULT_TITLE
            private set
        var titleTextColor: Int = DEFAULT_TITLE_TEXT_COLOR
            private set
        var submitText: String = DEFAULT_SUBMIT_TEXT
            private set
        var submitTextColor: Int = DEFAULT_SUBMIT_TEXT_COLOR
            private set
        var accentColor: Int = DEFAULT_ACCENT_COLOR
            private set
        var selectedFiles: List<Media> = arrayListOf()
            private set
        var limitCount: Int = DEFAULT_LIMIT_COUNT
            private set
        var fileType: FileType = DEFAULT_FILE_TYPE
            private set
        var gridSpanCount: Int = DEFAULT_SPAN_COUNT
            private set
        var cancellable: Boolean = DEFAULT_CANCELABLE
            private set
        var listDirection: ListDirection = DEFAULT_LIST_DIRECTION
            private set
        var onItemClickListener: OnItemClickListener? = null
            private set
        var onSubmitClickListener: OnSubmitClickListener? = null
            private set

        /**
         * Set title
         *
         * @param title
         * @return
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * Set title text color
         *
         * @param color
         * @return
         */
        fun setTitleTextColor(color: Int): Builder {
            this.titleTextColor = color
            return this
        }

        /**
         * Set submit text
         *
         * @param text
         * @return
         */
        fun setSubmitText(text: String): Builder {
            this.submitText = text
            return this
        }

        /**
         * Set submit text color
         *
         * @param color
         * @return
         */
        fun setSubmitTextColor(color: Int): Builder {
            this.submitTextColor = color
            return this
        }

        /**
         * Set accent color
         *
         * @param color
         * @return
         */
        fun setAccentColor(color: Int): Builder {
            this.accentColor = color
            return this
        }

        /**
         * Set selected files, for show as selected style in list
         *
         * @param selectedFiles
         * @return
         */
        fun setSelectedFiles(selectedFiles: List<Media>): Builder {
            this.selectedFiles = selectedFiles
            return this
        }

        /**
         * Set limit item selection
         *
         * @param limit the limit item can select
         * @return
         */
        fun setLimitItemSelection(limit: Int): Builder {
            this.limitCount = limit
            return this
        }

        /**
         * Set file type
         *
         * @param fileType the [FileType]
         * @return
         */
        fun setFileType(fileType: FileType): Builder {
            this.fileType = fileType
            return this
        }

        /**
         * Set grid span count
         *
         * @param spanCount the list span count
         * @return
         */
        fun setGridSpanCount(spanCount: Int): Builder {
            this.gridSpanCount = spanCount
            return this
        }

        /**
         * Set cancellable
         *
         * @param cancellable
         * @return
         */
        fun setCancellable(cancellable: Boolean): Builder {
            this.cancellable = cancellable
            return this
        }

        /**
         * Set list direction
         *
         * @param listDirection [ListDirection]
         * @return
         */
        fun setListDirection(listDirection: ListDirection): Builder {
            this.listDirection = listDirection
            return this
        }

        /**
         * Set on submit click listener
         *
         * @param onSubmitClickListener
         * @return
         */
        fun setOnSubmitClickListener(onSubmitClickListener: OnSubmitClickListener?): Builder {
            this.onSubmitClickListener = onSubmitClickListener
            return this
        }

        /**
         * Set on item click listener
         *
         * @param onItemClickListener
         * @return
         */
        fun setOnItemClickListener(onItemClickListener: OnItemClickListener?): Builder {
            this.onItemClickListener = onItemClickListener
            return this
        }

        /**
         * Build file picker and show it
         *
         */
        fun buildAndShow() = FilePicker(
            this
        ).show(
            appCompatActivity.supportFragmentManager,
            "file.picker"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
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

    /**
     * Request permission
     *
     * @param permission
     */
    private fun requestPermission(permission: String) = requestPermission.launch(permission)

    /**
     * Set cancellable dialog
     *
     * @param cancellable
     */
    private fun setCancellableDialog(cancellable: Boolean) {
        dialog?.setCancelable(cancellable)
        dialog?.setCanceledOnTouchOutside(cancellable)
    }

    /**
     * Set fixed submit button
     *
     */
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

    /**
     * Setup views
     *
     */
    private fun setupViews() = binding.apply {
        setSubmitButtonDisableColors()
        setupRecyclerView(rvFiles)
        setFixedSubmitButton()
        showSelectedCount()

        cardLine.setCardBackgroundColor(ColorStateList.valueOf(accentColor))
        progress.indeterminateTintList = ColorStateList.valueOf(accentColor)
        tvTitle.apply {
            text = title
            setTextColor(titleTextColor)
        }

        btnSubmit.apply {
            text = submitText
            setOnClickListener {
                submitList()
                dismissAllowingStateLoss()
            }
        }
    }

    /**
     * Show selected count
     *
     */
    private fun showSelectedCount() {
        val selectedCount = getSelectedItems()?.size ?: 0
        binding.tvTitle.text = "$title ($selectedCount/$limitCount)"
    }

    /**
     * Setup recycler view
     *
     * @param rvFiles
     */
    private fun setupRecyclerView(rvFiles: RecyclerView) {
        itemAdapter = ItemAdapter(
            accentColor = accentColor,
            limitSelectionCount = limitCount,
            listener = { itemPosition ->
                if (onItemClickListener != null) {
                    itemAdapter?.let {
                        val media = it.currentList[itemPosition]
                        if (media != null) {
                            onItemClickListener?.onClick(media, itemPosition, it)
                        }
                    }
                } else {
                    itemAdapter?.setSelected(itemPosition)
                }

                showSelectedCount()
                setButtonEnabled()
            }
        )
        rvFiles.apply {
            layoutDirection = when (listDirection) {
                ListDirection.LTR -> RecyclerView.LAYOUT_DIRECTION_LTR
                ListDirection.RTL -> RecyclerView.LAYOUT_DIRECTION_RTL
            }
            layoutManager = GridLayoutManager(requireContext(), gridSpanCount)
            adapter = itemAdapter
        }
    }

    /**
     * Set button enabled
     *
     */
    private fun setButtonEnabled() = view?.post {
        binding.apply {
            val hasSelected = hasSelectedItem()
            btnSubmit.apply {
                isEnabled = hasSelected
                if (isEnabled) {
                    setSubmitButtonEnableColors()
                } else {
                    setSubmitButtonDisableColors()
                }
            }
        }
    }

    /**
     * Set submit button enable colors
     *
     */
    private fun setSubmitButtonEnableColors() = binding.btnSubmit.apply {
        setTextColor(submitTextColor)
        setBackgroundColor(accentColor)
    }

    /**
     * Set submit button disable colors
     *
     */
    private fun setSubmitButtonDisableColors() = binding.btnSubmit.apply {
        setTextColor(Color.GRAY)
        setBackgroundColor(Color.LTGRAY)
    }

    /**
     * Load files
     *
     */
    private fun loadFiles() = CoroutineScope(Dispatchers.IO).launch {
        val files = getStorageFiles(fileType = fileType)
            .map { Media(file = it) }

        selectedFiles.forEach { media ->
            files.forEach {
                if (it.id == media.id) {
                    it.isSelected = media.isSelected
                }
            }
        }

        requireActivity().runOnUiThread {
            itemAdapter?.submitList(files)
            binding.progress.isVisible = false
            setFixedSubmitButton()
            showSelectedCount()
            setButtonEnabled()
        }
    }

    /**
     * Submit list
     *
     */
    private fun submitList() = getSelectedItems()?.let {
        onSubmitClickListener?.onClick(it)
    }

    /**
     * Get selected items
     *
     * @return
     */
    private fun getSelectedItems(): List<Media>? =
        itemAdapter?.currentList?.filter { it.isSelected }

    /**
     * Has selected item
     *
     * @return
     */
    private fun hasSelectedItem(): Boolean = !getSelectedItems().isNullOrEmpty()

    companion object {
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

        private var isShown: Boolean = false
    }

}

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
    onSubmitClickListener: OnSubmitClickListener? = null,
    onItemClickListener: OnItemClickListener? = null,
) {
    FilePicker.Builder(this)
        .setTitle(title)
        .setTitleTextColor(titleTextColor)
        .setSubmitText(submitText)
        .setSubmitTextColor(submitTextColor)
        .setAccentColor(accentColor)
        .setFileType(fileType)
        .setListDirection(listDirection)
        .setCancellable(cancellable)
        .setGridSpanCount(gridSpanCount)
        .setLimitItemSelection(limitItemSelection)
        .setSelectedFiles(selectedFiles)
        .setOnSubmitClickListener(onSubmitClickListener)
        .setOnItemClickListener(onItemClickListener)
        .buildAndShow()
}

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
    onSubmitClickListener: OnSubmitClickListener? = null,
    onItemClickListener: OnItemClickListener? = null,
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
        onSubmitClickListener = onSubmitClickListener,
        onItemClickListener = onItemClickListener,
    )
}