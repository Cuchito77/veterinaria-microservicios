# Sistema Veterinaria — Arquitectura de Microservicios

Proyecto Semestral de la asignatura Desarrollo FullStack 1 (DSY1103).
Sistema de gestión para una veterinaria basado en microservicios con Spring Boot.

## Integrantes — Equipo N° 8
- Cusi Ramirez B.
- Santiago Garcia-Huidobro S.

## Tecnologías
- Java 21 · Spring Boot 3.4.5 · Spring Cloud 2024.0.1 (Gateway + Eureka)
- MySQL (Laragon / Aiven) · JPA + Hibernate + Flyway
- Spring Security + JWT · Swagger/OpenAPI · HATEOAS
- Comunicación entre microservicios: WebClient
- Docker + Docker Compose

## Microservicios
| Microservicio | Puerto | Base de datos     | Responsabilidad                          |
|---------------|--------|-------------------|------------------------------------------|
| api-gateway   | 8080   | —                 | Punto único de entrada                   |
| eureka-server | 8761   | —                 | Registro y descubrimiento de servicios   |
| ms-usuarios   | 8081   | vet_usuarios_db   | Dueños y mascotas                        |
| ms-citas      | 8082   | vet_citas_db      | Citas (orquesta usuarios e inventario)   |
| ms-inventario | 8083   | vet_inventario_db | Productos y stock                        |
| ms-login      | 8084   | vet_login_db      | Autenticación y cuentas de usuario (JWT) |

## Rutas principales del API Gateway

Todo el sistema se consume por la entrada única del Gateway: **http://localhost:8080**.
El Gateway enruta cada solicitud al microservicio correspondiente (descubierto vía Eureka con `lb://`):

| Ruta (prefijo) | Microservicio destino | Descripción |
|---|---|---|
| `/api/auth/**` | ms-login | Login / autenticación (emite el JWT) |
| `/api/usuarios/**`, `/api/v2/usuarios/**` | ms-login | Cuentas de usuario |
| `/api/duenos/**`, `/api/v2/duenos/**` | ms-usuarios | Dueños |
| `/api/mascotas/**`, `/api/v2/mascotas/**` | ms-usuarios | Mascotas |
| `/api/productos/**`, `/api/v2/productos/**` | ms-inventario | Productos y stock |
| `/api/citas/**`, `/api/v2/citas/**` | ms-citas | Citas |

> Las rutas `/api/v2/**` corresponden a la API HATEOAS (respuestas HAL con enlaces).
> Configuración de las rutas: `api-gateway/src/main/resources/application.yml`.

## Cómo ejecutar con Docker (recomendado)
Con Docker Desktop corriendo, en la raíz del proyecto:
```bash
docker compose up --build
```
Eso levanta TODO el sistema en orden: MySQL (crea las 4 BD con
`docker/init-db.sql`) → Eureka → los 4 microservicios → API Gateway.

- Entrada única por el Gateway: http://localhost:8080
- Dashboard de Eureka: http://localhost:8761
- Swagger de cada servicio: http://localhost:PUERTO/doc/swagger-ui.html
- MySQL del contenedor queda en el puerto **3307** (no choca con Laragon)

Para detener todo: `docker compose down` (los datos persisten en un volumen;
para borrarlos también: `docker compose down -v`).

Cada microservicio tiene un `Dockerfile` multi-stage: la etapa 1 compila el
jar con Maven dentro del contenedor y la etapa 2 genera una imagen liviana
solo con el JRE 21. Las variables de entorno del `docker-compose.yml`
sobreescriben `application.properties`, por lo que la configuración local
con Laragon sigue funcionando sin cambios.

## Cómo ejecutar de forma local (Laragon + IntelliJ)
1. Iniciar MySQL en Laragon (usuario root, sin contraseña).
2. Crear las 4 bases de datos vacías:
```sql
   CREATE DATABASE vet_login_db;
   CREATE DATABASE vet_usuarios_db;
   CREATE DATABASE vet_citas_db;
   CREATE DATABASE vet_inventario_db;
```
3. Abrir cada microservicio en IntelliJ. Flyway crea las tablas e inserta datos al arrancar.
4. Levantar en este orden: eureka-server → ms-login → ms-usuarios → ms-inventario → ms-citas → api-gateway.

## Comunicación entre microservicios
ms-citas consume a ms-usuarios (valida la mascota) y a ms-inventario
(descuenta stock) mediante WebClient al crear una cita.
