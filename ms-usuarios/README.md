# MS-USUARIOS — Microservicio de Dueños y Mascotas (Veterinaria)

Microservicio que gestiona los **dueños** (clientes de la veterinaria) y sus
**mascotas**. Otros microservicios (ms-citas) consultan este servicio para
validar la existencia de una mascota antes de operar.

## Integrantes
- (Nombre Apellido 1) — Equipo N° __
- (Nombre Apellido 2) — Equipo N° __

## Datos técnicos
- **Java:** 21
- **Spring Boot:** 4.0.6
- **Puerto:** `8081`
- **Base de datos:** `vet_usuarios_db` (MariaDB / Laragon)
- **Persistencia:** JPA + Hibernate (ddl-auto=validate) + Flyway

## Funcionalidades implementadas
- CRUD completo de dueños (`/api/duenos`) y mascotas (`/api/mascotas`).
- Relación `Dueno (1) --- (N) Mascota` con integridad referencial (FK).
- Regla de negocio: el RUT del dueño es único; la mascota debe pertenecer a un
  dueño existente.
- Endpoint de consulta de mascota por id, usado por ms-citas vía WebClient.
- Validaciones con Bean Validation, manejo de errores con `@RestControllerAdvice`
  y logs estructurados con SLF4J.

## Pasos para ejecutar
1. Abrir Laragon e iniciar MariaDB (usuario `root`, sin contraseña).
2. Crear la base de datos vacía:
   ```sql
   CREATE DATABASE vet_usuarios_db;
   ```
3. Abrir el proyecto en IntelliJ IDEA.
4. Ejecutar `UsuariosApplication`. Flyway creará las tablas e insertará datos.
5. El servicio queda disponible en `http://localhost:8081`.

## Endpoints principales
| Método | Ruta                          | Descripción                       |
|--------|-------------------------------|-----------------------------------|
| GET    | /api/duenos                   | Listar dueños                     |
| GET    | /api/duenos/{id}              | Obtener dueño por id              |
| POST   | /api/duenos                   | Crear dueño                       |
| PUT    | /api/duenos/{id}              | Actualizar dueño                  |
| DELETE | /api/duenos/{id}              | Eliminar dueño                    |
| GET    | /api/mascotas                 | Listar mascotas                   |
| GET    | /api/mascotas/{id}            | Obtener mascota por id            |
| GET    | /api/mascotas/dueno/{duenoId} | Mascotas de un dueño              |
| POST   | /api/mascotas                 | Crear mascota                     |
| PUT    | /api/mascotas/{id}            | Actualizar mascota                |
| DELETE | /api/mascotas/{id}            | Eliminar mascota                  |
