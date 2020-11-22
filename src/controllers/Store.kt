package controllers

import models.AccountModel
import models.UserModel
import models.UserTokenModel
import tornadofx.*
import view.userforms.LoginView

class Store : Controller() {
    val user: UserModel by inject()
    val token: UserTokenModel by inject()
    val account: AccountModel by inject()

    fun logout() {
        user.item = null
        token.item = null
        account.item = null
        primaryStage.uiComponent<UIComponent>()?.replaceWith<LoginView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
    }
}