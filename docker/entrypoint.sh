#!/usr/bin/env bash
# Подключает устройства по сети (ADB_CONNECT=host:port,host:port), ждёт их загрузки и запускает команду.
set -euo pipefail

BOOT_TIMEOUT="${DEVICE_BOOT_TIMEOUT:-600}"
POLL_INTERVAL=5

wait_for_device() {
  local target="$1"
  local deadline=$((SECONDS + BOOT_TIMEOUT))
  echo "Ожидание устройства ${target} (таймаут ${BOOT_TIMEOUT} с)"
  while (( SECONDS < deadline )); do
    adb connect "${target}" > /dev/null 2>&1 || true
    if [[ "$(adb -s "${target}" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; then
      echo "Устройство ${target} готово"
      return 0
    fi
    sleep "${POLL_INTERVAL}"
  done
  echo "Устройство ${target} не загрузилось за ${BOOT_TIMEOUT} с" >&2
  adb devices -l >&2
  return 1
}

adb start-server > /dev/null

if [[ -n "${ADB_CONNECT:-}" ]]; then
  IFS=',' read -ra targets <<< "${ADB_CONNECT}"
  for target in "${targets[@]}"; do
    wait_for_device "$(echo "${target}" | xargs)"
  done
fi

adb devices -l
exec "$@"
