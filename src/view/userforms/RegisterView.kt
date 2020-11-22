package view.userforms

import Styles
import controllers.LoginController
import javafx.geometry.Orientation
import models.requests.RegisterRequest
import models.requests.RegisterRequestModel
import tornadofx.*
import utils.isValidEmail
import view.partials.LogoHeader

class RegisterView : View("PenguBank | Create a new account") {

    private val loginController: LoginController by inject()
    private val model = RegisterRequestModel()

    override val root = borderpane {
        addClass(Styles.userFormView)

        top<LogoHeader>()

        center = form {
            fieldset("Register", labelPosition = Orientation.VERTICAL) {
                field("Email") {
                    textfield(model.email).validator {
                        when {
                            it.isNullOrBlank() -> error("The email cannot be blank")
                            !it.isValidEmail() -> error("This is not a valid email")
                            else -> null
                        }
                    }
                }

                field("Password") {
                    passwordfield(model.password).required()
                }

                field("Confirm Password") {
                    passwordfield(model.confirmPassword).validator {
                        when {
                            it.isNullOrBlank() -> error("The confirmed password cannot be blank")
                            it != model.password.valueSafe -> error("The passwords need to match")
                            else -> null
                        }
                    }
                }
            }

            button("Register") {
                enableWhen(model.valid)
                isDefaultButton = true
                useMaxWidth = true

                action {
                    runAsyncWithProgress {
                        model.commit()
                        loginController.register(model.item)
                    }
                }
            }

            label(loginController.statusProperty) {
                addClass(Styles.status)
            }
        }

        bottom = hbox {
            addClass(Styles.formBottom)

            button("Already have an account? Login here") {
                addClass(Styles.linkButton)
                action {
                    clearState()
                    this@RegisterView.replaceWith<LoginView>(sizeToScene = true, transition = ViewTransition.FadeThrough(.3.seconds))
                }
            }
        }
    }

    override fun onUndock() {
        clearState()
    }

    private fun clearState() {
        loginController.status = ""
        model.item = RegisterRequest()
        model.clearDecorators()
    }
}
