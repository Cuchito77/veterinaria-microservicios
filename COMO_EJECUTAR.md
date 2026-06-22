# 🚀 Cómo ejecutar el proyecto desde 0

Guía paso a paso para levantar el **Sistema Veterinaria (Microservicios)** desde cero.
Hay **dos formas**: con **Docker** (la más fácil, todo automático) o **local con Laragon + IntelliJ**
(la que se usa para programar y depurar). Elige una.

---

## 🧩 ¿Qué es este sistema? (mapa rápido)

Son **6 aplicaciones** que trabajan juntas:

| Servicio | Puerto | Para qué sirve |
|---|---|---|
| **eureka-server** | 8761 | "Guía telefónica": cada servicio se registra aquí |
| **api-gateway** | 8080 | Puerta de entrada única a todo el sistema |
| **ms-login** | 8084 | Login y cuentas de usuario; **emite el token JWT** |
| **ms-usuarios** | 8081 | Dueños y mascotas |
| **ms-inventario** | 8083 | Productos y stock |
| **ms-citas** | 8082 | Citas (llama a usuarios e inventario) |

Base de datos: **MySQL** (4 bases, una por servicio de negocio).

---

## ✅ Requisitos previos

Necesitas tener instalado:

- **Java 21 (JDK 21)** — verifica con `java -version`
- **IntelliJ IDEA** (trae Maven incluido) — o Maven por separado
- **Laragon** (incluye MySQL) — para la base de datos local
- *(Opcional, solo para la Forma A)* **Docker Desktop**

---

# 🟢 FORMA A — Con Docker (la más fácil)

> Levanta TODO el sistema (base de datos + los 6 servicios) con **un solo comando**.
> No necesitas Laragon ni crear bases a mano: Docker hace todo.

### Paso 1 — Abre Docker Desktop
Espera a que diga "running" (la ballenita verde).

### Paso 2 — Abre una terminal en la carpeta `Microservicios`
```bash
cd ruta/al/proyecto/Microservicios
```

### Paso 3 — Levanta todo
```bash
docker compose up --build
```
La primera vez tarda varios minutos (compila cada servicio dentro de Docker).
Cuando veas que los servicios dicen *"Started ...Application"*, ya está arriba.

El orden lo maneja Docker solo: **MySQL → Eureka → los 4 microservicios → Gateway**.

### Paso 4 — Abre el sistema en el navegador
- 🚪 **Entrada única (Gateway):** http://localhost:8080
- 📋 **Panel de Eureka** (ver servicios registrados): http://localhost:8761
- 📚 **Swagger** de cada servicio (ver el punto "Probar la API" más abajo)

### Para apagar todo
```bash
docker compose down
```
> Los datos quedan guardados. Para borrarlos también: `docker compose down -v`.

---

# 🔵 FORMA B — Local con Laragon + IntelliJ (para programar)

### Paso 1 — Inicia MySQL en Laragon
Abre **Laragon** → botón **"Iniciar todo"** (o "Start All").
Esto enciende MySQL en `localhost:3306` (usuario `root`, **sin contraseña**).

### Paso 2 — Crea las 4 bases de datos vacías
En Laragon → botón **"Base de datos"** (abre el cliente SQL), o en una terminal,
ejecuta:
```sql
CREATE DATABASE vet_login_db;
CREATE DATABASE vet_usuarios_db;
CREATE DATABASE vet_inventario_db;
CREATE DATABASE vet_citas_db;
```
> ⚠️ Solo creas las bases **vacías**. Las **tablas y los datos** los crea
> **Flyway** automáticamente la primera vez que arranca cada servicio.

### Paso 3 — Abre el proyecto en IntelliJ
Abre la carpeta `Microservicios`. IntelliJ detecta los 6 módulos Maven y
descarga las dependencias (espera a que termine, abajo a la derecha).

### Paso 4 — Arranca los servicios EN ESTE ORDEN
Es importante respetar el orden (cada uno depende del anterior):

1. **eureka-server** → ejecuta `EurekaServerApplication`
2. **ms-login** → ejecuta `LoginApplication`
3. **ms-usuarios** → ejecuta `UsuariosApplication`
4. **ms-inventario** → ejecuta `InventarioApplication`
5. **ms-citas** → ejecuta `CitasApplication`
6. **api-gateway** → ejecuta `ApiGatewayApplication`

> En IntelliJ: abre la clase `*Application` de cada módulo y pulsa el ▶️ verde.
> Espera a que cada uno diga *"Started ...Application"* antes de seguir con el siguiente.

