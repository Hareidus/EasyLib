import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.27"
    kotlin("jvm") version "2.1.0"
    `maven-publish`
}


taboolib {
    subproject = true

    env {
        // 安装模块
        install(Basic, Bukkit, BukkitHook, BukkitNMSUtil , BukkitUI,Kether,Metrics)
    }
    version {
        taboolib = "6.2.3-1a8d7125"
        coroutines = "1.8.1"
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
    maven("https://repo.magstar.top/repository/maven-releases/")
}

dependencies {
    compileOnly("pers.neige.neigeitems:NeigeItems:1.21.95")
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
    compileOnly("top.magstar.lib:MagstarLib:1.0.1")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    taboo("top.maplex.arim:Arim:1.2.14") // 替换为最新版本
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

// 配置发布到本地Maven仓库
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // 使用正确的组件名称
            // 添加这两行
            groupId = "easy"  // 或者你想要的其他groupId
            artifactId = "EasyLib"
            version = "1.1.0"  // 或者你想要的版本号

            pom {
                name.set("EasyLib")
                description.set("Easy Taboolib Lib")
                url.set("http://example.com/your-library")

                developers {
                    developer {
                        id.set("Hareidus")
                        name.set("Hareidus")
                        email.set("your.email@example.com")
                    }
                }
            }
        }
    }

    // 添加这一部分来指定你的本地仓库路径
    repositories {
        maven {
            name = "LocalDirectory"
            url = uri("file:///B:/MyLibs")
        }
    }
}
