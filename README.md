# Online Store Backend API

Это backend-проект интернет-магазина, который я сделал на Java и Spring Boot для практики и портфолио.

В проекте реализованы основные функции интернет-магазина: регистрация и авторизация пользователей, JWT token, роли USER и ADMIN, товары, категории, корзина, оформление заказов и простая имитация оплаты.

## Стек технологий

В проекте использовал:

* Java 21
* Spring Boot
* Spring Web
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* PostgreSQL
* Docker
* Docker Compose
* Maven
* Swagger / OpenAPI
* Bean Validation

## Что умеет проект

### Auth

Реализована регистрация пользователя и логин. После успешного логина пользователь получает JWT token, который потом используется для доступа к защищённым endpoints.

Пароли хранятся не в открытом виде, а хешируются через BCrypt.

### Users и роли

В проекте есть две роли:

* USER
* ADMIN

USER может работать со своей корзиной, оформлять заказы и просматривать свои заказы.

ADMIN может создавать и редактировать товары, категории, а также управлять заказами.

### Products

Для товаров реализован CRUD:

* создание товара
* получение всех товаров
* получение товара по ID
* обновление товара
* удаление товара
* поиск товаров
* фильтр по категории
* фильтр по цене
* пагинация
* сортировка

### Categories

Категории нужны для группировки товаров.

Реализовано:

* создание категории
* получение всех категорий
* получение категории по ID

У товара есть связь с категорией через `ManyToOne`.

### Cart

Корзина привязана к текущему пользователю, который определяется через JWT token.

Пользователь может:

* добавить товар в корзину
* изменить количество товара
* удалить товар из корзины
* очистить корзину

### Orders

Заказ создаётся из корзины пользователя.

При оформлении заказа:

* создаётся заказ
* создаются элементы заказа
* сохраняется название товара и цена на момент покупки
* уменьшается количество товара на складе
* корзина очищается после оформления заказа

Также пользователь может посмотреть свои заказы.

### Payments

В проекте есть учебная имитация оплаты.

Статусы оплаты:

* PENDING
* SUCCESS
* FAILED
* REFUNDED

Если оплата успешная, статус заказа меняется на `PAID`.

Также генерируется `transactionId`.

Пользователь не может оплатить чужой заказ.

### Admin Orders

Для администратора реализовано управление заказами:

* просмотр всех заказов
* просмотр заказа по ID
* фильтр заказов по статусу
* изменение статуса заказа
* проверка правильных переходов между статусами
* возврат товара на склад при отмене заказа

## Структура проекта

```text
src/main/java/com/example/onlinestore

├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
├── service
├── specification
└── OnlineStoreApplication.java
```

## Как я разделил проект

```text
Controller  → принимает HTTP-запросы
Service     → содержит бизнес-логику
Repository  → работает с базой данных
Entity      → описывает таблицы базы данных
DTO         → используется для request/response моделей
Security    → JWT, фильтры, роли и авторизация
Exception   → обработка ошибок
```

## Основные Entity

В проекте используются такие основные сущности:

* AppUser
* Product
* Category
* CartItem
* CustomerOrder
* OrderItem
* Payment

## Связи в базе данных

```text
AppUser 1 → many CartItem
AppUser 1 → many CustomerOrder
Category 1 → many Product
Product 1 → many CartItem
Product 1 → many OrderItem
CustomerOrder 1 → many OrderItem
CustomerOrder 1 → 1 Payment
```

## Запуск проекта через Docker Compose

### 1. Клонировать проект

```bash
git clone https://github.com/your-username/online-store.git
cd online-store
```

### 2. Запустить приложение и PostgreSQL

```bash
docker compose up --build
```

Или запустить в фоне:

```bash
docker compose up --build -d
```

### 3. Проверить контейнеры

```bash
docker ps
```

После запуска должны быть контейнеры приложения и базы данных:

```text
online_store_app
online_store_postgres
```

## Swagger UI

После запуска проекта Swagger доступен по адресу:

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8081/v3/api-docs
```

## Настройки базы данных

По умолчанию используется PostgreSQL:

```text
Database: online_store_db
Username: postgres
Password: postgres
Port: 5433
```

В Docker Compose backend подключается к базе так:

```text
jdbc:postgresql://postgres:5433/online_store_db
```

## Основные API endpoints

### Auth

```http
POST /api/auth/register
POST /api/auth/login
```

### Products

```http
GET    /api/products
GET    /api/products/{id}
GET    /api/products/search
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

### Categories

```http
GET  /api/categories
GET  /api/categories/{id}
POST /api/categories
```

### Cart

```http
GET    /api/cart
POST   /api/cart/items
PUT    /api/cart/items/{itemId}
DELETE /api/cart/items/{itemId}
DELETE /api/cart
```

