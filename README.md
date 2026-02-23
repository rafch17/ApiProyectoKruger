# AuthAPI - Sistema de Autenticación y Gestión de Pedidos

## Descripción

API REST con autenticación JWT para gestión de usuarios, productos y pedidos. Incluye un frontend en React.

## Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                        ARQUITECTURA                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌──────────────┐     ┌──────────────┐     ┌──────────────┐    │
│   │    REACT     │     │    RENDER    │     │   RENDER     │    │
│   │   FRONTEND   │────▶│  (Backend)   │────▶│  (Frontend)  │    │
│   │              │     │  Java/Spring │     │   Static     │    │
│   └──────────────┘     └──────────────┘     └──────────────┘    │
│         │                      │                                  │
│         │              ┌───────┴───────┐                         │
│         │              │               │                         │
│         ▼              ▼               ▼                         │
│   ┌──────────┐   ┌──────────┐   ┌──────────┐                    │
│   │  Browser │   │ Render   │   │cockroachDB  │                    │
│   └──────────┘   └──────────┘   └──────────┘                    │
│                                                                  │
│   Frontend (React) ──▶ Render (Hosting)                         │
│   Backend (Java) ───▶ Render (Docker)                           │
│   Database ─────────▶ ChorraDB (PostgreSQL)                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Stack Tecnológico

- **Backend**: Java 17 + Spring Boot 3
- **Frontend**: React 18 + Vite
- **Base de Datos**: PostgreSQL (ChorraDB)
- **Despliegue**: Render

---

## 🚀 Despliegue en Render

### ¿Qué es Render?

