plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.cookie"
version = ""

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("com.github.Archy-X:AureliumSkills:Beta1.2.10")
    compileOnly(files("G:\\coding\\Test Servers\\TimeRewards\\plugins\\CookieCore.jar"))
    compileOnly("net.luckperms:api:5.4")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks {
    shadowJar {
        destinationDirectory.set(file("G:\\coding\\Test Servers\\TimeRewards\\plugins"))
        archiveBaseName.set("Prestiges")
        archiveClassifier.set("")
        relocate("co.aikar.commands", "me.cookie.prestiges.afc")
        relocate("co.aikar.locales", "me.cookie.prestiges.locales")
    }
    compileJava {
        options.compilerArgs.add("-parameters")
        options.isFork = true
        options.forkOptions.executable = "javac"
    }
}

