@file:Suppress("UnstableApiUsage")

rootProject.name = "exercises"

module("starter", "core")
module("starter", "test")
module("domain-modelling", "shopping-cart", "1")
module("patterns", "sdk", "api")
module("patterns", "sdk", "implementation", "memory")
module("patterns", "sdk", "implementation", "remote")

fun module(vararg pathSegments: String) {
    val projectName = pathSegments.last()
    val path = pathSegments.dropLast(1)
    val group = path.joinToString(separator = "-")
    val directory = path.joinToString(separator = "/", prefix = "./")

    include("${rootProject.name}-${if (group.isEmpty()) "" else "$group-"}$projectName")
    project(":${rootProject.name}-${if (group.isEmpty()) "" else "$group-"}$projectName").projectDir = mkdir("$directory/$projectName")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")