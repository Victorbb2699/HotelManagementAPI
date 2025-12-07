#  API de Gestión de Hoteles (Hotel Management API)

##  1. Título y Descripción

La **Hotel Management API** es una API RESTful diseñada para la gestión integral de hoteles. Permite a los usuarios crear, listar, actualizar y eliminar registros de hoteles con validaciones estrictas, garantizando la integridad de los datos.

### Lo que destaca del proyecto:

* **Validaciones Robustas:** Implementación estricta de validaciones para todos los campos (nombre, estrellas, dirección).
* **Manejo de Errores:** Gestión de errores centralizada para conflictos (`409`), recursos no encontrados (`404`) y errores de validación (`400`).
* **Seguridad con BasicAuth y Roles:** Acceso controlado a través de **Basic Authentication**, con roles \USER` y `ADMIN`.
* **Documentación Interactiva:** Documentación automática y navegable con **Swagger/OpenAPI**.

---

##  2. Tecnologías

Se ha utilizado un *stack* moderno de Java y Spring Boot:

| Componente | Versión / Descripción |
| :--- | :--- |
| **Lenguaje** | Java 21+ |
| **Framework** | Spring Boot 3.3.4 |
| **Persistencia** | Spring Data JPA |
| **Base de Datos** | H2 (en memoria, para entorno de desarrollo/evaluación) |
| **Seguridad** | Spring Security (BasicAuth) |
| **Documentación** | Swagger/OpenAPI |
| **Testing** | JUnit 5 + Mockito |
| **Mapeo** | MapStruct o mappers manuales para DTOs |

---

## 3. Requisitos Previos

Para la ejecución local se requiere:

* **JDK 21+**
* **Maven**
* **Git**

---

##  4. Puesta en Marcha

Sigue estos pasos para levantar la aplicación en tu entorno local:

1.  **Clonar el repositorio:**
    ```bash
    git clone <repo_url>
    cd HotelManagementAPI/hotel-api
    ```
2.  **Compilar e instalar dependencias:**
    ```bash
    mvn clean install
    ```
3.  **Ejecutar la aplicación:**
    ```bash
    mvn spring-boot:run
    ```

### Acceso a la Documentación

Una vez iniciada la aplicación, puedes acceder a la documentación interactiva de la API en:

**`http://localhost:8080/swagger-ui/index.html`**

---

## 🔒 5. Seguridad y Autenticación (BasicAuth)

La seguridad se implementa usando **Spring Security** con **Basic Authentication** (`.httpBasic()`) para la autenticación simple y usuarios en memoria. La autorización se realiza mediante roles.

### Roles de Usuario:

* **`USER`**: Permite listar, crear y actualizar hoteles.
* **`ADMIN`**: Permite eliminar hoteles, además de todas las acciones del rol `USER`.

### Configuración

1.  **Mecanismo:** Basic Authentication (`.httpBasic()`).
2.  **Autorización:** Control de acceso basado en roles, configurado para rutas (`/hotels/**`) y métodos (`@EnableMethodSecurity`).
3.  **Contraseñas:** Encriptadas usando **BCrypt**.

### Roles y Credenciales de Prueba (En Memoria)

| Usuario | Contraseña | Rol | Acceso a Eliminar (`DELETE`) |
| :--- | :--- | :--- | :--- |
| `user` | `password` | `USER` | ❌ No |
| `admin` | `adminpass` | **`ADMIN`** | ✅ Sí |

### Flujo de Acceso

El cliente debe incluir las credenciales codificadas en **Base64** en el *header* `Authorization` de cada solicitud protegida.

**Ejemplo de Petición (cURL con Admin):**

```bash
# 1. Codificar: admin:adminpass
# 2. El código Base64 resultante se usa en el header
curl -X GET http://localhost:8080/hotels \
-H "Authorization: Basic YWRtaW46YWRtaW5wYXNz"
```

---


## 6.  Endpoints de la API

Todos los *endpoints* listados requieren el *header* `Authorization: Basic <código_base64>`(o el header correspondiente para Basic Authentication).

