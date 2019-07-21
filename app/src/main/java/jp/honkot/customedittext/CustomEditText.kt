package jp.honkot.customedittext

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        val thisView = LayoutInflater.from(context).inflate(R.layout.view_custom_edit_text, this)

        thisView.findViewById<EditText>(R.id.input)?.also {
            it.setOnFocusChangeListener { _, focused ->
                thisView.findViewById<TextView>(R.id.caption)?.visibility = if (focused) {
                    Log.e("test", "visibility=VISIBLE")
                    View.VISIBLE
                } else {
                    if (it.text.toString().isEmpty()) {
                        Log.e("test", "visibility=GONE")
                        View.GONE
                    } else {
                        Log.e("test", "visibility=VISIBLE")
                        View.VISIBLE
                    }
                }
            }
        }
    }
}