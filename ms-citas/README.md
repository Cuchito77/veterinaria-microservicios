# MS-CITAS — Microservicio de Citas (Veterinaria)

Microservicio **central y orquestador** del sistema. Gestiona las citas médicas
de las mascotas y se comunica con otros dos microservicios mediante **WebClient**:

- **ms-usuarios** (8081): valida que la mascota exista antes de crear la cita.
- **ms-inventario** (8083): descuenta el stock del producto usado en la cita.

## Integrantes
- (Nombre Apellido 1) — Equipo N° __
- (Nombre Apellido 2) — Equipo N° __

## Datos técnicos
- **Java:** 21
- **Spring Boot:** 4.0.6
- **Puerto:** `8082`
- **Base de datos:** `vet_citas_db` (MariaDB / Laragon)
- **Persistencia:** JPA + Hibernate (ddl-auto=validate) + Flyway
- **Comunicación entre microservicios:** WebClient (spring-boot-starter-webflux)

## Diseño importante (autonomía de datos)
La tabla `citas` **no** tiene claves foráneas hacia mascotas ni productos,
porque esas entidades viven en **otras bases de datos** (otros microservicios).
Solo se guardan los IDs de referencia (`mascota_id`, `producto_id`) y la
existencia se valida en tiempo de ejecución mediante WebClient. Esto respeta
el principio de independencia de datos de la arquitectura de microservicios.

## Flujo al crear una cita (POST /api/citas)
1. Llama a ms-usuarios `GET /api/mascotas/{id}` → valida que la mascota exista.
2. Si la cita incluye un producto, llama a ms-inventario
   `PUT /api/productos/{id}/descontar` → descuenta el stock.
3. Solo entonces guarda la cita en `vet_citas_db` con estado `PROGRAMADA`.

## Requisito de ejecución
**Para que el flujo completo funcione, ms-usuarios (8081) y ms-inventario (8083)
deben estar corriendo antes de crear citas con producto.**

## Pasos para ejecutar
1. Abrir Laragon e iniciar MariaDB (usuario `root`, sin contraseña).
2. Crear la base de datos vacía:
   ```sql
   CREATE DATABASE vet_citas_db;
   ```
3. Abrir el proyecto en IntelliJ IDEA.
4. Levantar primero ms-usuarios y ms-inventario.
5. Ejecutar `CitasApplication`. Flyway creará la tabla e insertará datos.
6. El servicio queda disponible en `http://localhost:8082`.

## Endpoints principales
| Método | Ruta                            | Descripción                          |
|--------|---------------------------------|--------------------------------------|
| GET    | /api/citas                      | Listar citas                         |
| GET    | /api/citas/{id}                 | Obtener cita por id                  |
| GET    | /api/citas/mascota/{mascotaId}  | Citas de una mascota                 |
| POST   | /api/citas                      | Crear cita (valida mascota + stock)  |
| PUT    | /api/citas/{id}                 | Actualizar cita                      |
| PUT    | /api/citas/{id}/estado?valor=   | Cambiar estado (PROGRAMADA/ATENDIDA/CANCELADA) |
| DELETE | /api/citas/{id}                 | Eliminar cita                        |
