# 6. return result datatype from commands instead of raising exceptions

Date: 2021-03-31

## Status

Accepted

## Context

So far, we use exception to notify if a domain rule is infringed. It is not a good pratice as it is not really an exception but more that a business invariant has not been met

## Decision

A better way to convey business rule error would be to use the Result dataype from kittinunf/Result or Either from the Arrow. The choice here will be Arrow which seems like the default lib for functional programming in Kotlin

## Consequences

Test will need to be adapted
