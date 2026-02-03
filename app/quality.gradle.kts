plugins {
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.owasp.dependencycheck")
}

val qualityDir = "$rootDir/quality"
val reportsDir = "$buildDir/reports"

// Detekt設定
detekt {
    toolVersion = "1.23.1"
    config.setFrom("$qualityDir/detekt.yml")
    buildUponDefaultConfig = true
    allRules = false
    disableDefaultRuleSets = false
    debug = false
    parallel = true
    
    reports {
        xml {
            required.set(true)
            outputLocation.set(file("$reportsDir/detekt/detekt.xml"))
        }
        html {
            required.set(true)
            outputLocation.set(file("$reportsDir/detekt/detekt.html"))
        }
        txt {
            required.set(true)
            outputLocation.set(file("$reportsDir/detekt/detekt.txt"))
        }
        sarif {
            required.set(true)
            outputLocation.set(file("$reportsDir/detekt/detekt.sarif"))
        }
    }
}

tasks.named("detekt").configure {
    exclude("**/test/**", "**/androidTest/**", "**/build/**")
}

// ktlint設定
ktlint {
    version.set("0.50.0")
    verbose.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
    
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}

// OWASP Dependency Check設定
dependencyCheck {
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL
    autoUpdate = true
    cveValidForHours = 24
    failBuildOnCVSS = 7.0f
    suppressionFile = "$qualityDir/suppressions.xml"
    analyzers {
        assemblyEnabled = false
        nodeEnabled = false
        nodeAuditEnabled = false
        nodePackageEnabled = false
        nugetconfEnabled = false
        nugetEnabled = false
        bundleAuditEnabled = false
        cmakeEnabled = false
        autoconfEnabled = false
        composerEnabled = false
        swiftEnabled = false
        cocoapodsEnabled = false
        pythonEnabled = false
        rubygemsEnabled = false
    }
    scanConfigurations = listOf("runtimeClasspath")
}

tasks.named("dependencyCheckAnalyze").configure {
    reports {
        xml.outputLocation.set(file("$reportsDir/dependency-check/dependency-check-report.xml"))
        html.outputLocation.set(file("$reportsDir/dependency-check/dependency-check-report.html"))
        junit.outputLocation.set(file("$reportsDir/dependency-check/dependency-check-report.junit"))
    }
}

// 静的解析タスクのグループ化
tasks.register("qualityCheck") {
    group = "verification"
    description = "Run all quality checks"
    dependsOn(
        tasks.named("ktlintCheck"),
        tasks.named("detekt"),
        tasks.named("dependencyCheckAnalyze")
    )
}

// フォーマットタスク
tasks.register("format") {
    group = "formatting"
    description = "Format Kotlin files"
    dependsOn(tasks.named("ktlintFormat"))
}

// コードカバレッジ設定
subprojects {
    apply(plugin = "jacoco")
    
    jacoco {
        toolVersion = "0.8.10"
    }
    
    tasks.register<JacocoReport>("jacocoTestReport") {
        dependsOn("testDebugUnitTest")
        
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
        
        val fileFilter = listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/models/*.*",
            "**/di/*.*"
        )
        
        val debugTree = fileTree("${buildDir}/intermediates/javac/debug") {
            exclude(fileFilter)
        }
        
        val mainSrc = "${project.projectDir}/src/main/kotlin"
        
        sourceDirectories.setFrom(files(mainSrc))
        classDirectories.setFrom(files(debugTree))
        executionData.setFrom(files("${buildDir}/jacoco/testDebugUnitTest.exec"))
    }
}