| Método | Endpoint | Descripción | Roles | Respuestas Principales |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/hotels` | Crear un nuevo hotel. | `USER` | `201` (creado), `400` (validación), `409` (duplicado) |
| `GET` | `/hotels` | Listado de hoteles paginado y ordenable. | `USER` | `200` (ok), `400` (parámetros inválidos) |
| `GET` | `/hotels/{id}` | Obtener hotel por ID. | `USER` | `200`, `404` |
| `GET` | `/hotels/city/{city}` | Filtrar hoteles por ciudad. | `USER` | `200` |
| `PUT` | `/hotels/{id}` | Actualizar nombre y estrellas del hotel. | `USER` | `200`, `404`, `409` |
| `PUT` | `/hotels/{id}/address`| Actualizar la dirección del hotel. | `USER` | `200`, `404`, `409` |
| `DELETE` | `/hotels/{id}` | Eliminar hotel. | **`ADMIN`** | `204`, `403` (Prohibido), `404` |

---

## 7.  Características Avanzadas

El proyecto incorpora características que mejoran la calidad y usabilidad de la API:

* **Paginación (Paging) y Ordenación (Sorting):** Soportado en el *endpoint* de listado.
    * **Paginación:** `GET /hotels?page=0&size=10`
    * **Ordenación:** `GET /hotels?sort=name,asc&sort=stars,desc`

* **Ejemplo JSON de Paginación:**
    ```json
    {
      "page": 0,
      "size": 10,
      "sort": ["name,asc", "stars,desc"]
    }
    ```

---

## 8. Diseño y Arquitectura

La aplicación sigue una arquitectura limpia y modular con el objetivo de ser escalable y fácil de mantener.

### Estructura Principal del Proyecto:
es.twd.hotel 
├─ controller 
├─ service 
├─ repository 
├─ dto 
├─ entity 
├─ exception 
├─ mapper 
└─ security

* **Patrón Service-Repository:** Implementación clara de la separación de la lógica de negocio (`service`) y la persistencia de datos (`repository`).
* **DTOs:** Uso de **Data Transfer Objects** (`dto`) para desacoplar las entidades de la capa de presentación y validación. Mapeo asistido por **MapStruct** o mappers manuales.
* **GlobalExceptionHandler:** Clase centralizada para el manejo uniforme de todas las excepciones, asegurando respuestas consistentes al cliente.

---

## 9.  Testing

Se han implementado pruebas exhaustivas para garantizar la robustez y la confiabilidad del proyecto.

### Cobertura y Tipos de Tests

El proyecto alcanza una cobertura del **96.7%** e incluye:

* **Tests Unitarios de Validación:** Pruebas para DTOs que aseguran la validez de los datos de entrada.
* **Manejo de Errores:** Pruebas específicas para manejo de excepciones, asegurando el retorno de códigos HTTP correctos.
* **Tests de Funcionalidad:** Cobertura completa de las operaciones **CRUD** (Crear, Leer, Actualizar, Borrar), paginación y ordenación.
* **Tests de Seguridad:** Pruebas detalladas para validar las restricciones de roles y autenticación (`HotelControllerSecurityTest`).

---

## 10.  Buenas Prácticas y Mejoras

Este proyecto fue desarrollado bajo principios de desarrollo profesional y escalabilidad:

* **Código Limpio y Modular:** Estructura que facilita la lectura y el mantenimiento.
* **Validaciones Estrictas:** Prevención de datos inválidos en la capa de entrada.
* **Manejo de Conflictos y Errores:** Experiencia de usuario mejorada con mensajes de error claros y códigos HTTP correctos.
* **Documentación Swagger:** Facilita la exploración y el consumo de la API.
* **Escalabilidad:** Estructura de paquetes y patrón Service-Repository preparados para el crecimiento futuro.
* **Testing Robusto:** Alta cobertura y enfoque en casos límite (*edge cases*).

---

##  11. Notas de Paginación y Pruebas (Troubleshooting)

Esta sección detalla el comportamiento de la API en relación con la paginación (`size` y `sort`) y las posibles inconsistencias observadas durante la prueba con interfaces interactivas como Swagger UI.

### 1. Manejo de Parámetros de Paginación Inválidos

La API está configurada para manejar robustamente parámetros de paginación que no cumplen con los requisitos de **Spring Data Pageable** (el cual exige que `size >= 1`).

* **Comportamiento con `size=0`:** Si se recibe un parámetro `size` menor a 1 (por ejemplo, `size=0`), la API ignora este valor y aplica el **tamaño de página por defecto** (`10`), o devuelve un error **`400 Bad Request`** según la configuración específica.
    >  **Defensa:** Este manejo es correcto y demuestra que la API no permite estados de paginación inválidos.
* **Comportamiento con Campos de Ordenación Inválidos:** Si se proporciona un campo en el parámetro `sort` que no existe en la entidad, la API devuelve un error **`400 Bad Request`** con un mensaje claro.

### 2. Pruebas de Ordenación (*Sorting*)

* **Ordenación Correcta:** El *endpoint* funciona correctamente para ordenación sobre campos directos (`sort=name,asc`, `sort=stars,desc`).
* **Campos Anidados (Nested Fields):** **Swagger UI tiene limitaciones** al intentar probar la ordenación sobre campos anidados (ej. `address.city` o `address.country`).
    >  Las pruebas unitarias y de integración confirman que la API maneja la ordenación sobre campos anidados correctamente utilizando la notación `property.nestedProperty`. Se recomienda probar estos casos directamente con `cURL` o Postman.

