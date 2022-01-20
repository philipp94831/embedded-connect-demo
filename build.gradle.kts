plugins {
    java
}

allprojects {
    group = "com.bakdata.kafka"

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }
        compileTestJava {
            options.encoding = "UTF-8"
        }
        test {
            useJUnitPlatform()
        }
    }

    repositories {
        mavenCentral()
    }
}

dependencies {
    val junitVersion: String by project
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.20.2")
    val kafkaVersion: String by project
    testImplementation(group = "net.mguenther.kafka", name = "kafka-junit", version = kafkaVersion)
    testImplementation(group = "org.apache.kafka", name = "connect-file", version = kafkaVersion)
}
