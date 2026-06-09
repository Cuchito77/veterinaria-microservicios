# Sistema Veterinaria — Arquitectura de Microservicios

Proyecto Semestral de la asignatura Desarrollo FullStack 1 (DSY1103).
Sistema de gestión para una veterinaria basado en microservicios con Spring Boot.

## Integrantes — Equipo N° 8
- Cusi Ramirez B.
- Santiago Garcia-Huidobro S.

## Tecnologías
- Java 21 · Spring Boot 4.0.6
- MariaDB (Laragon) · JPA + Hibernate + Flyway
- Comunicación entre microservicios: WebClient

## Microservicios
| Microservicio | Puerto | Base de datos     | Responsabilidad                          |
|---------------|--------|-------------------|------------------------------------------|
| ms-login      | 8080   | vet_login_db      | Autenticación y cuentas de usuario       |
| ms-usuarios   | 8081   | vet_usuarios_db   | Dueños y mascotas                        |
| ms-citas      | 8082   | vet_citas_db      | Citas (orquesta usuarios e inventario)   |
| ms-inventario | 8083   | vet_inventario_db | Productos y stock                        |

## Cómo ejecutar
1. Iniciar MariaDB en Laragon (usuario root, sin contraseña).
2. Crear las 4 bases de datos vacías:
```sql
   CREATE DATABASE vet_login_db;
   CREATE DATABASE vet_usuarios_db;
   CREATE DATABASE vet_citas_db;
   CREATE DATABASE vet_inventario_db;
```
3. Abrir cada microservicio en IntelliJ. Flyway crea las tablas e inserta datos al arrancar.
4. Levantar en este orden: ms-login → ms-usuarios → ms-inventario → ms-citas.

## Comunicación entre microservicios
ms-citas consume a ms-usuarios (valida la mascota) y a ms-inventario
(descuenta stock) mediante WebClient al crear una cita.
