/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import groovy.xml.*

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlin)
}

data class TWAManifestOnlyShortcut(
    val name: String?,
    val shortName: String?,
    val url: String?,
    val icon: String?,
)

fun generateShortcutListString(s: TWAManifestOnlyShortcut, i: Int, twaManifest: TWAManifest): String {
    return """
        <shortcut
            "android:shortcutId": "shortcut${i}"
            "android:enabled": "true"
            "android:icon": "@drawable/${s.icon}"
            "android:shortcutShortLabel": "@string/shortcut_short_name_${i}"
            "android:shortcutLongLabel": "@string/shortcut_name_${i}">
            <intent
                "android:action": "android.intent.action.MAIN"
                "android:targetPackage": ${twaManifest.applicationId}
                "android:targetClass": "${twaManifest.applicationId}.LauncherActivity"
                "android:data": ${s.url}
            <categories "android:name": "android.intent.category.LAUNCHER" />
        </shortcut>
    """.trimIndent()
}

data class TWAManifest(
    val applicationId: String,
    val hostName: String,
    val launchUrl: String,
    val name: String,
    val launcherName: String,
    val themeColor: String, // The color used for the status bar.
    val navigationColor: String, // The color used for the navigation bar.
    val navigationColorDark: String, // The color used for the dark navbar.
    val navigationDividerColor: String, // The navbar divider color.
    val navigationDividerColorDark: String, // The dark navbar divider color.
    val backgroundColor: String, // The color used for the splash screen background.
    val enableNotifications: Boolean = false, // Set to true to enable notification delegation.
    // Every shortcut must include the following fields:
    // - name: String that will show up in the shortcut.
    // - short_name: Shorter string used if |name| is too long.
    // - url: Absolute path of the URL to launch the app with (e.g '/create').
    // - icon: Name of the resource in the drawable folder to use as an icon.
    val shortcuts: Array<TWAManifestOnlyShortcut> = emptyArray<TWAManifestOnlyShortcut>(),
    // The duration of fade out animation in milliseconds to be played when removing splash screen.
    val splashScreenFadeOutDuration: Number = 300,
    val generatorApp: String = "bubblewrap-cli", // Application that generated the Android Project
    // The fallback strategy for when Trusted Web Activity is not available. Possible values are
    // 'customtabs' and 'webview'.
    val fallbackType: String = "customtabs",
    val enableSiteSettingsShortcut: String = true.toString(),
    val orientation: String = "portrait"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TWAManifest

        if (applicationId != other.applicationId) return false
        if (hostName != other.hostName) return false
        if (launchUrl != other.launchUrl) return false
        if (name != other.name) return false
        if (launcherName != other.launcherName) return false
        if (themeColor != other.themeColor) return false
        if (navigationColor != other.navigationColor) return false
        if (navigationColorDark != other.navigationColorDark) return false
        if (navigationDividerColor != other.navigationDividerColor) return false
        if (navigationDividerColorDark != other.navigationDividerColorDark) return false
        if (backgroundColor != other.backgroundColor) return false
        if (enableNotifications != other.enableNotifications) return false
        if (!shortcuts.contentEquals(other.shortcuts)) return false
        if (splashScreenFadeOutDuration != other.splashScreenFadeOutDuration) return false
        if (generatorApp != other.generatorApp) return false
        if (fallbackType != other.fallbackType) return false
        if (enableSiteSettingsShortcut != other.enableSiteSettingsShortcut) return false
        if (orientation != other.orientation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = applicationId.hashCode()
        result = 31 * result + hostName.hashCode()
        result = 31 * result + launchUrl.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + launcherName.hashCode()
        result = 31 * result + themeColor.hashCode()
        result = 31 * result + navigationColor.hashCode()
        result = 31 * result + navigationColorDark.hashCode()
        result = 31 * result + navigationDividerColor.hashCode()
        result = 31 * result + navigationDividerColorDark.hashCode()
        result = 31 * result + backgroundColor.hashCode()
        result = 31 * result + enableNotifications.hashCode()
        result = 31 * result + shortcuts.contentHashCode()
        result = 31 * result + splashScreenFadeOutDuration.hashCode()
        result = 31 * result + generatorApp.hashCode()
        result = 31 * result + fallbackType.hashCode()
        result = 31 * result + enableSiteSettingsShortcut.hashCode()
        result = 31 * result + orientation.hashCode()
        return result
    }
}

val twaManifest: TWAManifest = TWAManifest(
    "app.vercel.easier_qurban_experience.twa",
    "easier-qurban-experience.vercel.app",
    "/?utm_source=pwa",
    "Easier Qurban Experience",
    "EQExp",
    "#2563EB",
    "#2563EB",
    "#0f172ab3",
    "#2563EB",
    "#0f172ab3",
    "#2563EB",
    true,
)

