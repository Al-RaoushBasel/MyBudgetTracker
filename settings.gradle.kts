pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // for JitPack
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // or FAIL_ON_PROJECT_REPOS if you want to strictly use settings repositories
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // for JitPack
    }
}

rootProject.name = "My-Budget-Tracker"
include(":app")
