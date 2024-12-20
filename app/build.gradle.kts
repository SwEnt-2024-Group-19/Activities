import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.gms)
    alias(libs.plugins.sonar)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("jacoco")
}

android {
    namespace = "com.android.sample"
    compileSdk = 34

    // Load the API key from local.properties
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }

    val mapsApiKey: String = System.getenv("MAPS_API_KEY") ?: localProperties.getProperty("MAPS_API_KEY") ?: ""

    defaultConfig {
        applicationId = "com.android.sample"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.android.sample.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    signingConfigs {
        create("release") {
            // Resolve the keystore path safely
            storeFile = file("keystore/keystore.jks")
            storePassword = (System.getenv("KEYSTORE_PASSWORD") ?: properties["KEYSTORE_PASSWORD"]) as String?
            keyAlias = (System.getenv("KEY_ALIAS") ?: properties["KEY_ALIAS"]) as String?
            keyPassword = (System.getenv("KEY_PASSWORD") ?: properties["KEY_PASSWORD"]) as String?
        }
    }
    testCoverage {
        jacocoVersion = "0.8.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xjvm-default=all"
        languageVersion = "1.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
    kapt {
        correctErrorTypes = true
    }


    // Robolectric needs to be run only in debug. But its tests are placed in the shared source set (test)
    // The next lines transfers the src/test/* from shared to the testDebug one
    //
    // This prevent errors from occurring during unit tests
    sourceSets.getByName("testDebug") {
        val test = sourceSets.getByName("test")

        java.setSrcDirs(test.java.srcDirs)
        res.setSrcDirs(test.res.srcDirs)
        resources.setSrcDirs(test.resources.srcDirs)
    }

    sourceSets.getByName("test") {
        java.setSrcDirs(emptyList<File>())
        res.setSrcDirs(emptyList<File>())
        resources.setSrcDirs(emptyList<File>())
    }
}

sonar {
    properties {
        property("sonar.projectKey", "gf_android-sample")
        property("sonar.projectName", "Android-Sample")
        property("sonar.organization", "gabrielfleischer")
        property("sonar.host.url", "https://sonarcloud.io")

        // Comma-separated paths to the various directories containing the *.xml JUnit report files.
        property("sonar.junit.reportPaths", "${project.layout.buildDirectory.get()}/test-results/testDebugUnitTest/")

        // Paths to xml files with Android Lint issues.
        property("sonar.androidLint.reportPaths", "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml")

        // Paths to JaCoCo XML coverage report files.
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")

        // Set Sonar to focus on line coverage as the primary metric.
        property("sonar.coverage.mode", "line")
    }
}


// When a library is used both by robolectric and connected tests, use this function
fun DependencyHandlerScope.globalTestImplementation(dep: Any) {
    androidTestImplementation(dep)
    testImplementation(dep)
}

dependencies {

    implementation ("io.github.vanpra.compose-material-dialogs:datetime:0.8.1-rc")
    implementation(libs.androidx.games.activity)
    implementation(libs.core)
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:1.1.6")

    implementation(libs.firebase.storage.ktx)
    implementation(libs.play.services.cast.framework)
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Core
    implementation(libs.core.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.kotlinx.serialization.json)

    // Jetpack Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    // Extended Material Icons
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.test.core.ktx)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.material)



    // JUnit for unit testing
    testImplementation("junit:junit:4.13.2")


    // Or replace with Mockito if preferred:
    // testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-core:5.3.1")



    // AndroidX Core Testing library for LiveData and ViewModel testing (optional)
    testImplementation("androidx.arch.core:core-testing:2.1.0")


    //----



    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Google Service and Maps
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.location)

    // Firebase
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)

    //CameraX
    val cameraxVersion = "1.3.0-rc01"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")

    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")

    // Networking with OkHttp
    implementation(libs.okhttp)

    // Testing Unit
    testImplementation(libs.junit)

    //testImplementation("io.mockk:mockk:1.12.0")
    testImplementation(libs.json)

    // Test UI
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Added for consistent Mockito usage across tests
    testImplementation(libs.mockito.core.v570)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockito.kotlin)
    androidTestImplementation("org.mockito:mockito-android:5.7.0")

    androidTestImplementation(libs.kaspresso)
    androidTestImplementation(libs.kaspresso.allure.support)
    androidTestImplementation(libs.kaspresso.compose.support)

    testImplementation(libs.kotlinx.coroutines.test)

    implementation(libs.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.espresso.intents)
    testImplementation(libs.junit)
    globalTestImplementation(libs.androidx.junit)
    globalTestImplementation(libs.androidx.espresso.core)

    //notifications
    implementation("androidx.work:work-runtime-ktx:2.9.0")


    // ------------- Jetpack Compose ------------------


    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    // Material Design 3
    implementation(libs.compose.material3)
    // Integration with ViewModels
    implementation(libs.compose.viewmodel)
    // Android Studio Preview support
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    // UI Tests
    globalTestImplementation(libs.compose.test.junit)
    debugImplementation(libs.compose.test.manifest)

    // ------------- Coil ------------------
    implementation(libs.coil.compose)
    implementation(libs.coil.kt.coil.compose)
    implementation(libs.coil.network.okhttp)
    // ----------       Robolectric     ------------
    testImplementation("org.robolectric:robolectric:4.11.1") {
        exclude(group = "com.google.auto.service", module = "auto-service")
    }
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("org.bouncycastle:bcprov-jdk15on:1.68")

    // ----------       Hilt     ------------
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kaptTest(libs.hilt.android.compiler)
    testImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)
    implementation(libs.gson.v210)
    implementation(libs.converter.gson)

}

tasks.withType<Test> {
    // Configure Jacoco for each tests
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}
tasks.withType<Test> {
    onlyIf {
        !name.toLowerCaseAsciiOnly().contains("release")
    }
}


tasks.register("jacocoTestReport", JacocoReport::class) {
    mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required = true
        html.required = true
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/sigchecks/**",
    )
    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.buildDir) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
    })
}