import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import model.Employee
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object AssignEmployeeToTheFactoryFeature : Spek({

    Feature("Assign employee to the factory") {

        Scenario("Assign employee to an empty factory") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("An empty factory") {
                currentEvents = emptyList()
            }
            When("An employee named \"Fry\" comes to the factory") {
                resultingEvents = fold(currentEvents, AssignEmployeeToFactory(Employee("Fry")))
            }
            Then("Fry is assigned to the factory") {
                resultingEvents.shouldBeRight()
                resultingEvents.map {
                    it shouldContain EmployeeAssignedToFactory(Employee("Fry"))
                }
            }
        }

        Scenario("An already assigned employee is assigned again") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>

            Given("A factory where an employee named \"Fry\" is assigned") {
                currentEvents = listOf(EmployeeAssignedToFactory(Employee("Fry")))

            }

            When("An employee named \"Fry\" comes to the factory") {
                resultingEvents = fold(currentEvents, AssignEmployeeToFactory((Employee("Fry"))))
            }

            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }

            Then("The error message should contain \"the name of Fry only one can have\" ") {
                resultingEvents.mapLeft {
                    it shouldContain "the name of Fry only one can have"
                }
            }
        }

        Scenario("Bender comes to the factory") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: Either<String, List<FactoryDomainEvent>>


            Given("An empty factory") {
                currentEvents = emptyList()
            }

            When("An employee named Bender is assigned to the factory") {
                resultingEvents = fold(currentEvents, AssignEmployeeToFactory(Employee("Bender")))
            }

            Then("There should be an error") {
                resultingEvents.shouldBeLeft()
            }

            Then("The error message should contain \"Guys with name 'bender' are trouble\" ") {
                resultingEvents.mapLeft { it shouldContain "Guys with the name 'bender' are trouble" }
            }
        }
    }
})
