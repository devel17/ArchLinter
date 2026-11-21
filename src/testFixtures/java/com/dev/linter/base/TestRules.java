package com.dev.linter.base;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

public class TestRules {

    @ArchTest
    static final ArchRule testClassesShouldResideInTheSamePackageAsImplementation = freeze(GeneralCodingRules.testClassesShouldResideInTheSamePackageAsImplementation());

    @ArchTest
    static final ArchRule shouldNotUseJunit4Dependency = freeze(noClasses()
            .should().accessClassesThat().resideInAnyPackage("org.junit")
            .because("Tests should use Junit5 instead of Junit4"));

    @ArchTest
    static final ArchRule shouldNotUseJunit4Annotation = freeze(noMethods().should().beAnnotatedWith("org.junit.Test")
            .orShould().beAnnotatedWith("org.junit.Ignore")
            .because("Tests should use Junit5 instead of Junit4"));
}
