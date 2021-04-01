import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import model.CarPart
import model.CarPartPackage
import model.Employee
import model.Shipment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object TransferringShipmentToCargoBayFeature : Spek({
    Feature("Transferring shipment to cargo bay") {
        Scenario("An empty shipment comes to the cargo bay") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("An employee 'Yoda' assigned to the factory") {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(
                        Employee("Yoda")
                    )
                )
            }

            When("An empty shipment comes to the cargo bay, there should be an error") {
                resultingEvents = fold(
                        currentEvents,
                        TransferShipmentToCargoBay(
                            Shipment(
                                "some shipment",
                                emptyList()
                            )
                        )
                    )
            }

            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }

            Then("The error message should contain \"Empty shipments are not accepted!\" ") {
                resultingEvents.mapLeft { it shouldContain "Empty shipments are not accepted!" }
            }
        }

        Scenario("An empty shipment come into an empty factory") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>
            Given("An empty factory") {
                currentEvents = emptyList()
            }
            When("An empty shipment comes to the cargo bay, there should be an error") {
                    resultingEvents =
                        fold(currentEvents, TransferShipmentToCargoBay(Shipment("some shipment", emptyList())))
            }
            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }
            And(
                "The error message should contain \"there has to be somebody " +
                        "at the factory in order to accept the shipment\" "
            ) {
                resultingEvents.mapLeft {
                    it shouldContain
                            "there has to be somebody at the factory in order to accept the shipment"
                }
            }
        }

        Scenario("A shipment come into an empty factory") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("An empty factory") {
                currentEvents = emptyList()
            }

            When("An empty shipment comes to the cargo bay") {
                resultingEvents =
                    fold(
                        currentEvents,
                        TransferShipmentToCargoBay(
                            Shipment(
                                "some shipment",
                                emptyList()
                            )
                        )
                    )
            }
            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }
            And(
                "The error message should contain \"there has to be somebody " +
                        "at the factory in order to accept the shipment\" "
            ) {
                resultingEvents.mapLeft {
                    it shouldContain "there has to be somebody at the factory in order to accept the shipment"
                }
            }
        }

        Scenario("There are already two shipments") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("There is an employee assigned to the factory and two shipments waiting in the cargo bay") {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Chewbacca")),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-11",
                            listOf(CarPartPackage(CarPart("engine"), 3))
                        )
                    ),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-12",
                            listOf(CarPartPackage(CarPart("wheel"), 40))
                        )
                    )
                )


            }

            When("A new shipment comes to the cargo bay") {
                resultingEvents =
                    fold(
                        currentEvents,
                        TransferShipmentToCargoBay(
                            Shipment(
                                "shipment-13",
                                listOf(CarPartPackage(CarPart("bmw6"), 6))
                            )
                        )
                    )
            }

            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }

            And("The error message should contain \"More than two shipments can't fit\" ") {
               resultingEvents.mapLeft { it  shouldContain "More than two shipments can't fit into this cargo bay"}

            }
        }

        Scenario("A shipment comes to a factory with an employee assigned and 1 shipment of in the cargo bay") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>
            Given("A factory with an employee assigned and 1 shipment in the cargo bay") {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Chewbacca")),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-55",
                            listOf(CarPartPackage(CarPart("wheel"), 5))
                        )
                    )
                )

            }

            When("A shipment comes to the factory") {
                resultingEvents = fold(
                    currentEvents,
                    TransferShipmentToCargoBay(
                        Shipment(
                            "shipment-56",
                            listOf(CarPartPackage(CarPart("engine"), 6), CarPartPackage(CarPart("chassis"), 2))
                        )
                    )
                )
            }

            Then("The shipment is transferred to the cargo bay") {
                resultingEvents.shouldBeRight()
                resultingEvents.map { it shouldContain ShipmentTransferredToCargoBay(
                    Shipment(
                        "shipment-56",
                        listOf(CarPartPackage(CarPart("engine"), 6), CarPartPackage(CarPart("chassis"), 2))
                    )
                ) }

            }
        }

        Scenario("A shipment of 5 wheels and 7 engines comes to the factory and the employee curse") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>
            Given("A factory with an employee assigned and 1 shipment in the cargo bay") {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Chewbacca")),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-58",
                            listOf(CarPartPackage(CarPart("chassis"), 3))
                        )
                    )
                )

            }
            When("A shipment of 5 wheel and 7 engines comes to the factory") {
                resultingEvents = fold(
                    currentEvents,
                    TransferShipmentToCargoBay(
                        Shipment(
                            "shipment-56",
                            listOf(CarPartPackage(CarPart("wheel"), 5), CarPartPackage(CarPart("engines"), 7))
                        )
                    )
                )

            }
            Then("The shipment is transferred to the cargo bay") {
                resultingEvents.shouldBeRight()
                resultingEvents.map { it shouldContain ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-56",
                            listOf(CarPartPackage(CarPart("wheel"), 5), CarPartPackage(CarPart("engines"), 7))
                        )
                        )
                }
            }

            And("A curse word has been uttered by one of the employee") {
                resultingEvents.map { it.filterIsInstance<CurseWordUttered>() shouldNot beEmpty() }
            }
        }
    }

})
