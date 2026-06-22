# 📋 Guía de la rúbrica — Qué hace cada herramienta y dónde encontrarla

Este documento explica, **tema por tema de la rúbrica (Unidad 3)**:
1. **Qué es** y para qué sirve.
2. **Qué hace en este proyecto**.
3. **Dónde encontrarlo** (archivos y clases exactas) para mostrarlo en la evaluación.
4. **Cómo demostrarlo funcionando**.

> 🗂️ Estructura base: 6 módulos en `Microservicios/` → `api-gateway`, `eureka-server`,
> `ms-login`, `ms-usuarios`, `ms-inventario`, `ms-citas`.
> Paquete de código: `com.veterinaria.<modulo>`.
> Stack: Java 21 · Spring Boot 3.4.5 · Spring Cloud 2024.0.1.

---

## 1. 🚪 API Gateway (Spring Cloud Gateway)

**Qué es:** una puerta de entrada única. En vez de llamar a cada microservicio por su
puerto, llamas todo por `http://localhost:8080` y el Gateway reenvía a quien corresponde.

**Qué hace aquí:** enruta por la URL hacia cada microservicio, descubriéndolos por nombre
vía Eureka (`lb://` = load balanced).

**Dónde encontrarlo:**
- `api-gateway/src/main/resources/application.yml` → sección `spring.cloud.gateway.routes` (las reglas de ruteo) y puerto `8080`.
- `api-gateway/src/main/java/com/veterinaria/gateway/ApiGatewayApplication.java` → clase principal.
- `api-gateway/pom.xml` → dependencia `spring-cloud-starter-gateway`.

**Rutas configuradas:**
- `/api/auth/**`, `/api/usuarios/**` → ms-login
- `/api/duenos/**`, `/api/mascotas/**` → ms-usuarios
- `/api/productos/**` → ms-inventario
- `/api/citas/**` → ms-citas

**Cómo demostrarlo:** con todo arriba, entra a `http://localhost:8080/api/duenos`
(con token) — responde aunque ms-usuarios esté en el 8081: lo resolvió el Gateway.

---

## 2. 🗺️ Eureka — Service Discovery

**Qué es:** un registro central (como una "guía telefónica"). Cada microservicio se
inscribe al arrancar, y así el Gateway sabe dónde está cada uno sin URLs fijas.

**Qué hace aquí:** el `eureka-server` (puerto 8761) recibe el registro de los 5 servicios
(gateway + 4 MS de negocio).

**Dónde encontrarlo:**
- **Servidor:** `eureka-server/src/main/java/com/veterinaria/eureka/EurekaServerApplication.java` → anotación **`@EnableEurekaServer`**.
- `eureka-server/src/main/resources/application.yml` → `register-with-eureka: false`, puerto 8761.
- **Clientes:** cada `ms-*/pom.xml` y `api-gateway/pom.xml` incluyen `spring-cloud-starter-netflix-eureka-client`. La config está en el perfil `dev` de cada `application.yml`: `eureka.client.service-url.defaultZone: http://localhost:8761/eureka/`.

**Cómo demostrarlo:** abre **http://localhost:8761** → verás la tabla
"Instances currently registered with Eureka" con los 5 servicios.

---

## 3. 📚 Swagger / OpenAPI

**Qué es:** documentación interactiva de la API. Permite ver todos los endpoints y
probarlos desde el navegador.

**Qué hace aquí:** cada MS de negocio expone su Swagger en `/doc/swagger-ui.html`, con
botón **Authorize** para enviar el token JWT, y **ejemplos reales** en cada campo.

**Dónde encontrarlo:**
- `*/pom.xml` → dependencia `springdoc-openapi-starter-webmvc-ui` (v2.7.0).
- **Config + botón Authorize:** `<cada-ms>/src/main/java/com/veterinaria/<modulo>/config/SwaggerConfig.java` → bean `customOpenApi()` que define el esquema `bearerAuth` (JWT).
- **Ejemplos en los campos (`@Schema`):** todos los DTOs en `<ms>/.../dto/*.java`, p.ej. `ms-usuarios/.../dto/DuenoRequestDTO.java` → cada campo con `@Schema(description=..., example=...)`.
- **Descripciones de endpoints (`@Operation`, `@Tag`):** en los controllers, p.ej. `ms-inventario/.../controller/ProductoController.java`.
- **Token recordado:** `ms-usuarios/src/main/resources/application.yml` → `springdoc.swagger-ui.persist-authorization: true`.

