plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("co.touchlab.skie") version "0.5.2"
    id("com.google.devtools.ksp") version "1.9.20-1.0.13"
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-19"
}

kotlin {
    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
        val commonMain by getting {
            dependencies {
                api("com.rickclephas.kmm:kmm-viewmodel-core:1.0.0-ALPHA-15")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("io.ktor:ktor-client-core:2.3.5")
                implementation("io.insert-koin:koin-core:3.5.0")
                implementation("dev.bluefalcon:blue-falcon:1.0.0")
                implementation("co.touchlab:kermit:2.0.2")
                implementation("co.touchlab:kermit-koin:2.0.2")
                implementation("com.ditchoom:buffer:1.3.6")
                implementation("com.benasher44:uuid:0.8.1")
                implementation("com.juul.kable:core:0.28.0-rc")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.8.0")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
                implementation("io.ktor:ktor-client-okhttp:2.3.5")
                implementation("io.insert-koin:koin-android:3.5.0")
            }
        }
        val appleMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.5")
            }
        }
        val androidIosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("dev.icerock.moko:permissions:0.16.0")
            }
        }

        androidMain.dependsOn(androidIosMain)
        iosMain.get().dependsOn(androidIosMain)
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

skie {
    analytics {
        disableUpload.set(true)
    }
}