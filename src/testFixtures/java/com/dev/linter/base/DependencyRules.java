package com.dev.linter.base;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

public class DependencyRules {

    @ArchTest
    public static final ArchRule NO_CONCURRENT_DEPENDENCIES =
            freeze(
            noClasses()
            .should().dependOnClassesThat().resideInAPackage("..java.util.concurrent..")
            )
    ;

    @ArchTest
    public static final ArchRule NO_CYCLES_DEPENDENCIES = freeze(slices().matching("..(*)..").should().beFreeOfCycles());

    
//    @Test
//    public void classes_dependency_diff_package_check() {
//            JavaClasses importedClasses = new ClassFileImporter()
//                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
//                    .importPackages("com.dev.lkb.service");
//            ArchRule methodSignatureCountRule = classes().that().resideInAPackage("..service..").should(onlyHaveDifferentDependencies());
//            methodSignatureCountRule.check(importedClasses);
//    }
    @ArchTest
    @ArchIgnore
    public  final ArchRule diffrentImportCount = classes().that().resideInAPackage("..*..").should(onlyHaveDifferentDependencies());

    //Различные импорты в классе (различные пакеты / различные классы)
    ArchCondition<JavaClass> onlyHaveDifferentDependencies() {
            return new ArchCondition<JavaClass>("only have different dependencies") {
                    @Override
                    public void check(JavaClass javaClass, ConditionEvents events) {
                            javaClass.getDirectDependenciesFromSelf().stream()
                                    .forEach(System.out::println);
                            //реализацию нужных проверок на количество классов или различных пакетов
//                                        .filter(d -> d.getTargetClass().getSimpleName().equals(allowedType.getSimpleName()))
//                                        .filter(d -> !d.getTargetClass().isEquivalentTo(allowedType))
//                                        .forEach(d -> events.add(SimpleConditionEvent.violated(d, d.getDescription())));
                    }
            };
    }


}
