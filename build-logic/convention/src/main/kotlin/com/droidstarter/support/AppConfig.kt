package com.droidstarter.support

import org.gradle.api.JavaVersion
import java.math.BigDecimal

object AppConfig {
    const val compileSdk = 37
    const val minSdk = 26
    const val targetSdk = 35

    val sourceCompatibility = JavaVersion.VERSION_17
    val targetCompatibility = JavaVersion.VERSION_17

    val minimumCoverageLimit = BigDecimal("0.7")
}