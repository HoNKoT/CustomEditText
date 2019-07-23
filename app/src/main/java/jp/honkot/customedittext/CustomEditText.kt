package jp.honkot.customedittext

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.text.InputType
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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

    private val textSizeSp: Float

    private val labelTextSizeSp: Float

    fun getText(): String {
        return viewModel.input
    }

    fun setText(value: String) {
        viewModel.input = value
    }

    init {
        // Get Attributes
        val a = getContext().obtainStyledAttributes(
            attrs, R.styleable.CustomEditText, defStyleAttr, 0
        )
        val hint = a.getString(R.styleable.CustomEditText_hint) ?: ""
        textSizeSp = a.getString(R.styleable.CustomEditText_textSize)?.let {
            it.replace("sp", "").toFloatOrNull()
        } ?: 18f
        labelTextSizeSp = a.getString(R.styleable.CustomEditText_textSize)?.let {
            it.replace("sp", "").toFloatOrNull()
        } ?: 12f

        // Set hint
        viewModel = ViewModel().also {
            it.hintAndCaption = hint
        }

        // Get Binding
        binding = DataBindingUtil.inflate<ViewCustomEditTextBinding>(
            LayoutInflater.from(context),
            R.layout.view_custom_edit_text,
            this,
            true
        )!!.also { binding ->
            binding.input.onFocusChangeListener = viewModel
            binding.input.textSize = textSizeSp
            binding.hint.textSize = textSizeSp
            binding.viewModel = viewModel
        }

        val num = when (a.getInt(R.styleable.CustomEditText_inputType, 0)) {
            1 -> InputType.TYPE_CLASS_NUMBER
            2 -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            3 -> InputType.TYPE_CLASS_TEXT
            4 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            5 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
        binding.input.inputType = num

        a.recycle()
    }

    fun getInputText(): String {
        return viewModel.input
    }

    fun setInverseBindingListener(listener: InverseBindingListener) {
        viewModel.listener = listener
    }

    /**
     * ヒント文字とラベル文字を入れ替え
     *
     * @param moveToLabel true means change accent color slowly, false means opposite.
     */
    private fun animate(moveToLabel: Boolean) {
        // Change text color slowly
        val fromColor = ContextCompat.getColor(context, if (moveToLabel) android.R.color.black else R.color.colorPrimary)
        val toColor = ContextCompat.getColor(context, if (moveToLabel) R.color.colorPrimary else android.R.color.black)
        val animatorForColor = ValueAnimator()
        animatorForColor.setIntValues(fromColor, toColor)
        animatorForColor.setEvaluator(ArgbEvaluator()) // ArgbEvaluatorをつかうことで、ARGB各値がアニメーションされる。
        animatorForColor.addUpdateListener { anim ->
            val color = anim.animatedValue as Int
            binding.hint.setTextColor(color)
        }
        animatorForColor.duration = ANIMATION_DURATION

        // Change text size slowly
        val startSize = if (moveToLabel) textSizeSp else labelTextSizeSp
        val endSize = if (moveToLabel) labelTextSizeSp else textSizeSp
        val animatorForText = ValueAnimator.ofFloat(
            startSize, endSize
        )
        animatorForText.duration = ANIMATION_DURATION
        animatorForText.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            binding.hint.textSize = animatedValue
        }

        // Start animation
        // move view position
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val autoTransition = AutoTransition()
            autoTransition.duration = ANIMATION_DURATION
            TransitionManager.beginDelayedTransition(binding.root as ViewGroup, autoTransition)
        }
        binding.caption.setContentId(if (moveToLabel) R.id.hint else R.id.caption)
        animatorForColor.start()
        animatorForText.start()
    }

    companion object {
        private const val ANIMATION_DURATION = 167L
    }

    inner class ViewModel : BaseObservable(), OnFocusChangeListener {
        @Bindable
        var input: String = ""
            set(value) {
                field = value
                listener?.onChange()
                notifyPropertyChanged(BR.input)
            }

        var listener: InverseBindingListener? = null

        var hintAndCaption: String = "AAA"

        @Bindable
        var showCaption: Boolean = false
            set(value) {
                val lastState = field
                field = value
                notifyPropertyChanged(BR.showCaption)

                if (lastState != value) {
                    animate(field)
                }
            }

        override fun onFocusChange(view: View?, focused: Boolean) {
            view?.let {
                showCaption = when {
                    focused -> true
                    else -> input.isNotEmpty()
                }
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