**Cómo demostrarlo:** abre cualquier Swagger, muestra que los campos traen ejemplos
(no "string"), pulsa Authorize y ejecuta un GET.

---

## 4. 🔗 HATEOAS (API v2 con hipervínculos)

**Qué es:** un estilo de API REST "maduro" donde cada respuesta incluye **enlaces**
(`_links`) a acciones relacionadas (formato HAL).

**Qué hace aquí:** cada MS de negocio tiene una **versión 2** (`/api/v2/...`) que devuelve
los recursos envueltos con enlaces `self` y a la colección.

**Dónde encontrarlo:**
- `*/pom.xml` → dependencia `spring-boot-starter-hateoas`.
- **Controllers V2:** `ms-usuarios/.../controller/MascotaControllerV2.java` y `DuenoControllerV2.java`; `ms-inventario/.../ProductoControllerV2.java`; `ms-citas/.../CitaControllerV2.java`; `ms-login/.../UsuarioCuentaControllerV2.java`. Usan `EntityModel`, `CollectionModel` y `MediaTypes.HAL_JSON_VALUE`.
- **Assemblers:** `<ms>/.../assembler/*ModelAssembler.java` (implementan `RepresentationModelAssembler`) → método `toModel()` que agrega los links.

**Cómo demostrarlo:** en Swagger, ejecuta un GET de `/api/v2/...` y muestra el bloque
`_links` con la URL `self` en la respuesta.

---

## 5. 🔐 Seguridad con JWT

**Qué es:** autenticación con token. Haces login una vez, recibes un token firmado, y lo
envías en cada petición para identificarte.

**Qué hace aquí:** **ms-login emite** el token; los otros 3 MS lo **validan** (con la misma
clave secreta). Endpoints protegidos responden 403 sin token válido.

**Dónde encontrarlo:**
- `*/pom.xml` → `spring-boot-starter-security` + `io.jsonwebtoken:jjwt-*` (v0.12.6).
- **Reglas de acceso:** `<ms>/.../security/SecurityConfig.java` → Swagger y login `permitAll`, el resto `authenticated`; stateless; registra el filtro JWT.
- **Filtro que lee el token:** `<ms>/.../security/JwtAuthFilter.java` → lee `Authorization: Bearer <token>`.
- **Generar/validar token:** `<ms>/.../security/JwtService.java`.
  - En **ms-login** este servicio **genera** el token (`generarToken`).
  - En los demás solo **valida**.
- **Login (emisión real):** `ms-login/.../service/AuthService.java` (valida con BCrypt) → `ms-login/.../controller/AuthController.java` (`POST /api/auth/login`).
- **Clave compartida:** `jwt.secret` en cada `application.yml` (mismo valor → por eso todos validan el mismo token).

**Cómo demostrarlo:** llama un endpoint protegido **sin** token → 403. Haz login en
ms-login, copia el token, Authorize, repite → 200 con datos.

---

## 6. 🧪 Testing (JUnit 5 + Mockito + MockMvc)

**Qué es:** pruebas automáticas que verifican que la lógica funcione sin levantar el
sistema completo.

**Qué hace aquí:** **17 clases de test** cubren la capa de **servicios** (lógica) y la de
**controllers** (la web), en los 4 MS de negocio.

**Dónde encontrarlo:**
- Carpeta `<ms>/src/test/java/com/veterinaria/<modulo>/...`
  - **Tests de service** (ej. `ms-usuarios/.../service/MascotaServiceTest.java`): usan `@ExtendWith(MockitoExtension.class)` + `@Mock` (simulan el repositorio).
  - **Tests de controller** (ej. `.../controller/MascotaControllerTest.java` y `...V2Test.java`): usan `@WebMvcTest` + `MockMvc` para simular peticiones HTTP.
- `*/pom.xml` → `spring-boot-starter-test`.

**Cómo demostrarlo / ejecutarlos:**
```bash
mvn -f ms-usuarios/pom.xml test
```
(verás los tests pasar; repite con cada módulo).

---

