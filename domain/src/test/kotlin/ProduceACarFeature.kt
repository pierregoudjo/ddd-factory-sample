import arrow.core.Either
import arrow.core.flatMap
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.string.shouldContain
import model.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ProduceACarFeature : Spek({
    Feature("Produce a car") {

        Scenario("Order to a non assigned employee to produce a model T car") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("Yoda assigned to the factory") {
                currentEvents = listOf(EmployeeAssignedToFactory(Employee("Yoda")))

            }

            When("Order given to Chewbacca to produce a model T car") {
                resultingEvents = fold(currentEvents, ProduceCar(Employee("Chewbacca"), CarModel.MODEL_T))
            }
            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }

            Then("The error should contains the message \"Chewbacca must be assigned to the factory\" ") {
                resultingEvents.mapLeft { it shouldContain "Chewbacca must be assigned to the factory" }
            }
        }

        Scenario("Order to build a model T car but there is not enough parts") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels and 5 bits and pieces") {
                currentEvents =
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(
                                    CarPartPackage(CarPart("chassis"), 3),
                                    CarPartPackage(CarPart("wheel"), 5),
                                    CarPartPackage(CarPart("bits and pieces"), 5)
                                )
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            Employee("Yoda"), listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5)
                            )
                        )
                    )

            }
            When("Yoda is ordered to build a model T car") {
                resultingEvents = fold(currentEvents, ProduceCar(Employee("Yoda"), CarModel.MODEL_T))

            }

            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }

            Then(
                "The error should contains the message \"There is not " +
                        "enough part to build ${CarModel.MODEL_T} car\" "
            ) {
                resultingEvents.mapLeft { it shouldContain "There is not enough part to build ${CarModel.MODEL_T} car" }
            }
        }

        Scenario("Order to build a model V car but there is not enough parts") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>
            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels and 5 bits and pieces") {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Yoda")),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-1",
                            listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5)
                            )
                        )
                    ),
                    ShipmentUnpackedInCargoBay(
                        Employee("Yoda"), listOf(
                            CarPartPackage(CarPart("chassis"), 3),
                            CarPartPackage(CarPart("wheel"), 5),
                            CarPartPackage(CarPart("bits and pieces"), 5)
                        )
                    )
                )
            }
            When("Yoda is ordered to build a model T car") {
                resultingEvents = fold(currentEvents, ProduceCar(Employee("Yoda"), CarModel.MODEL_V))
            }

            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }

            Then(
                "The error should contains the message \"There is not " +
                        "enough part to build ${CarModel.MODEL_V} car\" "
            ) {
                resultingEvents.mapLeft { it shouldContain "There is not enough part to build ${CarModel.MODEL_V} car" }
            }
        }

        Scenario("Order to build a model T car and there is enough parts") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines") {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Yoda")),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-1",
                            listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5),
                                CarPartPackage(CarPart("engine"), 2),
                            )
                        )
                    ),
                    ShipmentUnpackedInCargoBay(
                        Employee("Yoda"), listOf(
                            CarPartPackage(CarPart("chassis"), 3),
                            CarPartPackage(CarPart("wheel"), 5),
                            CarPartPackage(CarPart("bits and pieces"), 5),
                            CarPartPackage(CarPart("engine"), 2),
                        )
                    )
                )

            }
            When("Yoda is ordered to produce a model T car") {
                resultingEvents = fold(currentEvents, ProduceCar(Employee("Yoda"), CarModel.MODEL_T))
            }
            Then("Then a model T car is built") {

                resultingEvents.map {
                    it shouldContain CarProduced(
                        Employee("Yoda"),
                        CarModel.MODEL_T,
                        CarModel.neededParts(CarModel.MODEL_T)
                    )
                }
            }
        }

        Scenario("Order to build a model V car and there is enough parts") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines") {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Yoda")),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-1",
                            listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5),
                                CarPartPackage(CarPart("engine"), 2),
                            )
                        )
                    ),
                    ShipmentUnpackedInCargoBay(
                        Employee("Yoda"), listOf(
                            CarPartPackage(CarPart("chassis"), 3),
                            CarPartPackage(CarPart("wheel"), 5),
                            CarPartPackage(CarPart("bits and pieces"), 5),
                            CarPartPackage(CarPart("engine"), 2),
                        )
                    )
                )
            }
            When("Yoda is ordered to produce a model V car") {
                resultingEvents = fold(currentEvents, ProduceCar(Employee("Yoda"), CarModel.MODEL_V))
            }
            Then("Then a model V car is built") {
                resultingEvents.map {
                    it shouldContain CarProduced(
                        Employee("Yoda"),
                        CarModel.MODEL_V,
                        CarModel.neededParts(CarModel.MODEL_V)
                    )
                }
            }
        }

        Scenario("Order to build a model T car and checking the remaining parts") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var factory: Either<String, Factory>

            Given(
                "Yoda and Luke assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines"
            ) {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Yoda")),
                    EmployeeAssignedToFactory(Employee("Luke")),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-1",
                            listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5),
                                CarPartPackage(CarPart("engine"), 2),
                            )
                        )
                    ),
                    ShipmentUnpackedInCargoBay(
                        Employee("Yoda"), listOf(
                            CarPartPackage(CarPart("chassis"), 3),
                            CarPartPackage(CarPart("wheel"), 5),
                            CarPartPackage(CarPart("bits and pieces"), 5),
                            CarPartPackage(CarPart("engine"), 2),
                        )
                    )
                )
            }
            And("Model T car requires 2 wheels, 1 engine, and 2 bits and pieces parts to produce") {

            }
            And("Model V car requires 1 chassis, 2 wheels, 1 engine and 2 bits and pieces parts to produce") {

            }
            When("Yoda is ordered to produce a model T car and Luke is ordered to produce a model V car") {
                val initialState = currentEvents.fold(Factory.empty, { acc, curr -> evolve(curr, acc) })
                factory = decide(ProduceCar(Employee("Yoda"), CarModel.MODEL_T), initialState).map {
                    initialState to it
                }.map { (prevState, events) ->
                    events.fold(prevState, { acc, curr -> evolve(curr, acc) })
                }.flatMap { state ->
                    decide(ProduceCar(Employee("Luke"), CarModel.MODEL_V), state).map { state to it }
                }.map { (prevState, events) ->
                    events.fold(prevState, { acc, curr -> evolve(curr, acc) })
                }

                factory.shouldBeRight()
            }

            Then("There is 2 chassis left") {
                factory.map { it.inventory shouldContain (CarPart("chassis") to 2) }
            }

            And("There is 1 wheels left") {
                factory.map { it.inventory shouldContain (CarPart("wheel") to 1) }
            }

            And("There is 1 bits and pieces left") {
                factory.map { it.inventory shouldContain (CarPart("bits and pieces") to 1)}
            }

            And("There is 0 engine left") {
                factory.map { it.inventory shouldContain (CarPart("engine") to 0)}
            }
        }

        Scenario("Not enough stock to build a new car after some have already been built") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given(
                "Yoda assigned to the factory, 3 chassis, 5 wheels 5 bits and pieces and 2 engines " +
                        "shipment transferred and unpacked and 2 model T cars built"
            ) {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Yoda")),
                    EmployeeAssignedToFactory(Employee("Luke")),
                    EmployeeAssignedToFactory(Employee("Lea")),
                    ShipmentTransferredToCargoBay(
                        Shipment(
                            "shipment-1",
                            listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5),
                                CarPartPackage(CarPart("engine"), 2),
                            )
                        )
                    ),
                    ShipmentUnpackedInCargoBay(
                        Employee("Yoda"), listOf(
                            CarPartPackage(CarPart("chassis"), 3),
                            CarPartPackage(CarPart("wheel"), 5),
                            CarPartPackage(CarPart("bits and pieces"), 5),
                            CarPartPackage(CarPart("engine"), 2),
                        )
                    ),
                    CarProduced(
                        Employee("Yoda"),
                        CarModel.MODEL_T,
                        CarModel.neededParts(CarModel.MODEL_T)
                    ),
                    CarProduced(
                        Employee("Luke"),
                        CarModel.MODEL_T,
                        CarModel.neededParts(CarModel.MODEL_T)
                    ),
                )

            }
            And("Model T car requires 2 wheels, 1 engine, and 2 bits and pieces parts to produce") {

            }
            When("Yoda is ordered to build a model T car") {
                resultingEvents = fold(currentEvents, ProduceCar(Employee("Lea"), CarModel.MODEL_T))
            }

            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }
            And(
                "The error should contains the message \"There is not enough part to build ${CarModel.MODEL_T} car\" "
            ) {
                resultingEvents.mapLeft { it shouldContain "There is not enough part to build ${CarModel.MODEL_T} car" }
            }
        }

        Scenario("An employee may only produce a car once a day") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given(
                "Yoda an employee of the factory who has already built a car today" +
                        " and enough part left on the shelf to build a model T car"
            ) {
                currentEvents = listOf(
                    EmployeeAssignedToFactory(Employee("Yoda")),
                    ShipmentUnpackedInCargoBay(
                        Employee("Yoda"),
                        listOf(
                            CarPartPackage(CarPart("wheel"), 60),
                            CarPartPackage(CarPart("engine"), 40),
                            CarPartPackage(CarPart("bits and pieces"), 20)
                        )
                    ),
                    CarProduced(
                        Employee("Yoda"),
                        CarModel.MODEL_T,
                        CarModel.neededParts(CarModel.MODEL_T)
                    )
                )
            }
            When("Yoda is ordered to build a car") {
                resultingEvents = fold(currentEvents, ProduceCar(Employee("Yoda"), CarModel.MODEL_T))
            }

            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }

            Then("The error should contains a message \"Yoda may only produce a car once a day\"") {
                resultingEvents.mapLeft {
                    it shouldContain "Yoda may only produce a car once a day"
                }
            }
        }
    }

})
