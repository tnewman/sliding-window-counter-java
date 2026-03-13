plugins {
    `java-library`
    alias(libs.plugins.spotless)
    alias(libs.plugins.jmh)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    jmh(libs.jmh.core)
    jmhAnnotationProcessor(libs.jmh.annprocess)
}

jmh {
    fork.set(1)
    warmupIterations.set(3)
    iterations.set(5)
    benchmarkMode.set(listOf("thrpt"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

spotless {
    java {
        googleJavaFormat()
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
