# Migración de JDBC a API REST — PawLink JavaFX

## ¿Qué se hizo y por qué?

La aplicación JavaFX antes accedía a la base de datos **directamente** mediante JDBC
(una conexión SQL desde el código Java al PostgreSQL). El objetivo era cambiar eso para
que la app hable con una **API REST** (el backend Spring Boot de PawLink) en lugar de
conectarse directamente a la base de datos.

**Antes:**
```
JavaFX ──(JDBC / SQL)──► PostgreSQL
```

**Después:**
```
JavaFX ──(HTTP / JSON)──► Spring Boot API ──► PostgreSQL (Supabase)
```

Esto es más profesional porque:
- La app no necesita saber nada de SQL ni de la base de datos
- Si cambia la base de datos, el frontend no se toca
- Es el patrón real que se usa en empresas (arquitectura cliente-servidor)

---

## Archivos modificados o creados

### 1. `PawLinkClient.java` ← **NUEVO**
**Ruta:** `app/src/main/java/com/javafx/proyecto/bbdd/PawLinkClient.java`

Este es el archivo más importante de la migración. Es una clase que centraliza
**todas las llamadas HTTP a la API**. Antes de existir este archivo, habría que
escribir el código HTTP en cada sitio que lo necesitase.

#### ¿Cómo funciona?

Usa `HttpClient` de Java 11 (no hay dependencias externas) y `ObjectMapper` de
Jackson (que ya estaba en el proyecto) para convertir objetos Java a JSON y viceversa.

Tiene 4 métodos privados que hacen el trabajo sucio HTTP:
```java
private static HttpResponse<String> get(String path)    // GET
private static HttpResponse<String> post(String path, Object body)  // POST
private static HttpResponse<String> put(String path, Object body)   // PUT
private static HttpResponse<String> delete(String path)             // DELETE
```

Todos comprueban si el código de respuesta HTTP es >= 400 (error) y lanzan una
excepción con el mensaje del servidor. Así el código que los llama puede capturar
los errores con `try/catch`.

Encima de esos 4 métodos hay los métodos públicos concretos para cada entidad:

**Mascotas:**
```java
getMascotas()            // GET /api/mascotas  → devuelve lista de mapas JSON
crearMascota(body)       // POST /api/mascotas
actualizarMascota(id, body)  // PUT /api/mascotas/{id}
eliminarMascota(id)      // DELETE /api/mascotas/{id}
```

**Alquileres:**
```java
getAlquileres()          // GET /api/alquileres → devuelve lista de mapas JSON
crearAlquiler(body)      // POST /api/alquileres
actualizarAlquiler(id, body) // PUT /api/alquileres/{id}
eliminarAlquiler(id)     // DELETE /api/alquileres/{id}
```

Los métodos GET devuelven `List<Map<String, Object>>` porque la respuesta JSON
es un array de objetos, y Jackson lo convierte exactamente a eso: una lista de
mapas clave-valor.

---

### 2. `Mascota.java` ← **MODIFICADO**
**Ruta:** `app/src/main/java/com/javafx/proyecto/modelo/Mascota.java`

Se añadieron dos campos que antes no existían porque con JDBC no hacían falta,
pero con la API sí son necesarios:

- **`idCentro`** (`Integer`): El ID del centro veterinario al que pertenece la mascota.
  La API requiere enviarlo en cada PUT para actualizar una mascota. Sin él, la API
  devolvería error.
- **`foto`** (`String`): La URL de la foto de la mascota. La API también lo requiere
  en el body del PUT.

El modelo sigue siendo un POJO simple (clase Java con campos privados y getters),
sin anotaciones especiales. El mapeo JSON lo hace `PawLinkClient` manualmente.

---

### 3. `AdopcionTabla.java` ← **MODIFICADO**
**Ruta:** `app/src/main/java/com/javafx/proyecto/modelo/AdopcionTabla.java`

Se añadieron dos campos:
- **`idMascota`** (`Integer`): El ID de la mascota en la BD. Necesario para poder
  enviar el body correcto en las llamadas PUT/POST a la API de alquileres.
- **`idVoluntario`** (`Integer`): El ID del voluntario. Por la misma razón.

Antes, con JDBC, se trabajaba solo con los nombres (strings) porque se obtenían
directamente del JOIN SQL. Con la API, los nombres vienen en los campos
`nombreMascota` y `nombreVoluntario` del DTO de respuesta, pero para editar
o crear registros necesitas los IDs numéricos.

