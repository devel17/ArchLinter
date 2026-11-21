# Linter

Linter — библиотека архитектурных правил на базе [ArchUnit](https://www.archunit.org/), подключается к каждому микросервису и выполняется как обычные JUnit-тесты.

- Артефакт: `com.dev.infra:arch-linter` (тест-фикстуры, `testFixtures`)
- Версия: `0.0.1`
- ArchUnit: `1.5.1` (поддержка Java 25)

## Подключение к сервису

**1. Зависимость** в `build.gradle`:

```gradle
testImplementation(testFixtures("com.dev.infra:arch-linter:0.0.1"))
```

**2. Наследник** в `src/test/java` (например, `ApplicationTest`):

```java
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeJars;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.dev.linter.TeamCodeConvention;

@AnalyzeClasses(packagesOf = Application.class, importOptions = {DoNotIncludeJars.class})
public class ApplicationTest extends TeamCodeConvention {
}
```

> Важно: `@AnalyzeClasses` **обязателен** на наследнике — аннотация не наследуется, без неё правила молча не запускаются.

**3. Конфигурация** в `src/test/resources/application-test.yml` (файл `archunit.properties` не нужен):

```yaml
archunit:
  archRule:
    failOnEmptyShould: false
  freeze:
    store:
      default:
        allowStoreCreation: true
        path: "src/test/resources/linter-frozen"
```

Ключи с префиксом `archunit.` из `application-test.yml` (и `application.yml`) применяются к ArchUnit при загрузке теста. Приоритет: `archunit-linter.yml` → `application-test.yml` → `application.yml`.

**4. JUnit Platform** в `build.gradle`:

```gradle
tasks.named('test') {
    useJUnitPlatform()
}
```

## Теги

Правила помечены `@ArchTag("architecture")` — в CI их можно гонять отдельно:

```gradle
tasks.register('archTest', Test) {
    useJUnitPlatform { includeTags 'architecture' }
}
```

## Легаси (freeze)

При первом запуске все текущие нарушения записываются в freeze-store (`linter-frozen`) и перестают валить сборку. Новые нарушения — валят. Файл `stored.rules` коммитится в репозиторий сервиса.

## Собрать

```bash
./gradlew build
```

## Опубликовать

```bash
./gradlew publish            # в Nexus (нужны NEXUS_URL/USER_PASSWORD)
./gradlew publishToMavenLocal
```

## Список правил

0.0.1
- `@Autowired` внедряется только через конструктор
- Каждый сервис (`@Service`) должен реализовывать интерфейс
- Запрещено использовать многопоточность (пакет `..java.util.concurrent..`)
- Запрещены циклические зависимости
- Запрещено использовать `@Deprecated` сущности
- `@RestController` должен заканчиваться на `Controller`
- Тесты должны размещаться в тех же пакетах, что и реализации
- Запрещены аннотации и классы JUnit4
- Каждый метод сервиса (`@Service`) должен содержать хотя бы один try/catch
- Каждый метод сервиса должен начинаться с `log.info()/trace()/debug()` (либо первая строка, либо строка за try)
- Каждый вызов `log.error()` сервиса должен быть в секции catch

## Документация ArchUnit

- [Официальный сайт](https://www.archunit.org/userguide/html/000_Index.html#_importing_classes)
- [Примеры](https://github.com/TNG/ArchUnit-Examples/tree/main/example-junit5/src/test/java/com/tngtech/archunit/exampletest/junit5)
