package com.example.clubdeportivog11

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clubdeportivog11.models.ActividadesDataClass
import com.example.clubdeportivog11.models.ClientesDataClass
import com.example.clubdeportivog11.models.PlanesDataClass

class ClubDBHelper (context: Context): SQLiteOpenHelper(context, "ClubDB", null, 1){


    // OnCreate - Crea mi base de datos
    override fun onCreate(db: SQLiteDatabase) {

        // Activar restricciones de clave foránea
        db.execSQL("PRAGMA foreign_keys = ON")

        // Creo todas las tablas
        db.execSQL("""
            CREATE TABLE Roles (
                RolUsuario INTEGER PRIMARY KEY,
                NombreRol TEXT NOT NULL
            )
        """.trimIndent())


        db.execSQL("""
            CREATE TABLE Cliente (
                ClienteID INTEGER PRIMARY KEY AUTOINCREMENT,
                Nombre TEXT NOT NULL,
                Apellido TEXT NOT NULL,
                TipoDocumento TEXT NOT NULL,
                NumeroDocumento INTEGER NOT NULL,
                FechaInscripcion TEXT NOT NULL,
                AptoFisico INTEGER DEFAULT 0,
                UNIQUE (TipoDocumento, NumeroDocumento)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE Socio (
                SocioID INTEGER PRIMARY KEY AUTOINCREMENT,
                ClienteID INTEGER NOT NULL,
                FechaVencimientoCuota TEXT,
                FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE NoSocio (
                NoSocioID INTEGER PRIMARY KEY AUTOINCREMENT,
                ClienteID INTEGER NOT NULL,
                FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE Usuario (
                UsuarioID INTEGER PRIMARY KEY AUTOINCREMENT,
                Nombre TEXT NOT NULL UNIQUE,
                Pass TEXT NOT NULL,
                ClienteID INTEGER,
                RolUsuario INTEGER NOT NULL,
                FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID),
                FOREIGN KEY (RolUsuario) REFERENCES Roles(RolUsuario)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE Actividad (
                ActividadID INTEGER PRIMARY KEY AUTOINCREMENT,
                Nombre TEXT NOT NULL,
                Precio REAL NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE PlanMembresia (
                MembresiaID INTEGER PRIMARY KEY AUTOINCREMENT,
                PlanMembresia TEXT NOT NULL,
                Meses INTEGER NOT NULL,
                Precio REAL NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE Pago (
                PagoID INTEGER PRIMARY KEY AUTOINCREMENT,
                ClienteID INTEGER NOT NULL,
                MembresiaID INTEGER,
                ActividadID INTEGER,
                Monto REAL NOT NULL,
                MetodoPago TEXT NOT NULL,
                Cuotas INTEGER NOT NULL DEFAULT 1,
                FechaPago TEXT NOT NULL,
                FOREIGN KEY (ClienteID) REFERENCES Cliente(ClienteID),
                FOREIGN KEY (MembresiaID) REFERENCES PlanMembresia(MembresiaID),
                FOREIGN KEY (ActividadID) REFERENCES Actividad(ActividadID)
            )
        """.trimIndent())

        // Cargo los parametros de los roles (administrador o cliente) y al administrador del sistema
        db.execSQL("INSERT INTO Roles (RolUsuario, NombreRol) VALUES (1, 'Administrador')")
        db.execSQL("INSERT INTO Roles (RolUsuario, NombreRol) VALUES (2, 'Cliente')")
        db.execSQL("INSERT INTO Usuario (Nombre, Pass, RolUsuario) VALUES ('admin', '1234', 1)")

        // Cargo los planes de membresia y las actividades disponibles
        // Actividades
        db.execSQL("""
            INSERT INTO Actividad (Nombre, Precio) VALUES
            ('Fútbol', 2000.00),
            ('Natación', 5000.00),
            ('Musculación', 1500.00),
            ('Tenis', 2000.00),
            ('Yoga', 3000.00),
            ('Pilates', 3200.00),
            ('Crossfit', 4500.00),
            ('Zumba', 2500.00),
            ('Spinning', 2800.00),
            ('Boxeo', 3500.00),
            ('Escalada', 6000.00),
            ('Básquet', 2200.00),
            ('Vóley', 2100.00),
            ('Funcional', 4000.00),
            ('Aqua Gym', 3300.00)
        """.trimIndent())

        // Membresias
        db.execSQL("""
            INSERT INTO PlanMembresia (PlanMembresia, Meses, Precio) VALUES
            ('Mensual', 1, 30000.00),
            ('Trimestral', 3, 80000.00),
            ('Semestral', 6, 150000.00),
            ('Anual', 12, 290000.00)
        """.trimIndent())


        // Cargo datos para mostrar de mis clientes socios y no socios
        // Clientes socios
        db.execSQL("""
            INSERT INTO Cliente (Nombre, Apellido, TipoDocumento, NumeroDocumento, FechaInscripcion, AptoFisico)
            VALUES 
            ('Facundo', 'Pérez', 'DNI', 30123456, '2025-06-01', 1),
            ('Camila', 'González', 'DNI', 29222333, '2025-06-02', 1),
            ('Lucas', 'Ramírez', 'DNI', 30999888, '2025-06-03', 1);
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Socio (ClienteID, FechaVencimientoCuota)
            VALUES
            (1, '2025-07-01'),
            (2, '2025-09-02'),
            (3, NULL);
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Pago (ClienteID, MembresiaID, ActividadID, Monto, MetodoPago, Cuotas, FechaPago)
            VALUES 
            (1, 1, NULL, 30000.00, 'Efectivo', 1, '2025-06-01'),
            (2, 2, NULL, 80000.00, 'Tarjeta de Crédito', 3, '2025-06-02');
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Usuario (Nombre, Pass, RolUsuario)
            VALUES 
            ('facundo', 'facundo123', 2),
            ('camila', 'camila123', 2),
            ('lucas', 'lucas123', 2);
        """.trimIndent())

        // Clientes no socios
        db.execSQL("""
            INSERT INTO Cliente (Nombre, Apellido, TipoDocumento, NumeroDocumento, FechaInscripcion, AptoFisico)
            VALUES 
            ('Sofía', 'Martínez', 'DNI', 33111222, '2025-06-01', 0),
            ('Matías', 'López', 'DNI', 34455666, '2025-06-02', 1);
        """.trimIndent())

        db.execSQL("""
            INSERT INTO NoSocio (ClienteID)
            VALUES
            (4),
            (5);
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Pago (ClienteID, MembresiaID, ActividadID, Monto, MetodoPago, Cuotas, FechaPago)
            VALUES 
            (4, NULL, 14, 4000.00, 'Efectivo', 1, '2025-06-01'),
            (5, NULL, 10, 3500.00, 'Efectivo', 1, '2025-06-02'),
            (4, NULL, 8, 2500.00, 'Efectivo', 1, '2025-06-05'),
            (5, NULL, 11, 6000.00, 'Efectivo', 1, '2025-06-05'),
            (4, NULL, 15, 3300.00, 'Efectivo', 1, '2025-06-06'),
            (5, NULL, 4, 2000.00, 'Efectivo', 1, '2025-06-07');
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Usuario (Nombre, Pass, RolUsuario)
            VALUES 
            ('sofia', 'sofia123', 2),
            ('matias', 'matias123', 2);
        """.trimIndent())

    }



