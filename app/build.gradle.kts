plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.raybit.newvendor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.raybit.newvendor"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            debug {
                buildConfigField("String", "API_URL", "\"http://192.168.100.21/hospital/public/\"")
                buildConfigField("String", "SOCKET_BASE_URL", "\"http://192.168.100.30:4000\"")
                buildConfigField("String", "IMAGE_URL", "\"http://192.168.241.45/\"")
                buildConfigField("String", "AG_APP_ID", "\"52da6c314e4e43c69907e9b6b60f6706\"")
                buildConfigField("String", "AG_CERTIFY", "\"c82c29959c2c41e497f78a749fec883c\"")
            }
            release {
                buildConfigField("String", "API_URL", "\"http://vcsnews.vintageconsultancy.com/\"")
                buildConfigField("String", "SOCKET_BASE_URL", "\"http://159.65.147.143:3001/\"")
                buildConfigField(
                    "String",
                    "IMAGE_URL",
                    "\"http://vcsnews.vintageconsultancy.com/\""
                )
                buildConfigField("String", "AG_APP_ID", "\"52da6c314e4e43c69907e9b6b60f6706\"")
                buildConfigField("String", "AG_CERTIFY", "\"c82c29959c2c41e497f78a749fec883c\"")
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
    }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }


    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
        implementation("androidx.datastore:datastore-preferences:1.1.1")
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        implementation("androidx.multidex:multidex:2.0.1")

        //permission
        implementation("com.github.permissions-dispatcher:permissionsdispatcher:4.8.0")
        kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:4.8.0")
        //hilt
        implementation("com.google.dagger:hilt-android:2.51.1")
        // implementation(libs.firebase.crashlytics.buildtools)
        // implementation(libs.androidx.monitor)
        kapt("com.google.dagger:hilt-android-compiler:2.51.1")

        // Networking
        implementation("com.squareup.okhttp3:okhttp:4.11.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
        implementation("com.squareup.retrofit2:retrofit:2.6.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")


        //lifecycle
        implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")

//        implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
//        kapt ("androidx.hilt:hilt-compiler:1.0.0")

        //navigation
        implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
        implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

        //imageview
        implementation("com.makeramen:roundedimageview:2.3.0")

        implementation("com.github.stfalcon-studio:StfalconImageViewer:1.0.1")
        implementation("com.squareup.picasso:picasso:2.71828")
        implementation("com.squareup.picasso:picasso:2.71828")

        // Lottie Animation Library
        implementation("com.airbnb.android:lottie:6.5.2")


        // Firebase
        implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-analytics-ktx")
        implementation("com.google.firebase:firebase-database-ktx")
        implementation("com.google.firebase:firebase-messaging-ktx")
        implementation("com.google.firebase:firebase-messaging-directboot:24.0.0")

        // java time
        implementation("com.jakewharton.threetenabp:threetenabp:1.4.7")

// image glide
        implementation("com.airbnb.android:lottie:3.4.4")
        implementation("com.github.bumptech.glide:glide:4.16.0")
        implementation("jp.wasabeef:glide-transformations:4.3.0")

        // shimmer
        implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

        // Paging
        implementation("androidx.paging:paging-runtime-ktx:3.3.0")

        //permission
        implementation("pub.devrel:easypermissions:3.0.0")

        //filepicker
        implementation("io.github.chochanaresh:filepicker:0.1.9")
//        implementation ("droidninja:filepicker:2.2.5")
        //compresser
        implementation("id.zelory:compressor:3.0.1")
        //sdp
        implementation("com.intuit.sdp:sdp-android:1.1.1")
        // Location
        implementation("com.google.android.gms:play-services-location:21.3.0")
        implementation("com.google.android.gms:play-services-maps:19.0.0")
        implementation("com.myfatoorah:myfatoorah:2.2.0")
        // agora
        implementation("io.agora.rtc:voice-sdk:4.2.6")
        implementation("io.socket:socket.io-client:2.1.0") {
            exclude(group = "org.json", module = "json")
            implementation("com.jakewharton.timber:timber:5.0.1")

            implementation ("com.github.pawegio:KAndroid:0.8.7@aar")
            implementation ("com.hbb20:ccp:2.7.0")
            implementation("de.hdodenhof:circleimageview:3.1.0")

        }


    }
