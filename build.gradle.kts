fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.11.0"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.3.1"
}

// Import variables from gradle.properties file
val pluginGroup: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
//val pluginUntilBuild: String by project
val pluginVerifierIdeVersions: String by project

val forgeVersion: String by project

val platformType: String by project
val platformVersion: String by project
val platformPlugins: String by project
val platformDownloadSources: String by project

val addons: Configuration by configurations.creating
val addonsDir by extra("addon-repository")

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("org.jboss.forge:forge-bom:${forgeVersion}"))
    implementation("com.miglayout:miglayout-swing:4.2")
    implementation("org.jboss.logmanager:jboss-logmanager:1.4.1.Final")
    implementation("org.jboss.forge.furnace:furnace-se")
    implementation("org.jboss.forge.addon:facets-api")
    implementation("org.jboss.forge.addon:resources-api") {
        exclude("org.jboss.forge.addon", "facets-impl")
    }
    implementation("org.jboss.forge.addon:environment-api")
    implementation("org.jboss.forge.addon:convert-api")
    implementation("org.jboss.forge.addon:projects-api")
    implementation("org.jboss.forge.addon:ui-api") {
        exclude("org.jboss.forge.addon", "facets-impl")
        exclude("org.jboss.forge.addon", "environment-impl")
        exclude("org.jboss.forge.addon", "facets")
        exclude("org.jboss.forge.addon", "environment")
    }

    // Contains the core addons Forge needs
    addons("org.jboss.forge", "forge-distribution", forgeVersion, null, "offline", "zip")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(false)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

tasks {
    val extractAddons by creating(Sync::class) {
        dependsOn(addons)
        // Unzip only the addons directory
        from(zipTree(addons.asPath)) {
            include("forge-distribution-${forgeVersion}/addons/**")
            eachFile {
                relativePath = RelativePath(true, *relativePath.segments.drop(2).toTypedArray())
            }
            includeEmptyDirs = false
        }
        into("$buildDir/$addonsDir")
    }

    // Set the compatibility versions to 11
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    buildPlugin {
        // Generate the addon-repository when the JAR is built
        dependsOn("extractAddons")
        from("$buildDir/$addonsDir"){
            into(addonsDir)
        }
    }

    // Uncomment this if you're running "gradle runIde"
//    prepareSandbox {
//        dependsOn("extractAddons")
//        from("$buildDir/$addonsDir"){
//            into("${pluginName.get()}/$addonsDir")
//        }
//    }

//    buildSearchableOptions {
//        enabled = false
//    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
//        untilBuild.set(properties("pluginUntilBuild"))
        // Get the latest available change notes from the changelog file
        changeNotes.set(provider { changelog.getLatest().toHTML() })
    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://jetbrains.org/intellij/sdk/docs/tutorials/build_system/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}
