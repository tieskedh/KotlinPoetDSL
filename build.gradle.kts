import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlin_version: String by extra("1.3.40")

    repositories {
        maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap") }
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlin_version))
    }
}

group = "nl.devhaan"
version = "1.0-SNAPSHOT"

plugins {
    java
    jacoco
    maven
}


tasks.withType<JacocoReport>{
    reports{
        xml.isEnabled = true
        html.isEnabled = true
    }
    val check by tasks
    check.dependsOn(this)
}

apply {
    plugin("kotlin")
}
val kotlin_version: String by extra

repositories {
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
}
dependencies {
    compile(kotlin("stdlib-jdk8", kotlin_version))
    compile("com.squareup:kotlinpoet:1.0.0-RC3")
    testCompile("io.kotlintest:kotlintest-runner-junit5:3.1.9")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xuse-experimental=kotlin.Experimental")
    }
}

tasks.withType<Test> {
    testLogging {
        // set options for log level LIFECYCLE
        events = setOf(FAILED, PASSED, SKIPPED, STANDARD_OUT)
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        // set options for log level DEBUG and INFO
        debug {
            events = setOf(STARTED, FAILED, PASSED, SKIPPED, STANDARD_ERROR, STANDARD_OUT)
            exceptionFormat = TestExceptionFormat.FULL
        }
        info{
            events = debug.events
            exceptionFormat = debug.exceptionFormat
        }
    }
    addTestListener(object : TestListener {
        override fun beforeTest(p0: TestDescriptor?) = Unit
        override fun beforeSuite(p0: TestDescriptor?) = Unit
        override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit
        override fun afterSuite(desc: TestDescriptor, result: TestResult) {
            printResults(desc, result)
        }
    })
    useJUnitPlatform()
}

fun printResults(desc: TestDescriptor, result: TestResult) {
    if (desc.parent != null) {
        val output = result.run {
            "Results: $resultType (" +
                    "$testCount tests, " +
                    "$successfulTestCount successes, " +
                    "$failedTestCount failures, " +
                    "$skippedTestCount skipped" +
                    ")"
        }
        val testResultLine = "|  $output  |"
        val repeatLength = testResultLine.length
        val seperationLine = "-".repeat(repeatLength)
        println(seperationLine)
        println(testResultLine)
        println(seperationLine)
    }
}