// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("detekt/config.yml"))
    source.setFrom(
        files(
            "app/src/main/java",
            "app/src/test/java",
            "domain/src/main/java",
            "domain/src/test/java",
            "data/src/main/java",
            "data/src/test/java",
            "core/ui/src/main/java",
        ),
    )
}

ktlint {
    android.set(true)
    outputToConsole.set(true)
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}
