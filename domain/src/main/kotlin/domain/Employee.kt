package domain

inline class Employee(private val employeeName: String) {
    override fun toString(): String {
        return employeeName
    }
}
