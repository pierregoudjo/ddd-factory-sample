import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
}

group = "xyz.goudjo.btw-samples"
version = "1.0-SNAPSHOT"

val arrowVersion = "0.13.0"
val speckVersion = "2.0.15"
val kotestVersion = "4.4.3"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.3")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$speckVersion")
    testImplementation ("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation ("io.kotest:kotest-assertions-arrow:$kotestVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$speckVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
}

tasks {
    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
