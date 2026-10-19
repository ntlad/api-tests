# Домашнее задание №2: тестирование REST и GraphQL API

## Запуск

Нужны JDK 17, Maven 3.6.3 или новее и доступ к интернету.
Из корня проекта выполнить:

```bash
mvn test
```

Команда `mvn -version` должна показывать Java 17. Если на macOS Maven
использует другую установленную Java, выбрать JDK 17 в текущем терминале:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn test
```

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

## Проверки в Postman

Вручную проверены GraphQL query, mutation и subscription `Greetings`.
Subscription выполнена через SSE с заголовком `Accept: text/event-stream`:
получены пять событий `next` с приветствиями и событие `complete`.
[Коллекция запросов](postman/api-testing-homework-2.postman_collection.json)
добавлена в проект из семинара.

## Результаты

Полный прогон завершился успешно с результатом:

```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Скриншоты выполненных проверок находятся в папке [checks](checks/).
