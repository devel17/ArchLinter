package com.dev.linter.base;

import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

public class MethodRules {
    
        @ArchTest
        @ArchIgnore
        ArchRule methodSignateruCountCheck = methods().that().areDeclaredInClassesThat()
                .areAnnotatedWith(Service.class)
                .should(haveLessSignatureParametersThen(5));

        //условие , проверяющее количество параметров в сигнатуре метода
        private ArchCondition<JavaMethod> haveLessSignatureParametersThen(Integer countParams) {
                return new ArchCondition<JavaMethod>("have less then " + countParams +" params") {
                        @Override
                        public void check(JavaMethod method, ConditionEvents events) {
                                boolean lessThen = method.getParameters().size() < countParams;
                                String message = (lessThen ? "" : "not ")
                                        + "all parameters of " + method.getDescription()
                                        + " less then " + countParams;
                                events.add(new SimpleConditionEvent(method, lessThen, message));
                        }
                };
        }

        @ArchTest
        @ArchIgnore
        ArchRule methodCallCountCheck = methods().that().areDeclaredInClassesThat().resideInAPackage("..service..")
                .and().areAnnotatedWith(Service.class)
                .should(haveCallStackTraceLessThen(5));

        //условие , проверяющее  глубину стека вызовов (рекурсия)
        private ArchCondition<JavaMethod> haveCallStackTraceLessThen(Integer callCount) {
                return new ArchCondition<JavaMethod>("have less then " + callCount +" params") {
                        @Override
                        public void check(JavaMethod method, ConditionEvents events) {
                                int maxLevel = 1;
                                for (JavaMethodCall call : method.getCallsOfSelf()) {
                                        maxLevel = checkOneMoreLevel(maxLevel, call.getOrigin());
                                }

                                Boolean lessThen = maxLevel < callCount;
                                String message =  method.getDescription()
                                        + " count call of stacktrace grather then " + callCount + " ("+maxLevel+")";
                                events.add(new SimpleConditionEvent(method, lessThen, message));
                        }

                        public Integer checkOneMoreLevel(Integer level, JavaCodeUnit call) {
                                if (call.getCallsOfSelf().size() > 0) {
                                        level++;
                                        System.out.println(level + ":" +call.getFullName());
                                        for (var call1 : call.getCallsOfSelf()) {
                                                level = checkOneMoreLevel(level, call1.getOrigin());
                                        }
                                }
                                return level;
                        };
                };
        }


        @ArchTest
        ArchRule methodTryCatchCheck = freeze(methods().that()
                .areDeclaredInClassesThat()
                .areAnnotatedWith(Service.class)
                .should(haveTryCatchConstruction()));

        //условие , проверяющее  наличие конструкции try-catch
        private ArchCondition<JavaMethod> haveTryCatchConstruction() {
                return new ArchCondition<JavaMethod>("have try catch construction") {
                        @Override
                        public void check(JavaMethod method, ConditionEvents events) {
                                String message =  method.getDescription()
                                        + " try catch doesn't exist ";
                                events.add(new SimpleConditionEvent(method, method.getTryCatchBlocks().size() > 0, message));
                        }
                };
        }

        //Альтернативная форма записи @ArchTest в виде обычного junit теста
//        @Test
//        public void method_should_start_with_call_log() {
//                JavaClasses importedClasses = new ClassFileImporter()
//                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
//                        .importPackages("com.dev.lkb");
//                ArchRule methodShouldStartWithLoggerRule = methods()
//                        .should(haveStartWithCallLog());
//                methodShouldStartWithLoggerRule.check(importedClasses);
//        }

        @ArchTest
        ArchRule method_should_start_with_call_log = freeze(methods().that()
                .areDeclaredInClassesThat()//.resideInAPackage("..service..")
                .areAnnotatedWith(Service.class)
                .should(haveStartWithCallLog()));


