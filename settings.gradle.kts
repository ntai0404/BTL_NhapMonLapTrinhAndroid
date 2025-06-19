pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url= uri("https://jitpack.io")
        }//thêm JitPack vào danh sách các kho (repositories) mà Gradle có thể
        // dùng để tìm và tải thư viện về cho dự án Android
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url=uri( "https://jitpack.io") }
    }
}

rootProject.name = "project227.2"
include(":app")
 