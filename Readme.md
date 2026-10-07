# Фреймворк для автоматизации тестирования нативных Android-приложений

## 🛠 Технологический стек
* Java 17, Gradle 9
* Appium 2.x (UiAutomator2 Driver)
* JUnit 5 (параллельный запуск по пулу устройств)
* Lombok, AssertJ, Allure
* Docker, GitHub Actions

## 📋 Требования для локального запуска
* JDK 17 (`JAVA_HOME`).
* Node.js и Appium 2.x: `npm install -g appium@2` и `appium driver install uiautomator2`.
* Android SDK (platform-tools, build-tools), запущенный эмулятор или подключённое устройство.
* Appium Inspector (по желанию, для поиска локаторов).

## ⚙️ Настройки
Порядок приоритета (сверху вниз):

| Источник | Пример |
|---|---|
| Параметр JVM | `./gradlew test -Ddevices.udids=emulator-5554` |
| Переменная окружения | `DEVICES_UDIDS=emulator-5554` |
| `config-{ENV}.properties` | `ENV=local` (по умолчанию) или `ENV=ci` |
| `config.properties` | значения по умолчанию |

Имя переменной окружения получается из ключа: `app.path` → `APP_PATH`, `test.user.login` → `TEST_USER_LOGIN`.

Локальные настройки и секреты: скопируйте `src/test/resources/config-local.properties.example`
в `config-local.properties` (файл в `.gitignore`).

| Переменная | Назначение |
|---|---|
| `ENV` | окружение: `local`, `ci` |
| `DEVICES_UDIDS` | список устройств через запятую |
| `THREADS` | число потоков (по умолчанию — по числу `DEVICES_UDIDS` или 1) |
| `TEST_USER_LOGIN`, `TEST_USER_PASSWORD` | учётные данные |
| `APP_PATH` | абсолютный путь к APK |
| `INCLUDE_TAGS`, `EXCLUDE_TAGS` | фильтр тестов по тегам |

## 📱 Тестируемое приложение
Перед каждым тестом:
1. Если приложение `app.package` уже установлено — используется оно.
2. Иначе ставится APK из `APP_PATH` (или `-Dapp.path`).
3. Иначе — из ресурсов проекта: `src/test/resources/apps/app-debug.apk` (ключ `app.resource`).
4. Данные приложения очищаются (`app.resetBeforeTest=true`), приложение запускается.

## 🚀 Запуск
```bash
# Все тесты на двух эмуляторах
./gradlew clean test -Pthreads=2

# Только тесты с тегом
./gradlew test -PincludeTags=ANDROID

# Отчёт Allure
./gradlew allureServe
```

## 🐳 Запуск в Docker
Нужен Linux с KVM (на macOS эмулятор в Docker не работает).
```bash
export TEST_USER_LOGIN=... TEST_USER_PASSWORD=...
docker compose up --build --exit-code-from tests --abort-on-container-exit
```
* Экран эмулятора: http://localhost:6080
* Результаты: `build/allure-results`, отчёт: `./gradlew allureReport`.
* Внешнее устройство вместо эмулятора: `ADB_CONNECT=host:5555` и `DEVICES_UDIDS=host:5555`.

## ☁️ GitHub Actions
Workflow: `.github/workflows/android-ui-tests.yml`. Запускается на pull request или вручную
(Actions → Android UI tests → Run workflow).

Секреты репозитория (Settings → Secrets and variables → Actions):
* `TEST_USER_LOGIN`, `TEST_USER_PASSWORD` — обязательно;
* `APP_DOWNLOAD_URL` — по желанию, если APK не хранится в репозитории.

Отчёт Allure сохраняется в артефакте `allure-report`.

## 📁 Структура проекта
* `src/test/java/config` — чтение настроек (`ConfigLoader`, `TestConfig`).
* `src/test/java/managers` — Appium Server, драйвер, пул устройств, подготовка приложения (`app`).
* `src/test/java/base` — базовые классы тестов и экранов.
* `src/test/java/screens` — Page Object (описание экранов).
* `src/test/java/utils` — ожидания, действия, проверки.
* `src/test/java/extensions` — расширения JUnit (Appium Server, вложения при падении).
* `src/test/java/android` — тесты.
* `src/test/resources` — настройки и APK (`apps/`).
* `docker`, `Dockerfile`, `docker-compose.yml` — запуск в контейнерах.
