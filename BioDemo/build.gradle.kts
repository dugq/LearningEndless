plugins {
    id("java")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}



tasks.jar {
    archiveFileName.set("BIO.jar")
    manifest.attributes.set("Main-Class" , "com.example.Main")
}

tasks.test {
    useJUnitPlatform()
}