    // OnUpgrade  -  En caso de nueva version para actualizar mi base de datos borro lo anterior
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Pago")
        db.execSQL("DROP TABLE IF EXISTS PlanMembresia")
        db.execSQL("DROP TABLE IF EXISTS Actividad")
        db.execSQL("DROP TABLE IF EXISTS Usuario")
        db.execSQL("DROP TABLE IF EXISTS NoSocio")
        db.execSQL("DROP TABLE IF EXISTS Socio")
        db.execSQL("DROP TABLE IF EXISTS Cliente")
        db.execSQL("DROP TABLE IF EXISTS Roles")
        onCreate(db)
    }


    // Funciones que involucran mi base de datos

    // LOGIN DE USUARIO
    fun login(nombre: String, pass: String): Boolean {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM Usuario WHERE Nombre=? AND Pass=?",
                arrayOf(nombre, pass)
            )
            val existe = cursor.count > 0
            cursor.close()
            return existe
        }

    // CREACION DE NUEVO USUARIO CON ROL DE CLIENTE
    fun crearUsuarioCliente(nombre: String, pass: String): Boolean {
        val db = writableDatabase
        return try {
            val stmt = db.compileStatement("INSERT INTO Usuario (Nombre, Pass, RolUsuario) VALUES (?, ?, 2)")
            stmt.bindString(1, nombre)
            stmt.bindString(2, pass)
            stmt.executeInsert()
            true
        } catch (e: Exception) {
            false
        }
    }

    // FUNCIONES PARA OBTENER LISTAS DE LA BASE DE DATOS
    // OBTENER PLANES
    fun obtenerPlanes(): List<PlanesDataClass> {
        val db = readableDatabase
        val lista = mutableListOf<PlanesDataClass>()
        val cursor = db.rawQuery("SELECT MembresiaId, PlanMembresia, Meses, Precio FROM PlanMembresia", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val meses = cursor.getInt(2)
                val precio = cursor.getDouble(3)
                lista.add(PlanesDataClass(id, nombre, meses, precio))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    // OBTENER ACTIVIDADES
    fun obtenerActividades(): List<ActividadesDataClass> {
        val db = readableDatabase
        val lista = mutableListOf<ActividadesDataClass>()
        val cursor = db.rawQuery("SELECT ActividadID, Nombre, Precio FROM Actividad", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val precio = cursor.getDouble(2)
                lista.add(ActividadesDataClass(id, nombre, precio))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    // OBTENER DATOS DE LOS CLIENTES
    fun obtenerClientes(filtro: String = "", soloCuotaVencida: Boolean = false): List<ClientesDataClass> {
        val db = this.readableDatabase
        val listaClientes = mutableListOf<ClientesDataClass>()

        var whereClause = ""

        // Genero el argumento Where si lo que quiero listar son cuotas vencidas
        if (soloCuotaVencida) {
            whereClause = ("""WHERE
            s.SocioID IS NOT NULL AND 
            (s.FechaVencimientoCuota IS NULL OR date(s.FechaVencimientoCuota) < date('now'))
            """.trimIndent())
        }


        // Con lo armado genero el argumento de busqueda
        val query = """
            SELECT 
                c.ClienteID,
                c.Nombre, 
                c.Apellido, 
                c.TipoDocumento, 
                c.NumeroDocumento, 
                CASE 
                    WHEN s.SocioID IS NOT NULL THEN 1
                    ELSE 0
                END AS EsSocio,
                s.FechaVencimientoCuota
            FROM Cliente c
            LEFT JOIN Socio s ON c.ClienteID = s.ClienteID
            $whereClause
            ORDER BY c.Apellido ASC, c.Nombre ASC
            """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val clienteID = cursor.getLong(0)
                val nombre = cursor.getString(1)
                val apellido = cursor.getString(2)
                val tipoDoc = cursor.getString(3)
                val dni = cursor.getInt(4)
                val esSocio = cursor.getInt(5) == 1
                val venceCuota = if (!cursor.isNull(6)) cursor.getString(6) else null

                listaClientes.add(
                    ClientesDataClass(
                        clienteID,
                        nombre,
                        apellido,
                        tipoDoc,
                        dni,
                        esSocio,
                        venceCuota
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()


        if (filtro.isNotEmpty()) {
            val palabrasFiltro = filtro.normalizar().split("\\s+".toRegex())

            val listaFiltrada = listaClientes.filter { cliente ->
                val nombre = cliente.nombre.normalizar()
                val apellido = cliente.apellido.normalizar()
                val dni = cliente.dni.toString()

                // Cada palabra debe aparecer en al menos uno de los campos
                palabrasFiltro.all { palabra ->
                    nombre.contains(palabra) || apellido.contains(palabra) || dni.contains(palabra)
                }
            }

            return listaFiltrada
        }
        return listaClientes
    }




    // FUNCIONES PARA HACER REGISTROS EN MI BASE DE DATOS
    // REGISTRO DE NUEVO CLIENTE TIPO SOCIO O NO SOCIO
    fun registrarCliente(
        nombre: String,
        apellido: String,
        tipoDoc: String,
        nroDoc: Int,
        fechaInscripcion: String,
        aptoFisico: Boolean,
        esSocio: Boolean
    ): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val stmt = db.compileStatement("""
            INSERT INTO Cliente (Nombre, Apellido, TipoDocumento, NumeroDocumento, FechaInscripcion, AptoFisico)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent())
            stmt.bindString(1, nombre)
            stmt.bindString(2, apellido)
            stmt.bindString(3, tipoDoc)
            stmt.bindLong(4, nroDoc.toLong())
            stmt.bindString(5, fechaInscripcion)
            stmt.bindLong(6, if (aptoFisico) 1L else 0L)

            // Ejecuto el insert y capturo el cliente ID al mismo tiempo
            val clienteID = stmt.executeInsert()
            if (clienteID < 0L) {
                return -1L
            }

            if (esSocio) {
                val socioStmt = db.compileStatement("INSERT INTO Socio (ClienteID) VALUES (?)")
                socioStmt.bindLong(1, clienteID)
                socioStmt.executeInsert()
            } else {
                val noSocioStmt = db.compileStatement("INSERT INTO NoSocio (ClienteID) VALUES (?)")
                noSocioStmt.bindLong(1, clienteID)
                noSocioStmt.executeInsert()
            }

            db.setTransactionSuccessful()
            return clienteID
        } catch (e: Exception) {
            return -1L
        } finally {
            db.endTransaction()
        }
    }

    // INGRESAR EL PAGO DE UNA ACTIVIDAD O UNA MEMBRESIA
    fun ingresarPago(
        clienteID: Long,
        monto: Double,
        metodoPago: String,
        cuotas: Int,
        fechaPago: String,
        membresiaID: Int? = null,
        actividadID: Int? = null
    ): Boolean {
        val db = writableDatabase
        return try {
            val stmt = db.compileStatement("""
            INSERT INTO Pago (ClienteID, MembresiaID, ActividadID, Monto, MetodoPago, Cuotas, FechaPago)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent())

            stmt.bindLong(1, clienteID.toLong())
            if (membresiaID != null) stmt.bindLong(2, membresiaID.toLong()) else stmt.bindNull(2)
            if (actividadID != null) stmt.bindLong(3, actividadID.toLong()) else stmt.bindNull(3)
            stmt.bindDouble(4, monto)
            stmt.bindString(5, metodoPago)
            stmt.bindLong(6, cuotas.toLong())
            stmt.bindString(7, fechaPago)

            stmt.executeInsert()
            true
        } catch (e: Exception) {
            false
        }
    }

    // PASAR UN NO SOCIO QUE SE HACE SOCIO
    fun convertirNoSocioEnSocio(clienteID: Int, fechaVencimientoCuota: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            db.execSQL("DELETE FROM NoSocio WHERE ClienteID = ?", arrayOf(clienteID))
            db.execSQL("""
            INSERT INTO Socio (ClienteID, FechaVencimientoCuota)
            VALUES (?, ?)
        """, arrayOf(clienteID, fechaVencimientoCuota))
            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
        }
    }


    // DAR DE BAJA UN SOCIO QUE PASA A SER UN NO SOCIO (SOLO CON CUOTA VENCIDA)
    fun darDeBajaSocio(clienteID: Int, fechaHoy: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val cursor = db.rawQuery("""
            SELECT FechaVencimientoCuota FROM Socio WHERE ClienteID = ?
        """, arrayOf(clienteID.toString()))

            var vencida = false
            if (cursor.moveToFirst()) {
                val fechaVencimiento = cursor.getString(0)
                vencida = fechaVencimiento < fechaHoy
            }
            cursor.close()

            if (vencida) {
                db.execSQL("DELETE FROM Socio WHERE ClienteID = ?", arrayOf(clienteID))
                db.execSQL("INSERT INTO NoSocio (ClienteID) VALUES (?)", arrayOf(clienteID))
                db.setTransactionSuccessful()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
        }
    }


    // BUSCAR CLIENTES SEGUN LO QUE INGRESO A UN CAMPO
    fun buscarClientes(db: SQLiteDatabase, filtro: String): Cursor {
        // Primero convierto el string ingresado en una lista de palabras
        val palabras = filtro.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }

        // Si no hay nada en el filtro, devolvemos todos los clientes
        if (palabras.isEmpty()) {
            return db.rawQuery("SELECT * FROM Cliente", null)
        }

        // Construimos una clausula WHERE dinamica que va a depender de la cantidad de palabras
        // que se ingresan en el campo de busqueda y lo compara con los datos de mi base que
        // esten en la tabla cliente, sea el Nombre, el Apellido o el numero de documento

        // Primero creamos una lista con la misma sentencia repetida tantas veces
        // como palabras haya ingresado en el string (si pongo dos palabras, me repite dos veces
        // la misma sentencia
        val whereParts = palabras.map { palabra ->
            // En NumeroDocumento convertimos a TEXT para buscar texto también
            ("(Nombre LIKE ? OR Apellido LIKE ? OR CAST(NumeroDocumento AS TEXT) LIKE ?)")
        }

        // Unimos todas las sentencias de whereParts con un AND para que me quede una sola sentencia completa
        // Y se usa AND porque todas las sentencias deben cumplirse.
        val whereClause = whereParts.joinToString(" AND ")

        // Preparamos los argumentos para los ? en el query, usando % para LIKE
        // Se van a crear los argumentos para cada palabra
        // Y cada palabra se ingresa 3 veces porque la voy a buscar en la columna nombre,
        // en la columna apellido y en la columna numero de documento
        val whereArgs = palabras.flatMap { palabra ->
            listOf("%$palabra%", "%$palabra%", "%$palabra%")
        }.toTypedArray()

        // Ejecutamos la consulta donde le mandamos la sentencia completa creada en whereClause
        // y todos los argumentos creados en whereArgs, Devolvemos el finalmente el cursor.
        return db.rawQuery("SELECT * FROM Cliente WHERE $whereClause", whereArgs)
    }

    // ***VISUALIZAR DATOS***
    // PERFIL DE CLIENTE / CARNET DE SOCIO
    // COMPROBANTE DE PAGO
    // LISTADO DE CLIENTES
    // LISTADO DE CLIENTES CON CUOTA VENCIDA
    // PLANES DE MEMBRESIA
    // ACTIVIDADES
    }
