package domain

sealed class FactoryCommand

data class ProduceCar(val employee: Employee, val carModel: CarModel): FactoryCommand()
data class UnpackAndInventoryShipmentInCargoBay(val employee: Employee): FactoryCommand()
data class AssignEmployeeToFactory(val employee: Employee): FactoryCommand()
data class TransferShipmentToCargoBay(val shipment: Shipment): FactoryCommand()
