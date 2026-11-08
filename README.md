# TODO List — Spring Boot REST API + CI/CD

REST API для управления задачами (TODO-лист) с хранением в памяти, валидацией,
тестами (покрытие ≥ 70% через JaCoCo), многостадийным Docker-образом
(сборка → тесты → Docker Hub → деплой на ВМ по SSH
через Tailscale, обход CGNAT).

## REST API
| Метод  | Путь          | Описание                 |
|--------|---------------|--------------------------|
| POST   | `/tasks`      | создать задачу           |
| GET    | `/tasks`      | получить все задачи      |
| GET    | `/tasks/{id}` | получить одну задачу     |
| PUT    | `/tasks/{id}` | обновить задачу          |
| DELETE | `/tasks/{id}` | удалить задачу           |

Структура задачи:
```json
{ "id": 1, "title": "Купить молоко", "completed": false }
```
Валидация: `title` — обязательное, непустое, ≤ 255 символов (иначе `400` с полем `fields.title`).
Несуществующий `id` → `404`.

## Локальный запуск
```bash
mvn clean verify          # сборка + тесты + проверка покрытия ≥70%
mvn spring-boot:run       # запуск на http://localhost:8080
```
Отчёт о покрытии: `target/site/jacoco/index.html`.

### Примеры запросов (curl)
```bash
curl -X POST localhost:8080/tasks -H "Content-Type: application/json" -d '{"title":"Купить молоко"}'
curl localhost:8080/tasks
curl -X PUT localhost:8080/tasks/1 -H "Content-Type: application/json" -d '{"title":"Купить молоко","completed":true}'
curl -X DELETE localhost:8080/tasks/1
```

## Docker (локально)
```bash
docker build -t todo-app .
docker run -d --name todo-app -p 8080:8080 todo-app
```

### Секреты репозитория (Settings → Secrets and variables → Actions)
| Секрет              | Назначение                                    |
|---------------------|-----------------------------------------------|
| `DOCKER_USERNAME`   | логин Docker Hub                              |
| `DOCKER_PASSWORD`   | пароль или access token Docker Hub            |
| `TAILSCALE_AUTHKEY` | эфемерный auth key из Tailscale (для раннера) |
| `VM_HOST`           | **Tailscale-адрес ВМ** (`100.x.y.z`)          |
| `VM_USER`           | пользователь SSH на ВМ (напр. `nik`)          |
| `VM_SSH_KEY`        | приватный SSH-ключ (весь текст)               |


