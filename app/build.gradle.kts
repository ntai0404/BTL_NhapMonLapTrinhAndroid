plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "org.meicode.project2272"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.meicode.project2272"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures{
        //truy cập trực tiếp đến các View trong layout không cần findViewById().
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //additional
    // Thư viện dùng để tải ảnh từ URL, thư viện nội bộ hoặc drawable và hiển thị ảnh lên ImageView
    implementation("com.github.bumptech.glide:glide:4.13.2")
    // Chuyển đổi qua lại giữa JSON và đối tượng Java/Kotlin.
    implementation("com.google.code.gson:gson:2.8.6")
    //Thư viện tạo thanh điều hướng dạng "chip" đẹp mắt, thay thế BottomNavigationView mặc định của Android
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    //Thư viện hiển thị hình tròn
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    //Xử lý thao tác với Cloudinary
    implementation ("com.cloudinary:cloudinary-android:2.4.0")
    // Trang >.<
    // Thêm thư viện MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

}