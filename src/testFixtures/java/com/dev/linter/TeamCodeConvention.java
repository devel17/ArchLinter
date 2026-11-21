package com.dev.linter;

import com.tngtech.archunit.ArchConfiguration;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.dev.linter.base.*;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

@AnalyzeClasses
@ArchTag("architecture")
public class TeamCodeConvention {

    private static final String[] CONFIGURATION_RESOURCES = {"archunit-linter.yml", "application-test.yml", "application.yml"};

    static {
        applyArchUnitConfiguration();
    }

    private static void applyArchUnitConfiguration() {
        ArchConfiguration configuration = ArchConfiguration.get();
        for (String resourceName : CONFIGURATION_RESOURCES) {
            ClassPathResource resource = new ClassPathResource(resourceName);
            if (!resource.exists()) {
                continue;
            }
            try {
                YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
                List<PropertySource<?>> sources = loader.load(resourceName, resource);
                for (PropertySource<?> source : sources) {
                    if (!(source instanceof EnumerablePropertySource)) {
                        continue;
                    }
                    for (String name : ((EnumerablePropertySource<?>) source).getPropertyNames()) {
                        String propertyName = mapToArchUnitProperty(name);
                        if (propertyName != null) {
                            configuration.setProperty(propertyName, String.valueOf(source.getProperty(name)));
                        }
                    }
                }
            } catch (IOException ignored) {
                // leave ArchUnit defaults untouched
            }
        }
    }

    private static String mapToArchUnitProperty(String yamlKey) {
        String prefix = "archunit.";
        if (!yamlKey.startsWith(prefix)) {
            return null;
        }
        String propertyName = yamlKey.substring(prefix.length());
        if (propertyName.startsWith("archRule.") || propertyName.startsWith("freeze.")) {
            return propertyName;
        }
        return null;
    }

    @ArchTest
    static final ArchTests annotationRules = ArchTests.in(AnnotationRules.class);

    @ArchTest
    static final ArchTests dependencyRules = ArchTests.in(DependencyRules.class);

    @ArchTest
    static final ArchTests generalRules = ArchTests.in(GeneralRules.class);

    @ArchTest
    static final ArchTests methodRules = ArchTests.in(MethodRules.class);

    @ArchTest
    static final ArchTests namingRules = ArchTests.in(NamingRules.class);

    @ArchTest
    static final ArchTests testRules = ArchTests.in(TestRules.class);

}
