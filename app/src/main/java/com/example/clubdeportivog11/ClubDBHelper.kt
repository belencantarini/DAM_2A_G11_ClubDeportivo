package com.example.clubdeportivog11

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clubdeportivog11.models.ActividadesDataClass
import com.example.clubdeportivog11.models.ClientesDataClass
import com.example.clubdeportivog11.models.HistorialPagosDataClass
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
                ('Ana', 'Torres', 'DNI', 30111111, '2025-06-01', 1),
                ('Bruno', 'Fernández', 'DNI', 30222222, '2025-06-02', 1),
                ('Clara', 'López', 'DNI', 30333333, '2025-06-03', 1),
                ('Diego', 'Martínez', 'DNI', 30444444, '2025-06-04', 1),
                ('Elena', 'García', 'DNI', 30555555, '2025-06-05', 1)
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Socio (ClienteID, FechaVencimientoCuota)
            VALUES
                (1, '2025-07-01'),   -- válido
                (2, NULL),           -- vencido
                (3, '2025-06-15'),   -- vencido
                (4, '2025-10-01'),   -- válido
                (5, '2025-08-01')    -- válido
        """.trimIndent())


        db.execSQL("""
            INSERT INTO Usuario (Nombre, Pass, ClienteID, RolUsuario)
            VALUES 
            ('ana', 'ana123', 1, 2),
            ('bruno', 'bruno123', 2, 2),
            ('clara', 'clara123', 3, 2),
            ('diego', 'diego123', 4, 2)
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Pago (ClienteID, MembresiaID, ActividadID, Monto, MetodoPago, Cuotas, FechaPago)
            VALUES 
            (1, 1, NULL, 30000.00, 'Efectivo', 1, '2025-05-01'),           -- Mensual
            (2, 2, NULL, 80000.00, 'Tarjeta de Crédito', 3, '2025-06-01'), -- Trimestral
            (3, 3, NULL, 150000.00, 'Tarjeta de Crédito', 6, '2025-06-05'),-- Semestral
            (4, 4, NULL, 290000.00, 'Tarjeta de Crédito', 6, '2025-04-15'),-- Anual
            (5, 1, NULL, 30000.00, 'Efectivo', 1, '2025-06-03');            -- Mensual
        """.trimIndent())



        // Clientes no socios
        db.execSQL("""
            INSERT INTO Cliente (Nombre, Apellido, TipoDocumento, NumeroDocumento, FechaInscripcion, AptoFisico)
            VALUES 
            ('Federico', 'Suárez', 'DNI', 30666666, '2025-06-01', 1),
            ('Gabriela', 'Paz', 'DNI', 30777777, '2025-06-02', 0),
            ('Hernán', 'Ruiz', 'DNI', 30888888, '2025-06-03', 1),
            ('Isabel', 'Mendoza', 'DNI', 30999999, '2025-06-04', 1),
            ('Joaquín', 'Silva', 'DNI', 31000000, '2025-06-05', 1)
        """.trimIndent())

        db.execSQL("""
            INSERT INTO NoSocio (ClienteID)
            VALUES (6), (7), (8), (9), (10)
        """.trimIndent())

        db.execSQL("""
            INSERT INTO Pago (ClienteID, MembresiaID, ActividadID, Monto, MetodoPago, Cuotas, FechaPago)
            VALUES 
            (6, NULL, 1, 2000.00, 'Efectivo', 1, '2025-06-01'),  -- Fútbol
            (7, NULL, 8, 2500.00, 'Efectivo', 1, '2025-06-02'),  -- Zumba
            (8, NULL, 14, 4000.00, 'Efectivo', 1, '2025-06-03'), -- Funcional
            (9, NULL, 10, 3500.00, 'Efectivo', 1, '2025-06-04'), -- Boxeo
            (10, NULL, 9, 2800.00, 'Efectivo', 1, '2025-06-05'); -- Spinning
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

    //OBTENER DATOS DE USUARIO
    fun obtenerDatosUsuario(nombre: String, pass: String): Pair<Int, Long?>? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT RolUsuario, ClienteID FROM Usuario WHERE Nombre = ? AND Pass = ?",
            arrayOf(nombre, pass)
        )

        return if (cursor.moveToFirst()) {
            val rol = cursor.getInt(0)
            val clienteId = if (!cursor.isNull(1)) cursor.getLong(1) else null
            cursor.close()
            Pair(rol, clienteId)
        } else {
            cursor.close()
            null
        }
    }

    // CREACION DE NUEVO USUARIO CON ROL DE CLIENTE
    fun crearUsuarioCliente(nombre: String, pass: String, clienteID: Long): Boolean {
        val db = writableDatabase
        return try {
            val stmt = db.compileStatement("INSERT INTO Usuario (Nombre, Pass, ClienteID, RolUsuario) VALUES (?, ?, ?, 2)")
            stmt.bindString(1, nombre)
            stmt.bindString(2, pass)
            stmt.bindLong(3, clienteID)
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



    // OBTENER HISTORIAL DE PAGOS
    fun obtenerHistorialPagos(clienteId: Long): List<HistorialPagosDataClass> {
        val lista = mutableListOf<HistorialPagosDataClass>()
        val db = readableDatabase

        val cursor = db.rawQuery("""
        SELECT 
            p.FechaPago,
            a.Nombre AS NombreActividad,
            m.PlanMembresia AS NombreMembresia,
            p.Monto,
            p.MetodoPago

        FROM Pago p
        LEFT JOIN Actividad a ON p.ActividadID = a.ActividadID
        LEFT JOIN PlanMembresia m ON p.MembresiaID = m.MembresiaID
        WHERE p.ClienteID = ?
        ORDER BY p.FechaPago DESC
    """.trimIndent(), arrayOf(clienteId.toString()))

        while (cursor.moveToNext()) {
            val fecha = cursor.getString(0)
            val nombreActividad = cursor.getString(1)
            val nombreMembresia = cursor.getString(2)
            val monto = cursor.getDouble(3)
            val metodo = cursor.getString(4)


            val descripcion = when {
                !nombreActividad.isNullOrEmpty() -> nombreActividad
                !nombreMembresia.isNullOrEmpty() -> "Plan: $nombreMembresia"
                else -> "Otro pago"
            }

            lista.add(HistorialPagosDataClass(fecha, descripcion, monto, metodo ))
        }

        cursor.close()
        db.close()
        return lista
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
    fun convertirNoSocioEnSocio(clienteID: Long): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            db.execSQL("DELETE FROM NoSocio WHERE ClienteID = ?", arrayOf(clienteID))
            db.execSQL("""
            INSERT INTO Socio (ClienteID, FechaVencimientoCuota)
            VALUES (?, NULL)
        """.trimIndent(), arrayOf(clienteID))
            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
        }
    }


    // DAR DE BAJA UN SOCIO QUE PASA A SER UN NO SOCIO (SOLO CON CUOTA VENCIDA)
    fun darDeBajaSocio(clienteID: Long): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            db.execSQL("DELETE FROM Socio WHERE ClienteID = ?", arrayOf(clienteID))
            db.execSQL("DELETE FROM Usuario WHERE ClienteID = ?", arrayOf(clienteID))
            db.execSQL("INSERT INTO NoSocio (ClienteID) VALUES (?)", arrayOf(clienteID))
            db.setTransactionSuccessful()
            true
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
