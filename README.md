# Домашнее задание №2: тестирование REST и GraphQL API

## Запуск

Нужны JDK 17, Maven 3.6.3 или новее.
Из корня проекта выполнить:

```bash
mvn test
```

## Запуск в Docker - домашнее задание №3

Нужен запущенный Docker Engine (на macOS - Docker Desktop) и доступ к интернету.

Из корня проекта собрать образ:

```bash
docker build -t api-test .
```

Во время сборки устанавливаются зависимости и компилируются тесты.
HTTP-запросы к API выполняются при запуске контейнера:

```bash
docker run --rm api-test
echo $?
```

Контейнер автоматически запускает `mvn -B -ntp test`. Ожидаемый результат -
семь успешных тестов, `BUILD SUCCESS` и код завершения `0`. Результаты тестов выводятся в консоль; после завершения контейнер удаляется.
Файл `.dockerignore` исключает из контекста сборки локальную папку `target`, служебные файлы и материалы ручных проверок.

Для проверки ненулевого кода завершения запустить существующий тест успешного
входа с заведомо неверным паролем:

```bash
docker run --rm api-test mvn -B -ntp test \
  '-Dtest=AuthTest#shouldAccessProfileWithReceivedToken' \
  -Dapi.password=definitely-wrong-password
echo $?
```

В этом случае ожидаются падение проверки HTTP 200, `BUILD FAILURE` и ненулевой код завершения

## GitHub Actions

Конфигурация CI находится в [.github/workflows/ci.yml](.github/workflows/ci.yml).
Workflow `Docker API tests` автоматически запускается при `push` в любую ветку

В одной задаче на Ubuntu выполняются три шага:

1. Получение исходников текущего коммита через `actions/checkout`.
2. Сборка образа командой `docker build -t api-test .`.
3. Запуск этого же образа командой `docker run --rm api-test`.

Ошибка сборки или ненулевой код завершения тестов делают задачу и весь
workflow неуспешными. Если все семь тестов проходят, workflow завершается
успешно.

Результаты запусков доступны на вкладке
[Actions](https://github.com/ntlad/api-tests/actions) репозитория.

## Используемые API

- REST: <https://playground.nileslabs.com/api/v1>
- GraphQL: <https://graphql.postman-echo.com/graphql>
- Данные для входа: `Bret / Password@123`

## Автотесты

Реализованы семь автотестов на JUnit 5 и RestAssured:

1. **REST-сценарий:** получение ID автора, `POST /posts` -> извлечение ID поста ->
   `PATCH /posts/{id}` -> `GET /posts/{id}`. Проверяются коды ответов, ID,
   изменённый заголовок и сохранение полей `body` и `user_id`
2. **Авторизация:** `POST /auth/login` -> получение непустого строкового
   `access_token` -> `GET /auth/me` с Bearer-токеном. Проверяются поля профиля.
3. **Без токена:** `/auth/me` возвращает HTTP 401 и не выдаёт профиль.
4. **С неверным токеном:** `/auth/me` возвращает HTTP 401 и не выдаёт профиль.
5. **Дополнительный негативный сценарий:** неверный пароль возвращает HTTP 401
   и сообщение об ошибке, `access_token` отсутствует
6. **GraphQL query:** HTTP 200, `data.hello` равен `Hello Ivan`, `errors` отсутствуют
7. **GraphQL mutation:** HTTP 200, возвращаются непустой ID, отправленные имя
   и возраст; `errors` отсутствуют

## Результаты

Полный прогон завершился успешно с результатом:

```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Скриншоты выполненных проверок находятся в папке [checks](checks/).
