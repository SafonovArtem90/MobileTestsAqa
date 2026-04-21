# Фреймворк для автоматизации тестирования нативных Android-приложений.

## 🛠 Технологический стек
* Java 17
* Appium 2.x (UiAutomator2 Driver)
* JUnit 5
* Gradle
* Lombok, AssertJ, Allure Reporting

## 📋 Требования
JDK 17 (переменная JAVA_HOME должна быть настроена).
Node.js и Appium 2.x: 
npm install -g appium
appium driver install uiautomator2.
Android Studio (установленные SDK, Build Tools, Emulators).
Appium Inspector (опционально, для поиска локаторов).

## 🚀 Запуск тестов
### Через терминал (Gradle):
Очистка и запуск всех тестов./gradlew clean test
### Из IDE:
Запустите класс LoginTest или конкретный тестовый класс через IntelliJ IDEA.

## 📊 Отчеты (Allure)
### Команда для открытия отчета в браузере
./gradlew allureServe

## 📁 Структура проекта
src/test/java/screens — Page Objects (описание экранов).
src/test/java/managers — Управление драйвером, сервером и пулом устройств.
src/test/java/utils — Утилиты (Waiters, Actions, Regex).
src/test/java/android — Тесты (Test Layer).
src/test/resources — Конфигурационные файлы.