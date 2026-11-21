package com.dev.linter.base;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

@AnalyzeClasses
public class GeneralRules {
//    @ArchTest
//    @ArchIgnore
//    public static final ArchRule services_should_only_access_by_controllers_and_services = classes()
//            .that().resideInAPackage("..service..")
//            .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

//    @ArchTest
//    @ArchIgnore
//    static final ArchRule controllers_should_only_use_their_own_slice =
//            slices().matching("..controller.(*)..").namingSlices("Controller $1")
//                    .as("Controllers").should().notDependOnEachOther();



    @ArchTest
//    @ArchIgnore
    static final ArchRule DEPRECATED_API_SHOULD_NOT_BE_USED = freeze(GeneralCodingRules.DEPRECATED_API_SHOULD_NOT_BE_USED);

//    @ArchTest
//    @ArchIgnore
//    static final ArchRule NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;


}
