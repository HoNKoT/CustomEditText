package jp.honkot.customedittext

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.*
import jp.honkot.customedittext.databinding.ViewCustomEditTextBinding

@InverseBindingMethods(
    InverseBindingMethod(
        type = CustomEditText::class,
        attribute = "text"
    )
)
class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomEditTextBinding

    private val viewModel: ViewModel

    fun getText(): String {
        return viewModel.input
    }

    fun setText(value: String) {
        viewModel.input = value
    }

    init {
        viewModel = ViewModel()
        binding = DataBindingUtil.inflate<ViewCustomEditTextBinding>(
            LayoutInflater.from(context),
            R.layout.view_custom_edit_text,
            this,
            true
        )!!.also { binding ->
            binding.input.onFocusChangeListener = viewModel
            binding.viewModel = viewModel
        }
    }

    fun getInputText(): String {
        return viewModel.input
    }

    fun setInverseBindingListener(listener: InverseBindingListener) {
        viewModel.listener = listener
    }

    inner class ViewModel : BaseObservable(), OnFocusChangeListener {
        @Bindable
        var input: String = ""
            set(value) {
                field = value
                listener?.onChange()
                notifyPropertyChanged(BR.input)
                showCaption.set(value.isNotEmpty())
            }

        var listener: InverseBindingListener? = null

        var hintAndCaption: String = "AAA"

        var showCaption = ObservableBoolean(false)

        override fun onFocusChange(view: View?, focused: Boolean) {
            view?.let {
                showCaption.set(when {
                    focused -> true
                    else -> input.isNotEmpty()
                })
            }
        }
    }

    object CustomEditTextBindingAdapter {
        @BindingAdapter("textAttrChanged")
        @JvmStatic
        fun setTextWatcher(view: CustomEditText?, listener: InverseBindingListener) {
            view?.setInverseBindingListener(listener)
        }
    }
}
