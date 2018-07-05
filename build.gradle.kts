import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlin_version: String by extra("1.2.60-eap-7")

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
    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/")}
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8", kotlin_version))
    compile("com.squareup:kotlinpoet:0.8.0-SNAPSHOT")
    testCompile("io.kotlintest:kotlintest-runner-junit5:3.1.7")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<Test>{
    useJUnitPlatform()
}