## 7. 📊 JaCoCo — Cobertura de pruebas

**Qué es:** mide **qué porcentaje del código** está cubierto por los tests y genera un
reporte. Aquí, además, **obliga a un mínimo del 80%**.

**Qué hace aquí:** al ejecutar los tests, genera un reporte HTML y **falla el build** si la
cobertura de líneas baja del 80%.

**Dónde encontrarlo:**
- `<ms>/pom.xml` → plugin `jacoco-maven-plugin` (v0.8.12) con executions `prepare-agent`, `report` y `check` (regla `LINE` `COVEREDRATIO` mínimo **0.80**).
- Excluye clases sin lógica (modelos, DTOs, config, security, assemblers) para medir solo lo que importa.

**Cómo demostrarlo / ver el reporte:**
```bash
mvn -f ms-usuarios/pom.xml test
```
Luego abre: `ms-usuarios/target/site/jacoco/index.html` → muestra el % de cobertura.

---

## 8. 💾 Persistencia: JPA/Hibernate + Flyway

**Qué es:**
- **JPA/Hibernate:** mapea clases Java ↔ tablas de la base de datos.
- **Flyway:** versiona y crea el esquema (tablas + datos) con scripts SQL numerados.

**Qué hace aquí:** Flyway crea las tablas y datos iniciales al arrancar; Hibernate solo
**valida** (`ddl-auto: validate`) que las entidades coincidan con esas tablas.

**Dónde encontrarlo:**
- **Entidades:** `<ms>/.../model/*.java` con `@Entity` (ej. `ms-usuarios/.../model/Dueno.java`, `Mascota.java` con relación `@ManyToOne`).
- **Repositorios:** `<ms>/.../repository/*Repository.java` (extienden `JpaRepository`).
- **Migraciones Flyway:** `<ms>/src/main/resources/db/migration/` → archivos `V1__...sql`, `V2__...sql`, `V3__...sql`.
- Config en `application.yml` → `spring.jpa.hibernate.ddl-auto: validate` y `spring.flyway`.

**Cómo demostrarlo:** muestra los scripts `V*__*.sql` y, tras arrancar, que las tablas
existen en la base con sus datos.

---

## 9. 🐬 Base de datos MySQL + perfiles dev/prod

**Qué es:** la base de datos del sistema, con **dos configuraciones**: `dev` (local) y
`prod` (nube).

**Qué hace aquí:** cada MS guarda sus datos en MySQL. El perfil decide a qué base apunta.

**Dónde encontrarlo:**
- `<ms>/pom.xml` → driver `com.mysql:mysql-connector-j`.
- `<ms>/src/main/resources/application.yml` → documento multi-perfil:
  - **`dev`** → `jdbc:mysql://localhost:3306/vet_<modulo>_db` (Laragon, por defecto).
  - **`prod`** → datasource por variables `${SPRING_DATASOURCE_URL/USERNAME/PASSWORD}` (Aiven), Eureka desactivado.

**Cómo demostrarlo:** muestra los dos bloques de perfil en un `application.yml` y explica
que `dev` es local y `prod` usa variables de entorno secretas.

---

## 10. 🎲 DataFaker / DataLoader (datos de prueba)

**Qué es:** generación automática de datos falsos realistas para no partir con tablas
vacías.

**Qué hace aquí:** al arrancar, cada MS puebla sus tablas (de forma idempotente: no
duplica si ya hay datos).

**Dónde encontrarlo:**
- `<ms>/.../config/DataLoader.java` (implementa `CommandLineRunner`).
  - `ms-usuarios/.../config/DataLoader.java` → 20 dueños (con **RUT chileno válido**) y 30 mascotas con `net.datafaker.Faker`.
  - `ms-login/.../config/DataLoader.java` → cuentas con password **cifrada en BCrypt**.
- `<ms>/pom.xml` → dependencia `net.datafaker:datafaker`.

**Cómo demostrarlo:** arranca un servicio "limpio" y muestra que `GET /api/duenos`
ya devuelve datos generados.

---

## 11. 🔄 Comunicación entre microservicios (WebClient)

**Qué es:** un microservicio llamando a otro por HTTP (REST).