---

### 4. `MascotaCrudController.java` ← **REESCRITO**
**Ruta:** `app/src/main/java/com/javafx/proyecto/controlador/MascotaCrudController.java`

Este es el controlador de la pestaña de mascotas. Se sustituyó completamente
la capa de acceso a datos.

#### `cargarDatos()` — antes vs después

**Antes (JDBC):**
```java
// Abría conexión a BD, ejecutaba SQL, leía ResultSet fila a fila
Connection conn = ConexionBBDD.getConexion();
PreparedStatement ps = conn.prepareStatement("SELECT * FROM Mascotas");
ResultSet rs = ps.executeQuery();
while (rs.next()) {
    listaMascotas.add(new Mascota(rs.getInt("id_mascota"), ...));
}
```

**Después (API REST):**
```java
List<Map<String, Object>> mascotas = PawLinkClient.getMascotas();
for (Map<String, Object> m : mascotas) {
    int id = ((Number) m.get("idMascota")).intValue();
    String nombre = (String) m.get("nombre");
    // ... se construye el objeto Mascota con los datos del JSON
    listaMascotas.add(new Mascota(id, nombre, ...));
}
```

Los campos del Map corresponden a los que devuelve la API en el JSON
(por ejemplo `"idMascota"`, `"nombre"`, `"disponibleAlquiler"`, etc.).

Hay un detalle: `disponibleAlquiler` es un `Integer` (0 o 1) en la API, pero
en el modelo Java es un `Boolean`. La conversión se hace así:
```java
boolean disponible = dispObj instanceof Number && ((Number) dispObj).intValue() == 1;
```

#### Crear, editar y eliminar

En **crear** y **editar**, el formulario recoge los datos del usuario y los
mete en un `Map<String, Object>` con exactamente los nombres de campos que
espera la API (`idCentro`, `nombre`, `especie`, `raza`, `fechaNacimiento`,
`peso`, `estadoSalud`, `disponibleAlquiler`, `foto`, `notas`). Ese mapa se
pasa a `PawLinkClient.crearMascota(body)` o `actualizarMascota(id, body)`.

En **eliminar**, simplemente se llama a `PawLinkClient.eliminarMascota(id)`.

#### Menú contextual — "Cambiar disponibilidad"

Se añadió una opción nueva en el menú contextual (clic derecho sobre una mascota)
que permite activar/desactivar manualmente la disponibilidad. Hace un PUT a la
API con el mismo body completo pero con `disponibleAlquiler` invertido.

---

### 5. `AdopcionCrudController.java` ← **REESCRITO**
**Ruta:** `app/src/main/java/com/javafx/proyecto/controlador/AdopcionCrudController.java`

Mismo patrón que mascotas pero para los alquileres.

#### `cargarDatos()` — antes vs después

**Antes (JDBC):**
```sql
SELECT a.id_alquiler, m.nombre AS mascota, u.nombre AS voluntario,
       a.fecha_inicio, a.fecha_fin, a.estado, a.calificacion
FROM Alquileres a
JOIN Mascotas m ON a.id_mascota = m.id_mascota
JOIN Usuarios u ON a.id_voluntario = u.id_usuario
```

**Después (API REST):**
```java
List<Map<String, Object>> alquileres = PawLinkClient.getAlquileres();
for (Map<String, Object> a : alquileres) {
    int id = ((Number) a.get("idAlquiler")).intValue();
    String mascota = (String) a.get("nombreMascota");   // el JOIN lo hace la API
    String voluntario = (String) a.get("nombreVoluntario");
    // ...
}
```

La API devuelve `nombreMascota` y `nombreVoluntario` ya resueltos (el JOIN SQL
lo hace el backend, no nosotros). También devuelve `idMascota` e `idVoluntario`
para poder hacer las operaciones posteriores.

#### `nuevo()` — lógica de disponibilidad

Cuando se crea un nuevo alquiler ocurren **dos llamadas API**:

1. `POST /api/alquileres` — crea el alquiler
2. `PUT /api/mascotas/{id}` — actualiza la mascota con `disponibleAlquiler: 0`

Esto es necesario porque la base de datos **no tiene ningún trigger automático**
que actualice `disponible_alquiler` al crear un alquiler. Son dos tablas
independientes en el SQL, así que hay que actualizar ambas a mano.

