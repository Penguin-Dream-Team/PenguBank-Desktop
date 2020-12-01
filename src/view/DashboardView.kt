package view

import PenguBankApplicationConstants
import controllers.DashboardController
import controllers.Activate2FAController
import controllers.Store
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import models.requests.TransactionRequestModel
import tornadofx.*
import view.partials.LogoHeader
import view.settings.Enable2FAModal
import view.settings.NewTransactionModal

class DashboardView : View("PenguBank | Dashboard") {
    val dashboardController: DashboardController by inject()
    val activate2FAController: Activate2FAController by inject()
    val store: Store by inject()
    private val model = TransactionRequestModel()

    override val root = borderpane {
        prefWidth = 1080.0
        prefHeight = PenguBankApplicationConstants.USER_FORM_HEIGHT

        left = vbox(20.0) {
            prefWidth = 280.0
            alignment = Pos.CENTER

            style {
                backgroundColor += c("ffffff")
                borderColor += box(Color.TRANSPARENT, c("#00000033"), Color.TRANSPARENT, Color.TRANSPARENT)
            }

            borderpane {
                top<LogoHeader>()
                center = vbox(10.0) {
                    alignment = Pos.CENTER

                    label(store.user.email)

                    hbox(5.0) {
                        alignment = Pos.CENTER

                        button("Enable 2FA") {
                            enableWhen(!store.user.enabled2FA)
                            action {
                                runAsyncWithProgress {
                                    activate2FAController.requestActivate2FA()
                                }
                            }
                        }

                        button("Logout") {
                            action(store::logout)
                        }
                    }
                }
            }

        }

        center = vbox(20.0) {
            alignment = Pos.CENTER
            paddingHorizontal = 30.0

            borderpane {
                alignment = Pos.CENTER_LEFT

                left = button("New Transaction") {
                    action {
                        find<NewTransactionModal>().openModal(resizable = false)
                    }
                }

                right = hbox(5.0) {
                    alignment = Pos.CENTER_RIGHT

                    label("Balance:")
                    label(store.balanceProperty) {
                        style {
                            fontWeight = FontWeight.BOLD
                        }
                    }
                }
            }

            vbox(10.0) {
                alignment = Pos.CENTER

                listview(store.transactions)
                button("Refresh") {
                    action {
                        runAsyncWithProgress {
                            dashboardController.refreshDashboard()
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        dashboardController.refreshDashboard()
    }
}