**Qué hace aquí:** **ms-citas** orquesta: al crear una cita, llama a **ms-usuarios** para
validar la mascota y a **ms-inventario** para descontar stock — y propaga el token JWT.

**Dónde encontrarlo:**
- `ms-citas/pom.xml` → `spring-boot-starter-webflux` (aporta `WebClient`).
- `ms-citas/.../config/WebClientConfig.java` → define los dos clientes + filtro que **propaga el header Authorization**.
- `ms-citas/.../client/UsuariosClient.java` → `obtenerMascota(id)` (GET a ms-usuarios).
- `ms-citas/.../client/InventarioClient.java` → `descontarStock(...)` (PUT a ms-inventario).
- `ms-citas/.../service/CitaService.java` → método `guardar()` que orquesta los 3 pasos.

**Cómo demostrarlo:** crea una cita con producto (`POST /api/citas`) y muestra que el
stock del producto bajó en ms-inventario.

---

## 12. 🐳 Docker + Docker Compose

**Qué es:** empaquetar cada servicio en un contenedor y levantar todo junto con un
comando.

**Qué hace aquí:** cada módulo tiene un `Dockerfile` (multi-stage: compila y luego crea
imagen liviana). `docker-compose.yml` levanta base de datos + los 6 servicios en orden.

**Dónde encontrarlo:**
- `<cada-módulo>/Dockerfile` → 2 etapas (Maven para compilar → JRE 21 para ejecutar).
- `docker-compose.yml` (raíz) → servicio `mysql:8.4`, eureka, los 4 MS y el gateway, con `depends_on` + healthchecks y variables de entorno.
- `docker/init-db.sql` → crea las 4 bases al iniciar el contenedor MySQL.

**Cómo demostrarlo:** `docker compose up --build` levanta todo; muéstralo corriendo y
los servicios registrados en Eureka.

---

## 13. ☁️ Despliegue PaaS (Render + Aiven)

**Qué es:** publicar el servicio en internet (Render) con la base de datos en la nube
(Aiven), sin servidor propio.

**Qué hace aquí:** un *blueprint* despliega **ms-usuarios** como servicio web Docker en
Render, con perfil `prod` y la base MySQL de Aiven (credenciales secretas).

**Dónde encontrarlo:**
- `render.yaml` (raíz) → tipo `web`, `runtime: docker`, `rootDir: ms-usuarios`, variables `SPRING_PROFILES_ACTIVE: prod` y `SPRING_DATASOURCE_*` con `sync: false` (secretas).

**Cómo demostrarlo:** muestra el `render.yaml` y, si está desplegado, la URL pública de
Render sirviendo el Swagger de ms-usuarios contra Aiven.

---

# 🧭 Tabla resumen: tema → dónde buscarlo

| Tema rúbrica | Archivo/clase clave |
|---|---|
| API Gateway | `api-gateway/src/main/resources/application.yml` (routes) |
| Eureka | `eureka-server/.../EurekaServerApplication.java` (`@EnableEurekaServer`) + http://localhost:8761 |
| Swagger | `<ms>/.../config/SwaggerConfig.java` + `@Schema` en los DTOs |
| HATEOAS | `<ms>/.../controller/*ControllerV2.java` + `.../assembler/*ModelAssembler.java` |
| JWT | `<ms>/.../security/` (SecurityConfig, JwtAuthFilter, JwtService) + `AuthController` (login) |
| Testing | `<ms>/src/test/java/...` (`*ServiceTest`, `*ControllerTest`) |
| JaCoCo | `<ms>/pom.xml` (plugin) → reporte en `target/site/jacoco/index.html` |
| JPA + Flyway | `<ms>/.../model/`, `.../repository/`, `.../resources/db/migration/V*.sql` |
| MySQL dev/prod | `<ms>/.../resources/application.yml` (perfiles) + driver en `pom.xml` |
| DataFaker | `<ms>/.../config/DataLoader.java` |
| WebClient | `ms-citas/.../client/` + `.../config/WebClientConfig.java` |
| Docker | `<módulo>/Dockerfile` + `docker-compose.yml` |
| Render/Aiven | `render.yaml` |

> 💡 Truco para la evaluación: ten abierto este archivo y el Swagger de cada servicio.
> Para cada punto de la rúbrica, abre el archivo indicado **y** muéstralo funcionando.
