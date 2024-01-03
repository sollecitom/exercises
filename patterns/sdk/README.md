# SDK pattern

An SDK is a library that abstracts all communications between client and server, bidirectionally.

## Why

Because client applications shouldn't be forced to code against your API to integrate. A properly crafted client library provides tested workflows, and can leverage approaches beyond what your transport protocol can do.

## Side benefits

### Testability of frontend applications

Frontend applications are typically tested against backend services hosted in an environment. This is slow, cumbersome, and unreliable.

Instead, the SDK pattern allows testing a web application against the in-memory implementation of an SDK. You assume the backend service work, and test against your SDK API, using the in-memory implementation under the hood.

Your SDK also has a remote implementation that implements its API against the backend services. The responsibility of testing that the SDK works stays with the SDK project, rather than with each frontend application.

### Testability of backend applications

Backend applications are tested by driving them either via headless scripts. These tests can benefit from using the SDK API instead, so that they're more type-safe and expressive.

Under the hood, the tests for backend services use the remote implementation of your SDK, typically pointing at the services running as OCI containers locally, or remotely on an environment.

## Notes

While a properly crafted SDK offers many benefits, SDKs are inherently tied to a programming language. This means that if you want to maximize the benefits, you should provide SDKs in the main programming languages used by your customers.

Typically, a TypeScript SDK is a great start, followed by a JVM-language one (Java/Kotlin/Scala) if needed. Other to consider are Python, Rust/C++, Golang, etc. This depends on your business domain, as certain programming languages are more used in certain contexts e.g., Python in data science and Golang in Cloud infrastructure.