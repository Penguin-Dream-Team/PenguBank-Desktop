package view

import PenguBankApplicationConstants
import controllers.DashboardController
import controllers.Activate2FAController
import controllers.BluetoothConnectionController
import controllers.Store
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import view.dialogs.KeyPasswordDialog
import view.partials.LogoHeader
import view.dialogs.NewTransactionModal
import tornadofx.getValue
import tornadofx.setValue

class DashboardView : View("PenguBank | Dashboard") {
    val dashboardController: DashboardController by inject()
    val bluetoothConnectionController: BluetoothConnectionController by inject()
    val activate2FAController: Activate2FAController by inject()
    val store: Store by inject()

    val showingTransactionsProperty = SimpleBooleanProperty(true)
    var showingTransactions by showingTransactionsProperty

    private val enabledProperty = SimpleBooleanProperty(true)
    var enabled by enabledProperty

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
                            enableWhen(enabledProperty.and(!store.user.enabled2FA))
                            action {
                                enabled = false
                                runAsyncWithProgress {
                                    activate2FAController.requestActivate2FA()
                                    enabled = true
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

                left = hbox(10) {
                    button("New Transaction") {
                        action {
                            find<NewTransactionModal>().openModal(
                                resizable = false,
                                escapeClosesWindow = false,
                                block = true
                            )
                        }
                    }

                    stackpane {
                        button("Stop Bluetooth Local Server") {
                            enableWhen(enabledProperty)
                            visibleWhen(store.hasBluetoothConnectionProperty)
                            action {
                                enabled = false
                                bluetoothConnectionController.stop()
                                enabled = true
                            }
                        }

                        button("Start Bluetooth Local Server") {
                            enableWhen(enabledProperty)
                            visibleWhen(!store.hasBluetoothConnectionProperty)
                            action {
                                enabled = false
                                runAsyncWithProgress {
                                    bluetoothConnectionController.requestPhonePublicKey()
                                    runLater {
                                        if (bluetoothConnectionController.status.isNullOrBlank()) {
                                            find<KeyPasswordDialog>().openModal(resizable = false, block = true)
                                        } else {
                                            error(bluetoothConnectionController.status)
                                        }
                                    }
                                    enabled = true
                                }
                            }
                        }
                    }

                    stackpane {
                        label("Connected") {
                            visibleWhen(
                                store.hasBluetoothConnectionProperty
                                    .and(bluetoothConnectionController.connectedProperty)
                            )
                        }

                        label("Listening") {
                            visibleWhen(
                                store.hasBluetoothConnectionProperty
                                    .and(!bluetoothConnectionController.connectedProperty)
                            )
                        }
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
                alignment = Pos.CENTER_LEFT

                stackpane {
                    label("Transactions") {
                        visibleWhen(showingTransactionsProperty)
                    }

                    label("Pending transactions") {
                        visibleWhen(!showingTransactionsProperty)
                    }
                }

                vbox(10.0) {
                    alignment = Pos.CENTER

                    stackpane {
                        listview(store.transactions) {
                            visibleWhen(store.hasTransactionsProperty.and(showingTransactionsProperty))
                        }

                        listview(store.pendingTransactions) {
                            visibleWhen(store.hasPendingTransactionsProperty.and(!showingTransactionsProperty))
                        }

                        text("No transactions") {
                            visibleWhen(showingTransactionsProperty.and(!store.hasTransactionsProperty))
                        }

                        text("No Pending transactions") {
                            visibleWhen(showingTransactionsProperty.not().and(!store.hasPendingTransactionsProperty))
                        }
                    }

                    hbox(20.0) {
                        button("Refresh") {
                            enableWhen(enabledProperty)
                            action {
                                enabled = false
                                runAsyncWithProgress {
                                    dashboardController.refreshDashboard()
                                    enabled = true
                                }
                            }
                        }

                        stackpane {
                            alignment = Pos.CENTER_LEFT

                            button("Show transactions") {
                                enableWhen(enabledProperty)
                                visibleWhen(!showingTransactionsProperty)
                                action {
                                    enabled = false
                                    runAsyncWithProgress {
                                        dashboardController.refreshDashboard()
                                        showingTransactions = true
                                        enabled = true
                                    }
                                }
                            }

                            button("Show pending transactions") {
                                enableWhen(enabledProperty)
                                visibleWhen(showingTransactionsProperty)
                                action {
                                    enabled = false
                                    runAsyncWithProgress {
                                        dashboardController.refreshDashboard()
                                        showingTransactions = false
                                        enabled = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        showingTransactions = true
        enabled = false
        dashboardController.refreshDashboard()
        enabled = true
    }
}
