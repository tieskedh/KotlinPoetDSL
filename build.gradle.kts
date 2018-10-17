import org.jetbrains.kotlin.config.AnalysisFlag.Flags.experimental
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlin_version: String by extra("1.3.0-rc-146")

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

plugins{
    java
    maven
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
    compile("com.squareup:kotlinpoet:1.0.0-RC1")
    testCompile("io.kotlintest:kotlintest-runner-junit5:3.1.9")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xuse-experimental=kotlin.Experimental")
    }
}

tasks.withType<Test>{
    useJUnitPlatform()
}
