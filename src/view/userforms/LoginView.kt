package view.userforms

import Styles
import controllers.LoginController
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import models.requests.LoginRequest
import models.requests.LoginRequestModel
import tornadofx.*
import utils.isValidEmail
import view.partials.LogoHeader

class LoginView : View("PenguBank | Login to account") {

    private val loginController: LoginController by inject()
    private val model = LoginRequestModel(LoginRequest())

    private val enabledProperty = SimpleBooleanProperty(true)
    var enabled by enabledProperty

    override val root = borderpane {
        addClass(Styles.userFormView)

        top<LogoHeader>()

        center = form {
            fieldset("Login", labelPosition = Orientation.VERTICAL) {
                field("Email") {
                    textfield(model.email).validator {
                        if (it.isNullOrBlank()) error("The email cannot be blank")
                        else if (!it.isValidEmail()) error("This is not a valid email")
                        else null
                    }
                }

                field("Password") {
                    passwordfield(model.password).required()
                }
            }

            button("Login") {
                enableWhen(enabledProperty.and(model.valid))
                isDefaultButton = true
                useMaxWidth = true

                action {
                    enabled = false
                    runAsyncWithProgress {
                        model.commit()
                        loginController.requestLogin(model.item)
                        enabled = true
                    }
                }
            }

            label(loginController.statusProperty) {
                addClass(Styles.status)
            }
        }

        bottom = hbox {
            addClass(Styles.formBottom)

            button("Don't have an account? Register here") {
                addClass(Styles.linkButton)
                action {
                    this@LoginView.replaceWith<RegisterView>(sizeToScene = true, transition = ViewTransition.FadeThrough(.3.seconds))
                }
            }
        }
    }

    override fun onUndock() {
        clearState()
    }

    // TODO: TEMPORARY -> FOR TESTING ONLY
/*
    override fun onDock() {
        model.email.value = "a@b.c"
        model.password.value = "password"
    }
*/

    private fun clearState() {
        loginController.status = ""
        model.item = LoginRequest()
        model.clearDecorators()
    }
}
