package com.dev.linter.base;

import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;
//import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

public class NamingRules {

//    static ArchRule services_should_be_prefixed =
//            classes()
//                    .that().resideInAPackage("..service..")
//                    .and().areAnnotatedWith(Service.class)
//                    .should().haveSimpleNameEndingWith("Service");

    @ArchTest
    static ArchRule controllers_should_be_suffixed =
            freeze(
                    classes()
                    .that().resideInAPackage("..controller..")
                    .and().areAnnotatedWith(RestController.class)
                    //.or().areAssignableTo(AbstractController.class)
                    .should().haveSimpleNameEndingWith("Controller")
            )
    ;
}
