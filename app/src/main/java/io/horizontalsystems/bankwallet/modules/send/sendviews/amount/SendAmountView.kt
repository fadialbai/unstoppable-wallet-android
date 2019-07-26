package io.horizontalsystems.bankwallet.modules.send.sendviews.amount

import android.content.Context
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.modules.send.SendModule
import io.horizontalsystems.bankwallet.modules.send.SendViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_amount_input.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class SendAmountView : ConstraintLayout {

    init {
        inflate(context, R.layout.view_amount_input, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var viewModel: SendAmountViewModel
    private lateinit var lifecycleOwner: LifecycleOwner
    private var decimalSize: Int? = null
    private var disposable: Disposable? = null

    override fun onDetachedFromWindow() {
        disposable?.dispose()
        super.onDetachedFromWindow()
    }

    fun bindInitial(viewModel: SendAmountViewModel, mainViewModel: SendViewModel, lifecycleOwner: LifecycleOwner, decimalSize: Int?) {
        this.viewModel = viewModel
        this.lifecycleOwner = lifecycleOwner
        this.decimalSize = decimalSize

        btnSwitch.visibility = View.VISIBLE
        btnMax.visibility = View.VISIBLE

        btnMax?.setOnClickListener { viewModel.delegate.onMaxClick() }
        btnSwitch?.setOnClickListener { viewModel.delegate.onSwitchClick() }
        btnSwitch.imageTintMode = PorterDuff.Mode.SRC_IN
        invalidate()

        viewModel.delegate.onViewDidLoad()

        viewModel.amountInputPrefixLiveData.observe(lifecycleOwner, Observer { prefix ->
            topAmountPrefix.text = prefix
        })

        viewModel.amountInfoLiveData.observe(lifecycleOwner, Observer { amountInfo ->
            val amountNumber = when (amountInfo) {
                is SendModule.AmountInfo.CoinValueInfo -> {
                    amountInfo.coinValue.value.setScale(8, RoundingMode.HALF_EVEN)
                }
                is SendModule.AmountInfo.CurrencyValueInfo -> {
                    amountInfo.currencyValue.value.setScale(2, RoundingMode.HALF_EVEN)
                }
                else -> BigDecimal.ZERO
            }

            if (amountNumber > BigDecimal.ZERO) {
                editTxtAmount.setText(amountNumber.stripTrailingZeros().toPlainString())
                editTxtAmount.setSelection(editTxtAmount.text.length)
            } else {
                editTxtAmount.setText("")
            }
        })

        viewModel.amountHintValueLiveData.observe(lifecycleOwner, Observer { amountInfo ->
            val hintText = when (amountInfo) {
                is SendModule.AmountInfo.CoinValueInfo -> App.numberFormatter.format(amountInfo.coinValue, realNumber = true)
                is SendModule.AmountInfo.CurrencyValueInfo -> App.numberFormatter.format(amountInfo.currencyValue)
                else -> null
            }
            txtHintInfo.text = hintText
        })

        viewModel.maxButtonVisibleValueLiveData.observe(lifecycleOwner, Observer { visible ->
            btnMax?.visibility = if (visible) View.VISIBLE else View.GONE
        })

        viewModel.addTextChangeListenerLiveEvent.observe(lifecycleOwner, Observer {
            editTxtAmount.addTextChangedListener(textChangeListener)
        })

        viewModel.removeTextChangeListenerLiveEvent.observe(lifecycleOwner, Observer {
            editTxtAmount.removeTextChangedListener(textChangeListener)
        })

        viewModel.revertInputLiveEvent.observe(lifecycleOwner, Observer { revertedInput ->
            editTxtAmount.setText(revertedInput)
            editTxtAmount.setSelection(revertedInput.length)
            val shake = AnimationUtils.loadAnimation(context, R.anim.shake_edittext)
            editTxtAmount.startAnimation(shake)
        })

        viewModel.getAvailableBalanceLiveEvent.observe(lifecycleOwner, Observer {
            mainViewModel.delegate.onGetAvailableBalance()
        })

        viewModel.notifyMainViewModelOnAmountChangeLiveData.observe(lifecycleOwner, Observer { coinAmount ->
            mainViewModel.delegate.onAmountChanged(coinAmount)
        })
    }

    fun updateInput(hint: String? = null, error: String? = null) {
        txtHintInfo.visibility = if (error == null) View.VISIBLE else View.GONE
        txtHintError.visibility = if (error == null) View.GONE else View.VISIBLE
        txtHintInfo.text = hint
        txtHintError.text = error
    }

    fun enableSwitchBtn(enabled: Boolean) {
        btnSwitch.isEnabled = enabled
    }

    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val amountText = s?.toString() ?: ""
            viewModel.delegate.onAmountChange(amountText)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

}