android {
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    defaultConfig {
        applicationId = twaManifest.applicationId
        minSdk = 19
        targetSdk = 35
        versionCode = 1
        versionName = "1"

        // The name for the application
        resValue("string", "appName", twaManifest.name)

        // The name for the application on the Android Launcher
        resValue("string", "launcherName", twaManifest.launcherName)

        // The URL that will be used when launching the TWA from the Android Launcher
        val launchUrl = "https://${twaManifest.hostName}${twaManifest.launchUrl}"
        resValue("string", "launchUrl", launchUrl)


        // The URL the Web Manifest for the Progressive Web App that the TWA points to. This
        // is used by Chrome OS and Meta Quest to open the Web version of the PWA instead of
        // the TWA, as it will probably give a better user experience for non-mobile devices.
        resValue("string", "webManifestUrl", "https://easier-qurban-experience.vercel.app/manifest.json")



        // This is used by Meta Quest.
        resValue("string", "fullScopeUrl", "https://easier-qurban-experience.vercel.app/")




        // The hostname is used when building the intent-filter, so the TWA is able to
        // handle Intents to open host url of the application.
        resValue("string", "hostName", twaManifest.hostName)

        // This attribute sets the status bar color for the TWA. It can be either set here or in
        // `res/values/colors.xml`. Setting in both places is an error and the app will not
        // compile. If not set, the status bar color defaults to #FFFFFF - white.
        resValue("color", "colorPrimary", twaManifest.themeColor)

        // This attribute sets the navigation bar color for the TWA. It can be either set here or
        // in `res/values/colors.xml`. Setting in both places is an error and the app will not
        // compile. If not set, the navigation bar color defaults to #FFFFFF - white.
        resValue("color", "navigationColor", twaManifest.navigationColor)

        // This attribute sets the dark navigation bar color for the TWA. It can be either set here
        // or in `res/values/colors.xml`. Setting in both places is an error and the app will not
        // compile. If not set, the navigation bar color defaults to #000000 - black.
        resValue("color", "navigationColorDark", twaManifest.navigationColorDark)

        // This attribute sets the navbar divider color for the TWA. It can be either
        // set here or in `res/values/colors.xml`. Setting in both places is an error and the app
        // will not compile. If not set, the divider color defaults to #00000000 - transparent.
        resValue("color", "navigationDividerColor", twaManifest.navigationDividerColor)

        // This attribute sets the dark navbar divider color for the TWA. It can be either
        // set here or in `res/values/colors.xml`. Setting in both places is an error and the
        //app will not compile. If not set, the divider color defaults to #000000 - black.
        resValue("color", "navigationDividerColorDark", twaManifest.navigationDividerColorDark)

        // Sets the color for the background used for the splash screen when launching the
        // Trusted Web Activity.
        resValue("color", "backgroundColor", twaManifest.backgroundColor)

        // Defines a provider authority for the Splash Screen
        resValue("string", "providerAuthority", "${twaManifest.applicationId}.fileprovider")

        // The enableNotification resource is used to enable or disable the
        // TrustedWebActivityService, by changing the android:enabled and android:exported
        // attributes
        resValue("bool", "enableNotification", twaManifest.enableNotifications.toString())

        twaManifest.shortcuts.forEachIndexed { shortcut, index ->
            resValue("string", "shortcut_name_$index", "$shortcut.name")
            resValue("string", "shortcut_short_name_$index", "$shortcut.short_name")
        }

        // The splashScreenFadeOutDuration resource is used to set the duration of fade out animation in milliseconds
        // to be played when removing splash screen. The default is 0 (no animation).
        resValue("integer", "splashScreenFadeOutDuration", twaManifest.splashScreenFadeOutDuration.toString())

        resValue("string", "generatorApp", twaManifest.generatorApp)

        resValue("string", "fallbackType", twaManifest.fallbackType)

        resValue("bool", "enableSiteSettingsShortcut", twaManifest.enableSiteSettingsShortcut)
        resValue("string", "orientation", twaManifest.orientation)
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    namespace = twaManifest.applicationId
    lint {
        checkReleaseBuilds = false
    }
}

tasks.register("generateShorcutsFile") {
        assert(twaManifest.shortcuts.size < 5)  { "You can have at most 4 shortcuts." }
        twaManifest.shortcuts.forEachIndexed { i, s ->
            assert(s.name != null) { "Missing `name` in shortcut #${i}"}
            assert(s.shortName != null) { "Missing `short_name` in shortcut #${i}"}
            assert(s.url != null) { "Missing `icon` in shortcut #${i}" }
            assert(s.icon != null) { "Missing `url` in shortcut #${i}" }
        }

        val shortcutsFile = File("${projectDir}/src/main/res/xml/shortcuts.xml")

        var shortcutChildren = ""
        twaManifest.shortcuts.forEachIndexed { i, s ->
            shortcutChildren += generateShortcutListString(s, i, twaManifest)
        }
        val xmlContent = """
            <shortcuts ${if (twaManifest.shortcuts.isNotEmpty()) """ "xmlns:android": "http://schemas.android.com/apk/res/android"  """ else ""}>
            $shortcutChildren
            </shortcuts>
        """.trimIndent()
        println(xmlContent)
        shortcutsFile.writeText(XmlUtil.serialize(xmlContent))
}

repositories {

}

dependencies {
//    implementation fileTree(include: ['*.jar'], dir: 'libs')

    implementation(libs.androidbrowserhelper)
    implementation(libs.core.ktx)
}
