package io.horizontalsystems.bankwallet.modules.createwallet

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.entities.Coin
import io.horizontalsystems.bankwallet.entities.PredefinedAccountType
import io.reactivex.Observable

object CreateWalletModule {

    interface IService{
        val canCreate: Observable<Boolean>
        var state: CreateWalletService.State

        fun enable(coin: Coin)
        fun disable(coin: Coin)
        fun create()
    }

    class Factory(private val predefinedAccountType: PredefinedAccountType?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = CreateWalletService(predefinedAccountType, App.coinManager, App.accountCreator, App.accountManager, App.walletManager, App.derivationSettingsManager)

            return CreateWalletViewModel(service, listOf(service)) as T
        }
    }

    fun start(activity: FragmentActivity, inApp: Boolean = true, predefinedAccountType: PredefinedAccountType? = null) {
        val fragment = CreateWalletFragment.instance(predefinedAccountType, inApp)

        activity.supportFragmentManager.commit {
            add(R.id.fragmentContainerView, fragment)
            addToBackStack(null)
        }
    }
}
