import arrow.core.Either
import model.CarModel
import model.Curse
import model.Employee
import model.Shipment

internal fun assignEmployeeToFactory(employee: Employee, state: Factory) = when {
    thisEmployeeIsAssignedToTheFactory(state, employee) ->
        fail("the name of $employee only one can have")

    // another check that needs to happen when assigning employees to the factory
    // multiple options to prove this critical business rule:
    // John Bender: http://en.wikipedia.org/wiki/John_Bender_(character)#Main_characters
    // Bender Bending Rodríguez: http://en.wikipedia.org/wiki/Bender_(Futurama)
    thisEmployeeIsNamedBender(employee) ->
        fail("Guys with the name 'bender' are trouble")

    else -> {
        doPaperWork("Assign employee to the factory")
        success(listOf(EmployeeAssignedToFactory(employee)))
    }
}
private fun thisEmployeeIsNamedBender(employee: Employee) = employee == Employee("Bender")


internal fun produceCar(employee: Employee, carModel: CarModel, state: Factory) = when {
    thisEmployeeIsNotAssignedToTheFactory(state, employee) ->
        fail("$employee must be assigned to the factory to build a car")

    thisEmployeeHasAlreadyBuiltACar(state, employee) ->
        fail("$employee may only produce a car once a day")

    thereIsNotEnoughpartToBuildtheCar(state, carModel) ->
        fail("There is not enough part to build $carModel car")

    else -> {
        doRealWork("Building the car...")
        doPaperWork("Writing car specification documents")
        success(listOf(CarProduced(employee, carModel, CarModel.neededParts(carModel))))
    }
}
private fun thereIsNotEnoughpartToBuildtheCar(state: Factory, carModel: CarModel) =

    !thereIsEnoughPartToBuildTheCar(state, carModel)
private fun thisEmployeeHasAlreadyBuiltACar(state: Factory, employee: Employee) =

    state.employeesWhoHasBuiltCars.contains(employee)
private fun thereIsEnoughPartToBuildTheCar(state: Factory, carModel: CarModel): Boolean {

    val neededPartsToBuildTheCar = CarModel.neededParts(carModel)
    return neededPartsToBuildTheCar.fold(true,
        { acc, curr -> acc && state.inventory.getOrDefault(curr.part, 0) >= curr.quantity }
    )
}

internal fun unpackAndInventoryShipmentInCargoBay(employee: Employee, state: Factory) = when {
    thisEmployeeIsNotAssignedToTheFactory(state, employee) ->
        fail("$employee must be assigned to the factory to unpack the cargo bay")

    thisEmployeeAlreadyUnpackedShipmentInCargoBay(state, employee) ->
        fail("$employee may only unpack and inventory all Shipments in the CargoBay once a day")

    thereIsNoShipmentWaitingToBeUnpackedInTheCargoBay(state) ->
        fail("There should be a shipment to unpack")

    else -> {
        doRealWork("passing supplies")
        success(
            listOf(
                ShipmentUnpackedInCargoBay(
                    employee,
                    state.shipmentsWaitingToBeUnpacked.map { it.carPartPackages }.flatten()
                )
            )
        )
    }
}
private fun thereIsNoShipmentWaitingToBeUnpackedInTheCargoBay(state: Factory) =

    !thereIsAnyShipmentWaitingToBeUnpackedInTheCargoBay(state)
private fun thisEmployeeIsNotAssignedToTheFactory(state: Factory, employee: Employee) =

    !thisEmployeeIsAssignedToTheFactory(state, employee)
private fun thereIsAnyShipmentWaitingToBeUnpackedInTheCargoBay(state: Factory) = state.shipmentsWaitingToBeUnpacked.isNotEmpty()

private fun thisEmployeeAlreadyUnpackedShipmentInCargoBay(state: Factory, employee: Employee) =

    state.employeeWhoHasUnpackedShipmentsInCargoBayToday.contains(employee)
private fun thisEmployeeIsAssignedToTheFactory(state: Factory, employee: Employee) =

    state.listOfEmployeeNames.contains(employee)

internal const val NUMBER_OF_PARTS_TOO_MUCH_TO_HANDLE = 10

internal const val CARGO_BAY_MAX_CAPACITY = 2

internal fun transferShipmentToCargoBay(shipment: Shipment, state: Factory) = when {
    isThereAnyEmployeeInFactory(state) -> fail("there has to be somebody at the factory in order to accept the shipment")
    isShipmentEmpty(shipment) -> fail("Empty shipments are not accepted!")
    isCargoBayFull(state, CARGO_BAY_MAX_CAPACITY) -> fail("More than two shipments can't fit into this cargo bay")
    else -> {
        doRealWork("opening cargo bay doors")
        val transferredEvent = listOf(ShipmentTransferredToCargoBay(shipment = shipment))

        val totalCountOfParts = shipment.carPartPackages.sumOf { it.quantity }

        val curseWordEvent = when {
            (totalCountOfParts > NUMBER_OF_PARTS_TOO_MUCH_TO_HANDLE) -> listOf(
                CurseWordUttered(
                    Curse(
                        theWord = "Boltov tebe v korobky peredach",
                        meaning = "awe in the face of the amount of parts delivered"
                    )
                )
            )
            else -> emptyList()
        }
        success(transferredEvent + curseWordEvent)
    }
}
private fun isCargoBayFull(state: Factory, maxCapacity: Int) = state.shipmentsWaitingToBeUnpacked.size >= maxCapacity


private fun isShipmentEmpty(shipment: Shipment) = shipment.carPartPackages.isEmpty()

private fun isThereAnyEmployeeInFactory(state: Factory) = state.listOfEmployeeNames.isEmpty()

fun fold(events: List<FactoryDomainEvent>, command: FactoryCommand): Either<String, List<FactoryDomainEvent>> {
    val factory = events.fold(Factory.empty) { acc, curr -> evolve(curr, acc) }
    return decide(command, factory)
}

operator fun List<FactoryDomainEvent>.times(command: FactoryCommand): Either<String, List<FactoryDomainEvent>> {
    return fold(this, command)
}
