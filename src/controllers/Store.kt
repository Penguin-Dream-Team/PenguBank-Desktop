package controllers

import javafx.beans.property.SimpleStringProperty
import models.*
import tornadofx.*
import view.userforms.LoginView

class Store : Controller() {
    val user: UserModel by inject()
    val token: UserTokenModel by inject()
    val account: AccountModel by inject()
    val transactions = SortedFilteredList<Transaction>()

    val balanceProperty = SimpleStringProperty()
    var balance by balanceProperty

    fun logout() {
        user.item = null
        token.item = null
        account.item = null
        balance = ""
        transactions.clear()
        primaryStage.uiComponent<UIComponent>()?.replaceWith<LoginView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
    }
}