> 💡 Estás usando el **perfil `dev`** (es el que viene por defecto), que apunta a
> tu MySQL local de Laragon. No tienes que configurar nada más.

### Alternativa por terminal (sin IntelliJ)
Desde la carpeta de cada módulo:
```bash
mvn spring-boot:run
```

---

# 🧪 Probar la API (Swagger)

Cada servicio de negocio tiene su documentación interactiva **Swagger**:

| Servicio | Swagger UI |
|---|---|
| ms-usuarios | http://localhost:8081/doc/swagger-ui.html |
| ms-citas | http://localhost:8082/doc/swagger-ui.html |
| ms-inventario | http://localhost:8083/doc/swagger-ui.html |
| ms-login | http://localhost:8084/doc/swagger-ui.html |

### Casi todos los endpoints piden un **token JWT**. Cómo obtenerlo:

**Opción 1 — Login real (recomendado):**
1. Entra al Swagger de **ms-login** → endpoint `POST /api/auth/login`.
2. **Try it out** → usa una cuenta de prueba:
   | usuario | contraseña | rol |
   |---|---|---|
   | `admin` | `admin123` | ADMIN |
   | `vet` | `vet123` | VETERINARIO |
   | `recepcion` | `recep123` | RECEPCION |
3. **Execute** → copia el `token` que devuelve la respuesta.

**Opción 2 — Token de prueba ya listo (rol ADMIN, válido hasta el año 2100):**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbCI6IkFETUlOIiwiZXhwIjo0MTAyNDQ0ODAwfQ.QlK9ZgClp1kGyv2UjM18eotKWFyuYE47LhERosFggVU
```

### Usar el token en Swagger
1. En el Swagger del servicio que quieras probar, pulsa el botón **Authorize** 🔒 (arriba a la derecha).
2. Pega **solo el token** (sin escribir "Bearer " delante).
3. **Authorize** → **Close**.
4. Ahora cualquier endpoint → **Try it out** → **Execute** → ¡devuelve datos! 🎉

> En **ms-usuarios** el token queda **guardado** (no lo vuelve a pedir aunque recargues).

---

# 🔗 Probar el flujo completo (crear una cita)

`ms-citas` es el orquestador: al crear una cita, valida la mascota en `ms-usuarios`
y descuenta stock en `ms-inventario`. Para que funcione, esos dos deben estar arriba.

1. Autorízate (token) en el Swagger de **ms-citas**.
2. `POST /api/citas` con, por ejemplo:
   ```json
   { "mascotaId": 1, "motivo": "Vacunacion anual",
     "fecha": "2026-07-15", "hora": "10:30",
     "productoId": 1, "cantidadProducto": 2 }
   ```
3. El sistema valida la mascota, descuenta el stock del producto y guarda la cita.

---

# ☁️ (Opcional) Ejecutar ms-usuarios contra la base en la nube (Aiven / perfil prod)

Para probar el perfil **prod** (base de datos MySQL de Aiven en vez de local),
en PowerShell:
```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:SPRING_DATASOURCE_URL="jdbc:mysql://<host-aiven>:<puerto>/defaultdb?sslMode=REQUIRED"
$env:SPRING_DATASOURCE_USERNAME="avnadmin"
$env:SPRING_DATASOURCE_PASSWORD="<tu-password-de-aiven>"
mvn -f ms-usuarios/pom.xml spring-boot:run
```
> El perfil `prod` desactiva Eureka y toma la base por variables de entorno.
> ⚠️ La URL debe ser `jdbc:mysql://` con `sslMode=REQUIRED` (no `jdbc:mariadb`).

---

# 🆘 Problemas comunes

| Problema | Causa / Solución |
|---|---|
| `Communications link failure` / no conecta a la BD | MySQL (Laragon) no está iniciado, o no creaste las 4 bases. |
| `Connection refused: localhost:8761` | Eureka no está arriba. Inícialo **primero**. (Es solo un warning, no impide arrancar). |
| Respuesta **403** en un endpoint | Falta el token. Pulsa **Authorize** y pega el token (sin "Bearer "). |
| Puerto ocupado (`Port 8081 already in use`) | Ya tienes ese servicio corriendo. Ciérralo antes de relanzar. |
| Docker tarda mucho la 1ª vez | Normal: compila cada servicio dentro del contenedor. Las siguientes veces es rápido. |

---

## 📌 Resumen ultra-rápido

- **Solo quiero verlo funcionar** → Forma A: `docker compose up --build`
- **Quiero programar/depurar** → Forma B: Laragon + crear 4 BDs + arrancar en orden (Eureka primero)
- **Probar endpoints** → Swagger de cada servicio + botón Authorize con el token
