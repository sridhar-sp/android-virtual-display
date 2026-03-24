package com.droidstarter.support

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File
import java.io.FileInputStream
import java.util.Properties

class SigningConfigs private constructor(val project: Project) {

    companion object {

        const val DEFAULT_KEYSTORE_SIGNING_CONFIG = "defaultKeystoreSigningConfig"

        private const val KEYSTORE_FILENAME = "keystoreFilename"
        private const val KEYSTORE_PASSWORD = "keystorePassword"
        private const val KEY_ALIAS = "keyAlias"
        private const val KEY_PASSWORD = "keyPassword"

        private const val BUILD_LOGIC_MODULE_NAME = "build-logic"

        fun newInstance(project: Project) = SigningConfigs(project)
    }

    fun applyDefaultKeyStoreSigningConfig() {
        project.extensions.configure<ApplicationExtension> {
            signingConfigs {
                create(DEFAULT_KEYSTORE_SIGNING_CONFIG) {
                    val baseFilePath = File(project.rootDir, BUILD_LOGIC_MODULE_NAME)

                    val localProps = Properties()
                    localProps.load(FileInputStream(File(baseFilePath, "keystore.properties")))

                    if (!isKeystorePropertiesValid(localProps)) {
                        System.err.println("Missing/Invalid keystore.properties")
                        return@create
                    }

                    storeFile = File(baseFilePath, localProps[KEYSTORE_FILENAME] as String)
                    storePassword = localProps[KEYSTORE_PASSWORD] as String
                    keyAlias = localProps[KEY_ALIAS] as String
                    keyPassword = localProps[KEY_PASSWORD] as String

                    enableV2Signing = true
                }
            }
        }
    }

    private fun isKeystorePropertiesValid(properties: Properties): Boolean {
        return properties.containsKey(KEYSTORE_FILENAME) &&
                properties.containsKey(KEYSTORE_PASSWORD) &&
                properties.containsKey(KEY_ALIAS) &&
                properties.containsKey(KEY_PASSWORD)
    }
}