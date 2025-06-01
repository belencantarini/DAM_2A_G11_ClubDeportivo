package com.example.clubdeportivog11

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
            CREATE TABLE Cliente (
                ClienteID INTEGER PRIMARY KEY AUTOINCREMENT,
                Nombre TEXT NOT NULL,
                Apellido TEXT NOT NULL,
                TipoDocumento TEXT NOT NULL,
                NumeroDocumento INTEGER NOT NULL,
                FechaNacimiento TEXT,
                FechaInscripcion TEXT NOT NULL,
                AptoFisico INTEGER DEFAULT 0,
                UNIQUE (TipoDocumento, NumeroDocumento)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE Socio (
                SocioID INTEGER PRIMARY KEY AUTOINCREMENT,
                ClienteID INTEGER NOT NULL,
                FechaVencimientoCuota TEXT NOT NULL,
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
            ('Musculacion', 1500.00),
            ('Tenis', 2000.00)
        """.trimIndent())

        // Membresias
        db.execSQL("""
            INSERT INTO PlanMembresia (PlanMembresia, Meses, Precio) VALUES
            ('Mensual', 1, 30000.00),
            ('Trimestral', 3, 80000.00),
            ('Semestral', 6, 150000.00),
            ('Anual', 12, 290000.00)
        """.trimIndent())
    }

    // OnUpgrade  -  En caso de nueva version para actualizar mi base de datos borro lo anterior
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Pago")
        db.execSQL("DROP TABLE IF EXISTS PlanMembresia")
        db.execSQL("DROP TABLE IF EXISTS Actividad")
        db.execSQL("DROP TABLE IF EXISTS NoSocio")
        db.execSQL("DROP TABLE IF EXISTS Socio")
        db.execSQL("DROP TABLE IF EXISTS Cliente")
        db.execSQL("DROP TABLE IF EXISTS Usuario")
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

    // REGISTRO DE NUEVO CLIENTE TIPO SOCIO O NO SOCIO
    fun registrarCliente(
        nombre: String,
        apellido: String,
        tipoDoc: String,
        nroDoc: Int,
        fechaNac: String,
        fechaInscripcion: String,
        aptoFisico: Boolean,
        esSocio: Boolean,
        fechaVencimientoCuota: String = ""
    ): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val stmt = db.compileStatement("""
            INSERT INTO Cliente (Nombre, Apellido, TipoDocumento, NumeroDocumento, FechaNacimiento, FechaInscripcion, AptoFisico)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent())
            stmt.bindString(1, nombre)
            stmt.bindString(2, apellido)
            stmt.bindString(3, tipoDoc)
            stmt.bindLong(4, nroDoc.toLong())
            stmt.bindString(5, fechaNac)
            stmt.bindString(6, fechaInscripcion)
            stmt.bindLong(7, if (aptoFisico) 1 else 0)
            stmt.executeInsert()

            val clienteID = db.rawQuery("SELECT last_insert_rowid()", null).use {
                it.moveToFirst()
                it.getInt(0)
            }

            if (esSocio) {
                val socioStmt = db.compileStatement("""
                INSERT INTO Socio (ClienteID, FechaVencimientoCuota, Activo)
                VALUES (?, ?, 1)
            """.trimIndent())
                socioStmt.bindLong(1, clienteID.toLong())
                socioStmt.bindString(2, fechaVencimientoCuota)
                socioStmt.executeInsert()
            } else {
                val noSocioStmt = db.compileStatement("INSERT INTO NoSocio (ClienteID) VALUES (?)")
                noSocioStmt.bindLong(1, clienteID.toLong())
                noSocioStmt.executeInsert()
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
        }
    }

    // INGRESAR EL PAGO DE UNA ACTIVIDAD O UNA MEMBRESIA
    fun ingresarPago(
        clienteID: Int,
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
