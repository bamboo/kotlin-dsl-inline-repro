plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":api"))

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}