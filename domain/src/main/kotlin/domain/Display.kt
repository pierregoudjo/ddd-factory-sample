package domain

import arrow.core.Either

internal fun doPaperWork(workName: String) {
    println(" > Work: Papers... $workName ...")
}

internal fun doRealWork(workName: String) {
    println(" > Work: heavy stuff... $workName ...")
}

internal fun fail(message: String): Either.Left<String> {
    println(":> $message".red())
    return Either.Left(message)
}

internal fun <Element> success(element: Element): Either.Right<Element> {
    return Either.Right(element)
}

internal fun String.red() = "\u001B[41m$this\u001B[0m"
