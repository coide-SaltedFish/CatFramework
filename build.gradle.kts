import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
}

group = "org.sereinfish.cat.frame"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.yaml:snakeyaml:2.2")
//    implementation("org.apache.maven:maven-model:3.9.6")

    implementation("org.apache.maven.resolver:maven-resolver-api:1.9.20")
    implementation("org.apache.maven.resolver:maven-resolver-impl:1.9.20")
    implementation("org.apache.maven.resolver:maven-resolver-transport-file:1.9.20")
    implementation("org.apache.maven.resolver:maven-resolver-transport-http:1.9.20")
    implementation("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.20")
    implementation("org.apache.maven:maven-resolver-provider:3.9.7")
    implementation("org.apache.maven.resolver:maven-resolver-supplier:1.9.20")


    // 日志依赖
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.5.3")

    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}