package controllers

import javafx.beans.property.SimpleStringProperty
import models.requests.LoginRequest
import models.requests.RegisterRequest
import models.requests.Verify2FARequest
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue
import utils.safeExecute
import view.DashboardView
import view.userforms.LoginView
import view.userforms.RegisterView
import view.userforms.Verify2FAView

class LoginController : Controller() {
    private val api: Rest by inject()

    private val store: Store by inject()

    val statusProperty = SimpleStringProperty()
    var status: String by statusProperty

    fun requestLogin(loginRequest: LoginRequest) {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.post("login", loginRequest)
            val json = response.one()

            runLater {
                if(response.ok()) {
                    store.user.item = json.jsonModel("data")
                    store.token.item = json.toModel()

                    if(store.user.item.enabled2FA)
                        find(LoginView::class).replaceWith<Verify2FAView>(sizeToScene = true, transition = ViewTransition.FadeThrough(.3.seconds))
                    else
                        find(LoginView::class).replaceWith<DashboardView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                }
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.post("register", registerRequest)
            val json = response.one()

            runLater {
                if(response.ok()) {
                    information("Successfully registered") {
                        find(RegisterView::class).replaceWith<LoginView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
                    }
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                }
            }
        }
    }

    fun verify2FA(verify2FARequest: Verify2FARequest) {
        runLater { status = "" }

        safeExecute(statusProperty) {
            val response = api.post("verify", verify2FARequest)
            val json = response.one()

            runLater {
                if(response.ok()) {
                    store.user.item = json.jsonModel("data")
                    store.token.item = json.toModel()
                    find(Verify2FAView::class).replaceWith<DashboardView>(sizeToScene = true, centerOnScreen = true, transition = ViewTransition.FadeThrough(.3.seconds))
                } else {
                    status = json.string("message") ?: "Oops, something went wrong!"
                }
            }
        }
    }
}