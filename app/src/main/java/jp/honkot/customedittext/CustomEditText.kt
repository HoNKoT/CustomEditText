package jp.honkot.customedittext

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.*
import jp.honkot.customedittext.databinding.ViewCustomEditTextBinding

class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomEditTextBinding

    private val viewModel: ViewModel

    init {
        viewModel = ViewModel()
        binding = DataBindingUtil.inflate<ViewCustomEditTextBinding>(
            LayoutInflater.from(context),
            R.layout.view_custom_edit_text,
            this,
            true
        )!!.also { binding ->
            binding.input.onFocusChangeListener = viewModel
        }
    }

    class ViewModel : BaseObservable(), OnFocusChangeListener {
        @Bindable
        var input: String = ""
            set(value) {
                field = value
                showCaption.set(value.isNotEmpty())
            }

        var hintAndCaption: String = ""

        var showCaption = ObservableBoolean(true)

        override fun onFocusChange(view: View?, focused: Boolean) {
            view?.let {
                when {
                    focused -> showCaption.set(true)
                    else -> showCaption.set(input.isNotEmpty())
                }
            }
        }
    }
}
