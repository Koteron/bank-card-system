# 🚀 Bank Cards Management System

## 📝 Описание проекта

**Bank Cards Management System** — это backend-приложение на Java (Spring Boot) для безопасного управления банковскими картами. Система реализует роли пользователей (ADMIN и USER), управление картами, переводы, работу с разными валютами и расширенные функции для юзеров.

---

## 💡 Основной функционал

### 🔐 Аутентификация и авторизация

* Spring Security + JWT
* Роли: `USER`, `ADMIN`
* Обработка ошибок (401, 403, 404, 500)

### 👤 Пользовательские возможности

* Регистрация, логин
* Просмотр своих карт (filter + pagination)
* Переводы между своими картами
* Депозит и снятие средств
* Запрос на блокировку карты
* Обновление профиля: пароль, email, nickname
* Удаление профиля

### 🛠 ️ Админские возможности

* Расширенный поиск карт - также по пользователям
* Управление пользователями
* CRUD-операции над картами
* Просмотр всех карт

### 💳 Карты

* Шифрование номеров с маской `**** **** **** 1234`
* Статус: `ACTIVE`, `LOCKED`, `EXPIRED`, `PENDING_LOCK`
* Баланс и валюта

### 💱 Валюты

* Сделано 3 валюты: RUB, USD, EUR
* Конвертация при переводах
* Курсы хранятся в `application.yml`

---

## 📃 API-документация

* Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/docs)
* Группы:

    * Admin API
    * User API
    * Unauthenticated API
    * All API

---

## 📊 Технологии

Java 17+, Spring Boot, Spring Security, Spring Data JPA, JWT, PostgreSQL, Liquibase, Docker, Swagger/OpenAPI, MapStruct, Lombok, JUnit, Mockito, JaCoCo

---

## ▶️ Запуск

```bash
docker-compose up -d
```

---

## 🔮 Тесты

* Unit-тесты: `@Service` и `@Controller`
* Jacoco-отчет: `target/site/jacoco/index.html`

```bash
mvn verify
```

---

## 🔒 Безопасность

* JWT и ролевой доступ
* Маскировка номеров карт при выдаче на фронт
* Шифрование номеров карт в БД с помощью AES
* Групповой Swagger

