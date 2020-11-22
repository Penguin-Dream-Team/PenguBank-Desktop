package view.userforms

import Styles
import controllers.LoginController
import controllers.Store
import javafx.geometry.Orientation
import models.requests.Verify2FARequest
import models.requests.Verify2FARequestModel
import tornadofx.*
import view.partials.LogoHeader

class Verify2FAView : View("PenguBank | Verify Authentication") {

    private val loginController: LoginController by inject()
    private val store: Store by inject()

    private val model = Verify2FARequestModel()

    override val root = borderpane {
        addClass(Styles.userFormView)

        top<LogoHeader>()

        center = form {
            fieldset("Login", labelPosition = Orientation.VERTICAL) {
                field("Email") {
                    textfield(store.user.email) {
                        isDisable = true
                    }
                }

                field("Code") {
                    textfield(model.code).required()
                }
            }

            button("Verify") {
                enableWhen(model.valid)
                isDefaultButton = true
                useMaxWidth = true

                action {
                    runAsyncWithProgress {
                        model.commit()
                        loginController.verify2FA(model.item)
                    }
                }
            }

            label(loginController.statusProperty) {
                addClass(Styles.status)
            }
        }

        bottom = hbox {
            addClass(Styles.formBottom)

            button("Cancel Login and Go Back") {
                addClass(Styles.linkButton)
                action(store::logout)
            }
        }
    }

    override fun onUndock() {
        clearState()
    }

    private fun clearState() {
        loginController.status = ""
        model.item = Verify2FARequest()
        model.clearDecorators()
    }
}
