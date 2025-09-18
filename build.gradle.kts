import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.27"
    kotlin("jvm") version "2.1.0"
}


taboolib {
    env {
        // 安装模块
        install(Basic, Bukkit, BukkitHook, BukkitNMSUtil , BukkitUI,Kether,Metrics)
    }
    version {
        taboolib = "6.2.3-e102d76"
        coroutines = "1.8.1"
    }
    relocate("top.maplex.arim","EasyLib.arim")
    dependencies {
        taboo("top.maplex.arim:Arim:1.2.14") // 替换为最新版本
    }

    description {
        dependencies{
            name("neigeitems").optional(true)
            name("SX-Item").optional(true)
            name("MMOItems").optional(true)
            name("MythicLib").optional(true)
            name("ItemsAdder").optional(true)
        }
    }
}

repositories {
    mavenCentral()
    maven("https://r.irepo.space/maven/")
}

dependencies {
    compileOnly("pers.neige.neigeitems:NeigeItems:1.21.95")
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
