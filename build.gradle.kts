plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
}

group = "dev.oribuin"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    disableAutoTargetJvm()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://libraries.minecraft.net")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.md-5.net/content/repositories/public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io/")
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")

    api("dev.rosewood:rosegarden:1.4.7-SNAPSHOT")
    api("net.objecthunter:exp4j:0.4.8")
    api("com.jeff-media:MorePersistentDataTypes:2.4.0")
    api("net.wesjd:anvilgui:1.10.4-SNAPSHOT")
    api("dev.triumphteam:triumph-gui:3.1.11") {
        exclude(group = "net.kyori", module = "*")
    }
    
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
    
    // External Plugins
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")
}

tasks {

    this.compileJava {
        this.options.compilerArgs.add("-parameters")
        this.options.isFork = true
        this.options.encoding = "UTF-8"
    }

    this.shadowJar {
        this.archiveClassifier.set("")

        this.relocate("dev.rosewood.rosegarden", "${project.group}.skyblock.libs.rosegarden")
        this.relocate("net.objecthunter.exp4j", "${project.group}.skyblock.libs.exp4j")
        this.relocate("com.jeff_media", "${project.group}.skyblock.libs.morepersistentdatatypes")
        this.relocate("org.jetbrains", "${project.group}.skyblock.libs.jetbrains")
        this.relocate("net.wesjd.anvilgui", "${project.group}.skyblock.libs.anvilgui")
        this.relocate("dev.triumphteam.gui", "${project.group}.skyblock.libs.triumphgui")

        // rosegarden should be relocating this
        this.relocate("com.zaxxer", "${project.group}.skyblock.libs.rosegarden.hikari")
        this.relocate("org.slf4j", "${project.group}.skyblock.libs.rosegarden.slf4j")
    }
    
    this.processResources {
        this.filesMatching("**/plugin.yml") {
            this.expand("version" to project.version)
        }
    }
    
    this.jar {
        this.dependsOn(reobfJar)
    }
    
    this.build {
        this.dependsOn(shadowJar)
    }
    
}