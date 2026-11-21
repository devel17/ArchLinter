package com.dev.linter.base;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

@AnalyzeClasses
public class AnnotationRules {

    @ArchTest
    public static final ArchRule autowired_should_be_injected_only_constructor = freeze(noFields()
            .should().beAnnotatedWith(Autowired.class));

    @Test
    @Disabled
    public void autowired_should_only_be_injected_via_constructor() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.dev.lkb");
        ArchRule myRule = noFields().should().beAnnotatedWith(Autowired.class);
        myRule.check(importedClasses);
    }

//    @ArchTest
//    @ArchIgnore
//    static final ArchRule NO_CLASSES_SHOULD_USE_FIELD_INJECTION = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    public static final ArchRule services_should_implement_interfaces =
            freeze(
                    classes().that().areAnnotatedWith(Service.class)//resideInAPackage("..service..")
                            .should().implement(JavaClass.Predicates.INTERFACES)
            )
            ;
}
