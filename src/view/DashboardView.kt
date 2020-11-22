package view

import PenguBankApplicationConstants
import controllers.DashboardController
import controllers.Store
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import view.partials.LogoHeader

class DashboardView : View("PenguBank | Dashboard") {
    val dashboardController: DashboardController by inject()
    val store: Store by inject()

    override val root = borderpane {
        val transactions = FXCollections.observableArrayList("Trans 1", "Trans 2", "Trans 3")

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

                        button("Settings")
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

                left = button("New Transaction")

                right = hbox(5.0) {
                    alignment = Pos.CENTER_RIGHT

                    label("Balance:")
                    label(store.account.balance) {
                        style {
                            fontWeight = FontWeight.BOLD
                        }
                    }
                }
            }

            vbox(10.0) {
                alignment = Pos.CENTER

                listview(transactions)
                button("Load More")
            }
        }
    }

    override fun onDock() {
        dashboardController.refreshDashboard()
    }
}