        //проверяет, начинается ли метод с вызова логера
        private ArchCondition<JavaMethod> haveStartWithCallLog() {
                return new ArchCondition<JavaMethod>(" start with log.info()/debug()/trace()") {
                        @Override
                        public void check(JavaMethod method, ConditionEvents events) {
                                Boolean isValid = method.getMethodCallsFromSelf()//список всех вызовов в методе
                                        .stream()
                                        .filter(it -> List.of("info","debug","trace").contains(it.getTarget().getName()))//вызов в таргете содержит название метода из списка
                                        .filter(it -> it.getLineNumber() == method.getSourceCodeLocation().getLineNumber())//строка вызова совпадает с первой строкой метода
                                        .findFirst()
                                        .isPresent();
//                                }
                                String message = "Method " + method.getFullName() + " should start with log.info()";
                                events.add(new SimpleConditionEvent(method, isValid, message));
                        }
                };
        }


//        @Test
//        public void method_should_call_log_error_only_catch_block() {
//                JavaClasses importedClasses = new ClassFileImporter()
//                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
//                        .importPackages("com.dev.lkb.service");
//                ArchRule methodSignatureCountRule = methods()
////                        .that().areDeclaredInClassesThat().haveSimpleName("VicariousClientKonturAdapter")//для тестирования конкретного класса
//                        .should(haveCallLogErrorInCatch());
//                methodSignatureCountRule.check(importedClasses);
//        }
        @ArchTest
        ArchRule method_should_call_log_error_only_catch_block = freeze(methods()
                .that().areDeclaredInClassesThat()//.resideInAPackage("..com.dev.lkb.service..")
                .areAnnotatedWith(Service.class)
                .should(haveCallLogErrorInCatch()));


        //проверяет, что вызовы log.error() находятся в catch секциях
        private ArchCondition<JavaMethod> haveCallLogErrorInCatch() {
                return new ArchCondition<JavaMethod>(" call log.error() in catch section ") {
                        @Override
                        public void check(JavaMethod method, ConditionEvents events) {
                                try {
                                        //                                if (!method.getName().equals("downloadFiles")) return;//для тестирования конкретного метода
                                        //Список всех вызовов log.error() отсортированны в порядке расположения в коде
                                        var listErrorCalls = method.getMethodCallsFromSelf()
                                                .stream()
                                                .filter(it -> List.of("error").contains(it.getTarget().getName()))//вызов в таргете содержит название метода из списка;
                                                .sorted(Comparator.comparing(JavaMethodCall::getLineNumber).reversed())
                                                .collect(Collectors.toList());

                                        //map всех вызовов во всех блоках try сортируем по ключам (в порядке расположения в коде)
                                        TreeMap<Integer, Set<JavaAccess<?>>> sortedMapTryCatchesBlocksCalls = method.getTryCatchBlocks()
                                                .stream()
                                                .collect(Collectors.toMap(block -> block.getSourceCodeLocation().getLineNumber(), block -> block.getAccessesContainedInTryBlock(), (oldValue,newValue) -> newValue, TreeMap::new));

                                        Boolean isValid;
                                        if (listErrorCalls.size() > 0 && sortedMapTryCatchesBlocksCalls.size() == 0) {
                                                isValid = false;//log.error есть , try/catch нет
                                        } else {
                                                //Трансформация результирующего Map сгруппированных по вызовам log.error()
                                                Map<JavaMethodCall, Set<JavaAccess<?>>> rezMap = IntStream.range(0, listErrorCalls.size())
                                                        .boxed()
                                                        .collect(Collectors.toMap(
                                                                index -> listErrorCalls.get(index),
                                                                index -> sortedMapTryCatchesBlocksCalls.values().stream().toList()
                                                                        .get(Math.min(index, sortedMapTryCatchesBlocksCalls.size()-1))));//если вызовов log.error() меньше чем конструкций try/catch

                                                isValid = rezMap.entrySet().stream().noneMatch( it -> it.getValue().contains(it.getKey()));//не должно быть вхождений log.error внутри своей секции try
                                        }


//************************************************************************************************************************************************************************************************************
//                                Если в методе будет сторого один try catch то достаточно реализации ниже, все что выше можно удалить
//
//                                Boolean isValid = method.getMethodCallsFromSelf()//список всех вызовов в методе
//                                        .stream()
//                                        .filter(it -> List.of("error").contains(it.getTarget().getName()))//вызов в таргете содержит название метода из списка
//                                        .collect(Collectors.toList())
//                                        .stream()
//                                        .noneMatch(it -> method.getTryCatchBlocks()//не должно быть совпадений с вызовами, находящимися в try блоке
//                                                .stream()
//                                                .flatMap(it1 -> it1.getAccessesContainedInTryBlock().stream())
//                                                .collect(Collectors.toList())
//                                                .contains(it));
                                        String message = "Method " + method.getFullName() + " should call log.error() in catch section";
                                        events.add(new SimpleConditionEvent(method, isValid, message));
                                } catch(Exception ex) {
                                        System.err.println("Error in : " + method.getFullName() + " : " + ex);
                                }

                        }
                };
        }

    
}
