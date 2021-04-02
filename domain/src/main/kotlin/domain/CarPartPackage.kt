package domain

typealias CarPartPackages = List<CarPartPackage>

data class CarPartPackage(val part: CarPart, val quantity: Int)