[Render](https://render.com) es una plataforma de cloud computing que permite desplegar aplicaciones web de forma gratuita. Características principales:

- **Despliegue automático**: Cada vez que haces push a la rama `main`, Render detecta los cambios y redepliega automáticamente
- **Docker integrado**: Render puede construir y desplegar contenedores Docker automáticamente
- **Base de datos gestionada**: Ofrece PostgreSQL, Redis, etc.
- **SSL gratuito**: Todos los despliegues incluyen HTTPS automático

### Cómo Funciona el Despliegue Automático

1. **Conectar repositorio**: Conectas tu repositorio de GitHub a Render
2. **Detección automática**: Render detecta el `Dockerfile` y lo usa para construir la imagen
3. **Push a main**: Cada vez que haces `git push origin main`:
   - Render recibe la notificación
   - Construye la nueva imagen Docker
   - Despliega automáticamente
   - Cambia el tráfico al nuevo deploy

### Configuración en Render

#### 1. Backend (API)

1. Ve a [Render Dashboard](https://dashboard.render.com)
2. Crea un nuevo servicio: **Web Service**
3. Conecta tu repositorio de GitHub
4. Configura:
   - **Build Command**: (vacío, usa Dockerfile)
   - **Start Command**: (vacío, usa Dockerfile)
5. Agrega las **Variables de Entorno**:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db
   SPRING_DATASOURCE_USERNAME=usuario
   SPRING_DATASOURCE_PASSWORD=password
   SERVER_PORT=8080
   ```

#### 2. Frontend (React)

1. Crea un nuevo servicio: **Static Site**
2. Conecta tu repositorio
3. Configura:
   - **Build Command**: `npm run build`
   - **Publish Directory**: `dist`
4. Agrega **Environment Variables** si es necesario:
   ```
   VITE_API_URL=https://tu-backend.onrender.com/graphql
   ```

---

## 📋 Credenciales de Prueba

| Username  | Password     | Rol    |
|-----------|--------------|--------|
| admin     | password123 | ADMIN  |
| juanperez | password123 | USER   |
| maria     | password123 | USER   |
| carlos    | password123 | USER   |
| lucia     | password123 | USER   |

---

## 🗄️ Datos de Prueba

### Productos (100 productos incluidos)

La tabla `products` viene precargada con 100 productos de tecnología.

### Órdenes de Ejemplo

5 pedidos de prueba con items asociados.

---

## 🛠️ Desarrollo Local

### Prerequisites

- Java 17+
- Docker y Docker Compose
- Node.js 18+ (para frontend)

### Ejecutar con Docker Compose

```bash
# Levantar todos los servicios
docker-compose up --build

# La API estará en: http://localhost:8080
# PostgreSQL en: localhost:5432
```

### Ejecutar Frontend Local

```bash
cd frontend
npm install
npm run dev
```

---

## 📁 Estructura del Proyecto

```
AuthAPI/
├── src/                          # Código fuente Java (Backend)
│   ├── main/java/com/example/demo/
│   │   ├── config/               # Configuraciones de Spring
│   │   ├── model/                # Entidades JPA
│   │   ├── repository/           # Repositorios
│   │   ├── service/             # Lógica de negocio
│   │   ├── resolver/             # GraphQL Resolvers
│   │   ├── security/            # JWT y seguridad
│   │   └── dto/                 # Data Transfer Objects
│   └── resources/
│       └── application.yml      # Configuración
├── Dockerfile                    # Imagen Docker del backend
├── docker-compose.yml           # Orquestación de servicios
└── pom.xml                      # Dependencias Maven
```

---

## 🏗️ Arquitectura del Código (Hexagonal)

Este proyecto utiliza una **Arquitectura Hexagonal** (también conocida como **Ports and Adapters**), que permite mantener el código bien separado y fácil de probar.

### ¿Qué es la Arquitectura Hexagonal?

La Arquitectura Hexagonal es un patrón de diseño que separa la lógica de negocio de los mecanismos de entrada y salida. Imagina un hexágono donde:

- **El núcleo (centro)**: Contiene la lógica de negocio pura
- **Los lados (puertos)**: Son las interfaces que conectan con el exterior
- **Los adaptadores**: Implementan los puertos para comunicarse con tecnologías específicas

### Diagrama de la Arquitectura Hexagonal

```
                    ┌─────────────────────────────────────┐
                    │         EXTERIOR (Adapters)          │
                    │  ┌─────────────┐  ┌──────────────┐  │
                    │  │   GraphQL   │  │    REST      │  │
                    │  │  Resolvers  │  │   (Futuro)   │  │
                    │  └──────┬──────┘  └──────┬───────┘  │
                    │         │                 │           │
                    └─────────┼─────────────────┼───────────┘
                              │                 │
                    ┌─────────▼─────────────────▼───────────┐
                    │            PUERTOS (Interfaces)        │
                    │  ┌─────────────────────────────────┐   │
                    │  │   UserService, ProductService, │   │
                    │  │   OrderService (Puerto de     │   │
                    │  │         entrada)               │   │
                    │  └─────────────────────────────────┘   │
                    │  ┌─────────────────────────────────┐   │
                    │  │  UserRepository, ProductRepo,  │   │
                    │  │  OrderRepository (Puerto de    │   │
                    │  │         salida)                 │   │
                    │  └─────────────────────────────────┘   │
                    └─────────────────┬───────────────────────┘
                                      │
                    ┌─────────────────▼───────────────────────┐
                    │          DOMINIO (Core/Núcleo)          │
                    │  ┌─────────────────────────────────┐   │
                    │  │  User, Product, Order,          │   │
                    │  │  OrderItem (Entidades)          │   │
                    │  └─────────────────────────────────┘   │
                    │  ┌─────────────────────────────────┐   │
                    │  │  JwtService, TokenBlacklist     │   │
                    │  │  (Lógica de negocio)            │   │
                    │  └─────────────────────────────────┘   │
                    └────────────────────────────────────────┘
                                      │
                    ┌─────────────────▼───────────────────────┐
                    │         EXTERIOR (Adapters)            │
                    │  ┌─────────────────────────────────┐   │
                    │  │  PostgreSQL (JPA/Hibernate)    │   │
                    │  │  via Spring Data Repository    │   │
                    │  └─────────────────────────────────┘   │
                    └────────────────────────────────────────┘
```

### Cómo se Aplica en Este Proyecto

| Capa/Paquete          | Rol en Hexagonal          | Descripción |
|-----------------------|---------------------------|-------------|
| **model/**            | Dominio (Entities)        | Entidades puras del negocio: User, Product, Order, OrderItem |
| **service/**          | Dominio (Use Cases)       | Lógica de negocio: UserService, ProductService, OrderService |
| **resolver/**         | Puerto de Entrada        | Adapta las peticiones GraphQL a llamadas del servicio |
| **repository/**      | Puerto de Salida         | Interfaz para acceder a la base de datos |
| **security/**         | Dominio (Infraestructura)| Manejo de JWT y autenticación |
| **dto/**              | Adaptador                | Objetos para transferir datos hacia/desde el exterior |

### Beneficios de Esta Arquitectura

1. **Bajo acoplamiento**: Los servicios no conocen cómo se accede a ellos (GraphQL, REST, etc.)
2. **Fácil testing**: Puedes probar la lógica de negocio sin needing una base de datos
3. **Flexibilidad**: Puedes cambiar de GraphQL a REST sin modificar la lógica de negocio
4. **Separación de responsabilidades**: Cada cosa está en su lugar

### Flujo de una Petición

```
1. Cliente (Frontend) envía query GraphQL
        │
        ▼
2. Resolver (AuthResolver/OrderResolver) recibe la petición
        │
        ▼
3. Resolver llama al Service correspondiente
        │
        ▼
4. Service ejecuta la lógica de negocio
        │
        ▼
5. Service usa Repository para acceder a datos
        │
        ▼
6. Repository (JPA) consulta PostgreSQL
        │
        ▼
7. Respuesta vuelve por el mismo camino
```

---

## 🔌 Endpoints GraphQL

### Autenticación

```graphql
mutation Login($username: String!, $password: String!) {
  login(username: $username, password: $password) {
    token
    user {
      id
      username
      role
    }
  }
}

mutation Register($username: String!, $password: String!) {
  register(username: $username, password: $password) {
    id
    username
  }
}
```

### Productos

```graphql
query GetProducts {
  products {
    id
    name
    description
    price
  }
}
```

### Órdenes

```graphql
query GetOrders {
  orders {
    id
    total
    status
    createdAt
    items {
      quantity
      price
      product {
        name
      }
    }
  }
}
```

---

## 📄 Licencia

MIT License

