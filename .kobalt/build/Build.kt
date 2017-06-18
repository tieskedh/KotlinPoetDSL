import com.beust.kobalt.*
import com.beust.kobalt.plugin.application.*
import com.beust.kobalt.plugin.packaging.*
// D:\IdeaProjects\poet\kobalt\src\Build.kt

val p = project {
    name = "poet"
    group = "eu.dhaan"
    artifactId = name
    version = "0.1"

    sourceDirectories {
        path("src/main/src/")
    }
    sourceDirectoriesTest {
        path("src/main/test/src")
    }
    dependencies {
        compile("org.jetbrains.kotlin:kotlin-stdlib:1.1.2-5")
        compile("com.squareup:kotlinpoet:0.3.0")
    }

    dependenciesTest {

    }

    assemble {
        jar {

        }
    }

    application {
        mainClass = "eu.dhaan."
    }
}