### Orders

```http
POST /api/orders/checkout
GET  /api/orders/my
GET  /api/orders/{orderId}
```

### Payments

```http
POST /api/payments/pay
GET  /api/payments/my
```

### Admin Orders

```http
GET   /api/admin/orders
GET   /api/admin/orders/{orderId}
GET   /api/admin/orders/status/{status}
PATCH /api/admin/orders/{orderId}/status
```

## Пример регистрации

```http
POST /api/auth/register
```

```json
{
  "fullName": "Test User",
  "email": "user@test.com",
  "password": "123456"
}
```

## Пример логина

```http
POST /api/auth/login
```

```json
{
  "email": "user@test.com",
  "password": "123456"
}
```

Пример ответа:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "fullName": "Test User",
    "email": "user@test.com",
    "role": "USER",
    "enabled": true,
    "createdAt": "2026-06-16T12:00:00"
  }
}
```

## JWT Authorization

Для защищённых endpoints нужно передавать token в header:

```http
Authorization: Bearer YOUR_TOKEN
```

В Swagger UI нужно нажать кнопку `Authorize` и вставить:

```text
Bearer YOUR_TOKEN
```

## Пример создания категории

Создание категории доступно только пользователю с ролью ADMIN.

```http
POST /api/categories
```

```json
{
  "name": "Accessories"
}
```

## Пример создания товара

Создание товара доступно только ADMIN.

```http
POST /api/products
```

```json
{
  "name": "AirPods Pro 2",
  "description": "Wireless headphones",
  "price": 120000,
  "stockQuantity": 20,
  "categoryId": 1
}
```

## Пример поиска товаров

```http
GET /api/products/search?keyword=phone&page=0&size=10&sortBy=price&sortDir=asc
```

Для поиска можно использовать такие параметры:

```text
keyword
categoryId
minPrice
maxPrice
active
page
size
sortBy
sortDir
```

## Пример добавления товара в корзину

```http
POST /api/cart/items
```

```json
{
  "productId": 1,
  "quantity": 2
}
```

## Пример оформления заказа

```http
POST /api/orders/checkout
```

```json
{
  "shippingAddress": "Turkestan, Kazakhstan",
  "phoneNumber": "+77001234567"
}
```

## Пример оплаты заказа

```http
POST /api/payments/pay
```

```json
{
  "orderId": 1,
  "method": "KASPI",
  "success": true
}
```

Поле `success` используется только для учебной имитации оплаты.

В реальном проекте результат оплаты должен приходить от внешней платёжной системы через callback или webhook.

## Order statuses

```text
PENDING_PAYMENT
PAID
PROCESSING
SHIPPED
DELIVERED
CANCELLED
```

## Payment statuses

```text
PENDING
SUCCESS
FAILED
REFUNDED
```

## Безопасность

В проекте реализовано:

* JWT-аутентификация
* BCrypt-хеширование паролей
* доступ по ролям
* защита admin endpoints
* проверка владельца корзины
* проверка владельца заказа
* запрет оплаты чужого заказа
* глобальная обработка ошибок

## Примеры ошибок API

### 401 Unauthorized

```json
{
  "status": 401,
  "error": "UNAUTHORIZED",
  "message": "Нужен JWT token"
}
```

### 403 Forbidden

```json
{
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Недостаточно прав"
}
```

### 404 Not Found

```json
{
  "timestamp": "2026-06-16T12:00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Товар не найден",
  "path": "/api/products/999",
  "validationErrors": null
}
```

### 400 Validation Error

```json
{
  "timestamp": "2026-06-16T12:00:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Ошибка валидации",
  "path": "/api/products",
  "validationErrors": {
    "name": "Название товара обязательно",
    "price": "Цена должна быть больше 0"
  }
}
```

## Что я изучил во время работы над проектом

Во время разработки этого проекта я лучше разобрался с тем, как строится backend на Spring Boot.

Особенно полезной была практика с:

* созданием REST API
* подключением PostgreSQL
* работой с Hibernate и JPA-связями
* использованием DTO
* разделением проекта на слои
* обработкой ошибок
* JWT-аутентификацией
* Spring Security
* ролями USER и ADMIN
* пагинацией и сортировкой
* Specification для поиска
* транзакциями
* Dockerfile
* Docker Compose
* Swagger/OpenAPI

## Что можно добавить в будущем

В будущем проект можно улучшить и добавить:
* refresh token
* подтверждение email
* реальную оплату через Stripe или Kaspi API
* unit и integration tests
* Flyway или Liquibase
* CI/CD через GitHub Actions
* кеширование через Redis
* загрузку изображений товаров
* избранные товары
* отзывы и рейтинг товаров
