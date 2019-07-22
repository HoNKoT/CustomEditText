package jp.honkot.customedittext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.honkot.customedittext.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel = ViewModel()

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    fun onButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        Log.e("test", "### activityText='${viewModel.input3.value}', editText='${binding.editText1.getInputText()}'")
    }

    class ViewModel : androidx.lifecycle.ViewModel() {
//        @Bindable
//        var activityInputText: String = ""
//            set(value) {
//                field = value
//                notifyPropertyChanged(BR.activityInputText)
//            }

        var input2 = ObservableField<String>("")

        var input3 = MutableLiveData<String>()

        init {
            input3.value = ""
        }
    }
}
