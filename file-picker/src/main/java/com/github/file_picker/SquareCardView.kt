package com.github.file_picker

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView

class SquareCardView : MaterialCardView {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributes: AttributeSet) : super(
        context,
        attributes
    )

    constructor(context: Context?, attributes: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}