# Образ с тестами: JDK 17 + Node.js + Appium + UiAutomator2 + Android platform-tools (adb).
# Эмулятор в образ не входит: тесты подключаются к устройству по сети через adb (см. docker-compose.yml).
FROM eclipse-temurin:17-jdk-jammy

ARG NODE_MAJOR=22
ARG APPIUM_VERSION=2
# Последний uiautomator2-драйвер требует Appium 3; для Appium 2 фиксируем ветку 4.x
ARG UIAUTOMATOR2_VERSION=4.2.8
ARG ANDROID_CMDLINE_TOOLS_VERSION=11076708
ARG ANDROID_BUILD_TOOLS_VERSION=34.0.0

ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    GRADLE_USER_HOME=/opt/gradle-home \
    ENV=ci
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl unzip ca-certificates gnupg \
    && curl -fsSL "https://deb.nodesource.com/setup_${NODE_MAJOR}.x" | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p "${ANDROID_HOME}/cmdline-tools" \
    && curl -fsSL -o /tmp/cmdline-tools.zip \
       "https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMDLINE_TOOLS_VERSION}_latest.zip" \
    && unzip -q /tmp/cmdline-tools.zip -d "${ANDROID_HOME}/cmdline-tools" \
    && mv "${ANDROID_HOME}/cmdline-tools/cmdline-tools" "${ANDROID_HOME}/cmdline-tools/latest" \
    && rm /tmp/cmdline-tools.zip \
    && yes | sdkmanager --licenses > /dev/null \
    && sdkmanager "platform-tools" "build-tools;${ANDROID_BUILD_TOOLS_VERSION}"

RUN npm install -g "appium@${APPIUM_VERSION}" \
    && appium driver install "uiautomator2@${UIAUTOMATOR2_VERSION}"

WORKDIR /app

# Сначала только файлы сборки: слой с зависимостями кешируется, пока не меняется build.gradle
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

COPY config ./config
COPY src ./src
RUN ./gradlew testClasses checkstyleTest --no-daemon

COPY docker/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["./gradlew", "test", "--no-daemon"]
