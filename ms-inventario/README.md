# MS-INVENTARIO — Microservicio de Productos (Veterinaria)

Microservicio que gestiona el **inventario de productos** de la veterinaria
(vacunas, alimentos, medicamentos, accesorios) y su stock. Expone un endpoint
de descuento de stock que es consumido por ms-citas.

## Integrantes
- (Nombre Apellido 1) — Equipo N° __
- (Nombre Apellido 2) — Equipo N° __

## Datos técnicos
- **Java:** 21
- **Spring Boot:** 4.0.6
- **Puerto:** `8083`
- **Base de datos:** `vet_inventario_db` (MariaDB / Laragon)
- **Persistencia:** JPA + Hibernate (ddl-auto=validate) + Flyway

## Funcionalidades implementadas
- CRUD completo de productos (`/api/productos`).
- Consulta de productos por categoría.
- Regla de negocio principal: **descuento de stock** validando que no quede
  negativo (lanza error 400 si el stock es insuficiente).
- Validaciones con Bean Validation, manejo de errores con `@RestControllerAdvice`
  y logs estructurados con SLF4J.

## Pasos para ejecutar
1. Abrir Laragon e iniciar MariaDB (usuario `root`, sin contraseña).
2. Crear la base de datos vacía:
   ```sql
   CREATE DATABASE vet_inventario_db;
   ```
3. Abrir el proyecto en IntelliJ IDEA.
4. Ejecutar `InventarioApplication`. Flyway creará la tabla e insertará datos.
5. El servicio queda disponible en `http://localhost:8083`.

## Endpoints principales
| Método | Ruta                                 | Descripción                  |
|--------|--------------------------------------|------------------------------|
| GET    | /api/productos                       | Listar productos             |
| GET    | /api/productos/{id}                  | Obtener producto por id      |
| GET    | /api/productos/categoria/{categoria} | Productos por categoría      |
| POST   | /api/productos                       | Crear producto               |
| PUT    | /api/productos/{id}                  | Actualizar producto          |
| PUT    | /api/productos/{id}/descontar        | Descontar stock (usado por citas) |
| DELETE | /api/productos/{id}                  | Eliminar producto            |
