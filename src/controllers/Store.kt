package controllers

import models.*
import tornadofx.*
import view.userforms.LoginView

class Store : Controller() {
    val user: UserModel by inject()
    val token: UserTokenModel by inject()
    val account: AccountModel by inject()
    val transactions = SortedFilteredList<Transaction>()

    fun logout() {
        user.item = null
        token.item = null
        account.item = null
        transactions.clear()
        primaryStage.uiComponent<UIComponent>()?.replaceWith<LoginView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
    }
}