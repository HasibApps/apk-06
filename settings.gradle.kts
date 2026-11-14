pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // yt-dlp / ffmpeg AARs (youtubedl-android) are sometimes only published here
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "YTDownloader"
include(":app")
