package controllers

import bluetooth.BluetoothConnectionMaster
import models.PendingTransaction
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import models.*
import tornadofx.*
import view.DashboardView
import view.userforms.LoginView
import view.userforms.Verify2FAView
import java.security.PublicKey

class Store : Controller() {
    val user: UserModel by inject()
    val token: UserTokenModel by inject()
    val account: AccountModel by inject()

    val transactions = SortedFilteredList<Transaction>()
    val hasTransactionsProperty = SimpleBooleanProperty(false)
    var hasTransactions: Boolean by hasTransactionsProperty

    val pendingTransactions = SortedFilteredList<PendingTransaction>()
    val hasPendingTransactionsProperty = SimpleBooleanProperty(false)
    var hasPendingTransactions: Boolean by hasPendingTransactionsProperty

    private val mobilePublicKeyProperty = SimpleObjectProperty<PublicKey>()
    var mobilePublicKey: PublicKey by mobilePublicKeyProperty

    private val bluetoothConnectionMasterProperty = SimpleObjectProperty<BluetoothConnectionMaster>()
    var bluetoothConnectionMaster: BluetoothConnectionMaster? by bluetoothConnectionMasterProperty

    val hasBluetoothConnectionProperty = SimpleBooleanProperty(false)
    var hasBluetoothConnection: Boolean by hasBluetoothConnectionProperty

    val balanceProperty = SimpleStringProperty()
    var balance: String by balanceProperty

    fun logout() {
        user.item = null
        token.item = null
        account.item = null
        hasBluetoothConnection = false
        bluetoothConnectionMaster?.quit()
        bluetoothConnectionMaster = null
        balance = ""

        transactions.clear()
        hasTransactions = false

        pendingTransactions.clear()
        hasPendingTransactions = false

        find<DashboardView>().replaceWith<LoginView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
        find<Verify2FAView>().replaceWith<LoginView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
    }

}