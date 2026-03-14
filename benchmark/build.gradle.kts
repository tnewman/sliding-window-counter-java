plugins {
    java
    alias(libs.plugins.jmh)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))

    jmh(libs.jmh.core)
    jmhAnnotationProcessor(libs.jmh.annprocess)
}


jmh {
    fork.set(1)
    warmupIterations.set(3)
    iterations.set(5)
    benchmarkMode.set(listOf("thrpt"))
}