Las dos llamadas están en `try/catch` separados: si falla la creación del alquiler
se para y no se intenta actualizar la mascota. Si el alquiler se creó bien pero
falla la actualización de disponibilidad, se avisa al usuario pero no se deshace
el alquiler.

```java
// Paso 1: crear el alquiler
try {
    PawLinkClient.crearAlquiler(body);
} catch (Exception e) {
    UIUtils.mostrarInfo("Error API", "No se pudo crear el alquiler:\n" + e.getMessage());
    return; // se para aquí
}

// Paso 2: marcar la mascota como no disponible
try {
    PawLinkClient.actualizarMascota(m.getId(), bodyMascota);
} catch (Exception e) {
    UIUtils.mostrarInfo("Aviso", "Alquiler creado, pero no se pudo actualizar la disponibilidad:\n" + e.getMessage());
    // no se para, se continúa igualmente
}
```

#### `nuevo()` — filtro de mascotas disponibles

El combo desplegable de selección de mascota **solo muestra las mascotas con
`disponible = true`**. Esto se consigue con un `FilteredList` de JavaFX:

```java
ObservableList<Mascota> mascotasDisponibles = listaMascotas.filtered(Mascota::getDisponible);
UIUtils.configurarAutocompletado(comboMascota, mascotasDisponibles);
```

`filtered()` es un método de JavaFX que crea una vista filtrada de la lista
original. Como es una referencia al método `getDisponible()`, solo aparecen
las mascotas donde ese campo es `true`.

#### Menú contextual — "Cambiar a finalizado"

Se añadió una opción en el menú contextual para marcar rápidamente un alquiler
como `"finalizado"` sin tener que abrir el formulario de edición.

---

### 6. `PrincipalController.java` ← **MODIFICADO**
**Ruta:** `app/src/main/java/com/javafx/proyecto/controlador/PrincipalController.java`

Se hizo un cambio en cómo se inicializa `adopcionCtrl`. Antes el callback que
se le pasaba era solo `this::recargarDashboard` (actualizar los contadores del
panel principal). Pero eso no recargaba la lista de mascotas.

**Problema:** Al crear un alquiler, la mascota se marcaba como no disponible en
la BD a través de la API, pero la lista de mascotas en memoria (`listaMascotas`)
no se actualizaba. Por eso la mascota seguía apareciendo como disponible en la
tabla hasta que reiniciabas la app.

**Solución:** el callback ahora también llama a `mascotaCtrl.cargarDatos()` y
`mascotaCtrl.rellenarGraficaEspecies()`:

```java
// Antes:
adopcionCtrl = new AdopcionCrudController(..., this::recargarDashboard);

// Después:
adopcionCtrl = new AdopcionCrudController(
    ...,
    () -> {
        mascotaCtrl.cargarDatos();           // recarga mascotas desde la API
        mascotaCtrl.rellenarGraficaEspecies(); // actualiza la gráfica de barras
        recargarDashboard();                 // actualiza los contadores del panel
    }
);
```

---

## Resumen de las llamadas a la API que hace la aplicación

| Acción | Método HTTP | Endpoint |
|--------|-------------|----------|
| Ver mascotas | GET | `/api/mascotas` |
| Crear mascota | POST | `/api/mascotas` |
| Editar mascota | PUT | `/api/mascotas/{id}` |
| Eliminar mascota | DELETE | `/api/mascotas/{id}` |
| Ver alquileres | GET | `/api/alquileres` |
| Crear alquiler | POST | `/api/alquileres` |
| Editar alquiler | PUT | `/api/alquileres/{id}` |
| Eliminar alquiler | DELETE | `/api/alquileres/{id}` |
| Crear alquiler + marcar mascota | POST + PUT | `/api/alquileres` + `/api/mascotas/{id}` |

---

## Lo que NO se migró (sigue con JDBC)

Los **usuarios** siguen usando JDBC directamente porque la API de PawLink no
tiene (todavía) un endpoint de usuarios disponible para el frontend JavaFX.
El dashboard (contadores y últimos registros) también usa JDBC para los datos
de usuarios.

---

## Dependencias utilizadas

No se añadió ninguna dependencia nueva al `build.gradle`. Se usó:
- `java.net.http.HttpClient` — incluido en Java 11+, no hace falta añadir nada
- `com.fasterxml.jackson.databind.ObjectMapper` — ya estaba en el proyecto
  para otras cosas (serialización JSON)
