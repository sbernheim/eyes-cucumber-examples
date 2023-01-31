package com.applitools.eyes.selenium.settings;

import java.util.Optional;

import com.applitools.eyes.AccessibilityGuidelinesVersion;
import com.applitools.eyes.AccessibilityLevel;
import com.applitools.eyes.AccessibilitySettings;
import com.applitools.eyes.selenium.settings.Setting.StringSetting;

public class Settings {
    public static Settings SETTINGS_SINGLETON;
    
    public boolean defaultHeadless = true;
    public long defaultImplicitWaitSeconds = 10;

    public boolean headless;
    public long implicitWaitSeconds;
    public StringSetting applitoolsApiKey;
    public StringSetting serverUrl;
    public boolean renderOnUltraFastGrid;
    public boolean applitoolsApiDisabled;
    public boolean isAccessibilityValidationEnabled;
    public int concurrency;
    public AccessibilityLevel accessibilityLevel;
    public AccessibilityGuidelinesVersion accessibilityGuidelinesVersion;
    public StringSetting batchId;
    public StringSetting batchName;
    public StringSetting branchName;
    public StringSetting baselineEnvironment;
    public StringSetting mobileBaselineEnvironment;

    public Settings() {

        // Read the Applitools API key from a System property or environment variable.
        // To find your Applitools API key:
        // https://applitools.com/tutorials/getting-started/setting-up-your-environment.html
        applitoolsApiKey = Setting.fromProperty("applitools.api.key")
                .orEnv("APPLITOOLS_API_KEY");


        boolean checkApplitoolsApiKey = Setting.fromProperty("applitools.api.key.required")
                .orEnv("APPLITOOLS_API_KEY_REQUIRED")
                .asBoolean()
                .orDefault(true);

        if (checkApplitoolsApiKey) {
            applitoolsApiKey.notBlankOrThrow("The APPLITOOLS_API_KEY environment variable is not set!");
        }
        
        // The Applitools Eyes server URL if you're NOT using https://eyes.applitools.com
        // If you want to hard-code the server URL for your use-case and ignore the
        // the environment variable and system property, use this next line instead.
        //serverUrl = Setting.of("yourcompanyeyes.applitools.com");
        serverUrl = Setting.fromProperty("applitools.server.url")
                .orEnv("APPLITOOLS_SERVER_URL");
        
        // Read the headless mode setting from a system property or use the default
        // Use headless mode for Continuous Integration (CI) execution.
        // Use headed mode for local development.
        headless = Setting.fromProperty("webdriver.chrome.headless")
                .orEnv("WEBDRIVER_HEADLESS")
                .asBoolean()
                .orDefault(defaultHeadless);
        
        // Get WebdriverIO's implicitWaitSeconds setting from a system property or use the default
        implicitWaitSeconds = Setting.fromProperty("webdriver.implicitWaitSeconds")
                .orEnv("WEBDRIVER_IMPLICIT_WAIT_SECONDS")
                .asLong()
                .orDefault(defaultImplicitWaitSeconds);

        applitoolsApiDisabled = Setting.fromProperty("applitools.api.skip.checks")
                .orEnv("APPLITOOLS_API_SKIP_CHECKS")
                .asBoolean()
                .orDefault(false);

        isAccessibilityValidationEnabled = Setting.fromProperty("applitools.api.accessibility.validation.enabled")
                .orEnv("APPLITOOLS_ACCESSIBILITY_VALIDATION_ENABLED")
                .asBoolean()
                .orDefault(false);

        accessibilityLevel = Setting.fromProperty("applitools.api.accessibility.level")
                    .orEnv("APPLITOOLS_ACCESSIBILITY_LEVEL")
                    .map((l) -> AccessibilityLevel.valueOf(l))
                    .orDefault(AccessibilityLevel.AA);
        
        accessibilityGuidelinesVersion = Setting.fromProperty("applitools.api.accessibility.guidelines.version")
                    .orEnv("APPLITOOLS_ACCESSIBILITY_GUIDELINES_VERSION")
                    .map((g) -> AccessibilityGuidelinesVersion.valueOf(g))
                    .orDefault(AccessibilityGuidelinesVersion.WCAG_2_0);
        
        // Use the batchId env var set by the Applitools Jenkins plug-in if defined,
        batchId = Setting.fromProperty("applitools.api.batch.id").orEnv("APPLITOOLS_BATCH_ID");

        // Use the APPLITOOLS_BRANCH_NAME env var set by Applitools Jenkins CI,
        // GitHub, GitLab, and/or Bitbucket integrations if defined.
        // Use the APPLITOOLS_BRANCH_NAME env var set by Applitools Jenkins CI,
        branchName = Setting.fromProperty("applitools.api.branch").orEnv("APPLITOOLS_BRANCH_NAME");
        
        // Use the APPLITOOLS_BATCH_NAME set by the Applitools Jenkins plug-in if defined,
        batchName = Setting.fromProperty("applitools.api.batch.name").orEnv("APPLITOOLS_BATCH_NAME");
                //.orGet(this::suiteBatchName)
                //.orDefault(defaultBatchName);

        baselineEnvironment = Setting.fromProperty("applitools.api.baseline.environment").orEnv("APPLITOOLS_BASELINE_ENVIRONMENT");

        mobileBaselineEnvironment = Setting.fromProperty("applitools.api.baseline.environment.mobile").orEnv("APPLITOOLS_MOBILE_BASELINE_ENVIRONMENT");

        renderOnUltraFastGrid = Setting.fromProperty("applitools.ultra.fast.grid")
                .orEnv("APPLITOOLS_ULTRA_FAST_GRID")
                .asBoolean()
                .orDefault(false);

        concurrency = Setting.fromProperty("applitools.concurrency")
                .orEnv("APPLITOOLS_CONCURRENCY")
                .asInteger()
                .orDefault(5);
    }
    
    public Optional<AccessibilitySettings> accessibility() {
        AccessibilitySettings accessibilitySettings = 
                isAccessibilityValidationEnabled ? 
                        new AccessibilitySettings(accessibilityLevel, accessibilityGuidelinesVersion) :
                        null;
        return Optional.ofNullable(accessibilitySettings);
    }
    
    /**
     * Initializes and returns a Settings Singleton object.
     * 
     * @return Settings
     */
    public static Settings readSettings() {
        if (SETTINGS_SINGLETON == null) {
            SETTINGS_SINGLETON = new Settings();
        }
        return SETTINGS_SINGLETON;
    }

}