# MS-LOGIN — Microservicio de Autenticación (Veterinaria)

Microservicio de cuentas de acceso al sistema de la veterinaria. Implementa
**autenticación simple** (usuario + contraseña contra la BD, sin token JWT) y
el CRUD de cuentas de usuario con roles diferenciados.

## Integrantes
- (Nombre Apellido 1) — Equipo N° __
- (Nombre Apellido 2) — Equipo N° __

## Datos técnicos
- **Java:** 21
- **Spring Boot:** 4.0.6
- **Puerto:** `8080`
- **Base de datos:** `vet_login_db` (MySQL / Laragon)
- **Persistencia:** JPA + Hibernate (ddl-auto=validate) + Flyway

## Funcionalidades implementadas
- Login simple: valida `username` + `password` y devuelve el rol del usuario.
- CRUD completo de cuentas (`/api/usuarios`).
- Roles: `ADMIN`, `VETERINARIO`, `RECEPCION`.
- Validación de cuenta inactiva.
- Validaciones con Bean Validation, manejo de errores con `@RestControllerAdvice`
  y logs estructurados con SLF4J.

## Pasos para ejecutar
1. Abrir Laragon e iniciar MySQL (usuario `root`, sin contraseña).
2. Crear la base de datos vacía:
   ```sql
   CREATE DATABASE vet_login_db;
   ```
3. Abrir el proyecto en IntelliJ IDEA.
4. Ejecutar `LoginApplication`. Flyway creará las tablas e insertará los datos.
5. El servicio queda disponible en `http://localhost:8080`.

## Cuentas de prueba (datos iniciales)
| username  | password  | rol         | activo |
|-----------|-----------|-------------|--------|
| admin     | admin123  | ADMIN       | sí     |
| vet       | vet123    | VETERINARIO | sí     |
| recepcion | recep123  | RECEPCION   | sí     |
| inactivo  | test123   | RECEPCION   | no     |

## Endpoints principales
| Método | Ruta                  | Descripción                |
|--------|-----------------------|----------------------------|
| POST   | /api/auth/login       | Autenticar usuario         |
| GET    | /api/usuarios         | Listar cuentas             |
| GET    | /api/usuarios/{id}    | Obtener cuenta por id      |
| POST   | /api/usuarios         | Crear cuenta               |
| PUT    | /api/usuarios/{id}    | Actualizar cuenta          |
| DELETE | /api/usuarios/{id}    | Eliminar cuenta            |
