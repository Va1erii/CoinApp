package com.valerii.coinapp.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatSpinner

class CloseDropdownSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.spinnerStyle,
) : AppCompatSpinner(context, attrs, defStyle) {
    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}