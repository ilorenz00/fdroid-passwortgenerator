package com.lorenzl5.hermes.passwort.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MetaDataSQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "metadatabase"
        private const val DB_VERSION = 1
        private const val TABLE = "accounts"

        const val COL_ID = "ID"
        const val COL_DOMAIN = "DOMAIN"
        const val COL_USERNAME = "USERNAME"
        const val COL_HAS_LETTERS_LOW = "HAS_LETTERS_LOW"
        const val COL_HAS_LETTERS_UP = "HAS_LETTERS_UP"
        const val COL_HAS_NUMBERS = "HAS_NUMBERS"
        const val COL_HAS_SYMBOLS = "HAS_SYMBOLS"
        const val COL_LENGTH = "LENGTH"
        const val COL_ITERATION = "ITERATION"
        const val COL_POSITIONID = "POSITIONID"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_DOMAIN TEXT NOT NULL, " +
                "$COL_USERNAME TEXT, " +
                "$COL_HAS_LETTERS_LOW INTEGER DEFAULT 1, " +
                "$COL_HAS_LETTERS_UP INTEGER DEFAULT 1, " +
                "$COL_HAS_NUMBERS INTEGER DEFAULT 1, " +
                "$COL_HAS_SYMBOLS INTEGER DEFAULT 1, " +
                "$COL_LENGTH INTEGER DEFAULT 10, " +
                "$COL_ITERATION INTEGER DEFAULT 1, " +
                "$COL_POSITIONID INTEGER DEFAULT 0)"
        )
        // Sample data
        addAccount(db, "sample.email.com", "sample.name@email.com", 1, 1, 1, 1, 15, 1)
        addAccount(db, "sample-domain.de", "Sample Username", 1, 1, 1, 1, 20, 1)
        addAccount(db, "Sample PIN", "", 0, 0, 1, 0, 4, 1)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    fun getAllAccounts(): List<MetaData> {
        val list = mutableListOf<MetaData>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE ORDER BY $COL_POSITIONID ASC", null
        )
        while (cursor.moveToNext()) {
            list.add(
                MetaData(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    domain = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOMAIN)) ?: "",
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)) ?: "",
                    hasLettersLow = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAS_LETTERS_LOW)),
                    hasLettersUp = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAS_LETTERS_UP)),
                    hasNumbers = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAS_NUMBERS)),
                    hasSymbols = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAS_SYMBOLS)),
                    length = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LENGTH)),
                    iteration = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITERATION)),
                    positionId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_POSITIONID))
                )
            )
        }
        cursor.close()
        return list
    }

    fun getAccountById(id: Int): MetaData? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE WHERE $COL_ID = ?", arrayOf(id.toString())
        )
        var result: MetaData? = null
        if (cursor.moveToFirst()) {
            result = MetaData(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                domain = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOMAIN)) ?: "",
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)) ?: "",
                hasLettersLow = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAS_LETTERS_LOW)),
                hasLettersUp = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAS_LETTERS_UP)),
                hasNumbers = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAS_NUMBERS)),
                hasSymbols = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAS_SYMBOLS)),
                length = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LENGTH)),
                iteration = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITERATION)),
                positionId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_POSITIONID))
            )
        }
        cursor.close()
        return result
    }

    fun addAccount(
        domain: String, username: String,
        hasLow: Int, hasUp: Int, hasNum: Int, hasSym: Int,
        length: Int, iteration: Int
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DOMAIN, domain)
            put(COL_USERNAME, username)
            put(COL_HAS_LETTERS_LOW, hasLow)
            put(COL_HAS_LETTERS_UP, hasUp)
            put(COL_HAS_NUMBERS, hasNum)
            put(COL_HAS_SYMBOLS, hasSym)
            put(COL_LENGTH, length)
            put(COL_ITERATION, iteration)
            put(COL_POSITIONID, nextPosition(db))
        }
        return db.insert(TABLE, null, values)
    }

    private fun addAccount(
        db: SQLiteDatabase,
        domain: String, username: String,
        hasLow: Int, hasUp: Int, hasNum: Int, hasSym: Int,
        length: Int, iteration: Int
    ) {
        val values = ContentValues().apply {
            put(COL_DOMAIN, domain)
            put(COL_USERNAME, username)
            put(COL_HAS_LETTERS_LOW, hasLow)
            put(COL_HAS_LETTERS_UP, hasUp)
            put(COL_HAS_NUMBERS, hasNum)
            put(COL_HAS_SYMBOLS, hasSym)
            put(COL_LENGTH, length)
            put(COL_ITERATION, iteration)
            put(COL_POSITIONID, nextPosition(db))
        }
        db.insert(TABLE, null, values)
    }

    fun updateAccount(meta: MetaData): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DOMAIN, meta.domain)
            put(COL_USERNAME, meta.username)
            put(COL_HAS_LETTERS_LOW, meta.hasLettersLow)
            put(COL_HAS_LETTERS_UP, meta.hasLettersUp)
            put(COL_HAS_NUMBERS, meta.hasNumbers)
            put(COL_HAS_SYMBOLS, meta.hasSymbols)
            put(COL_LENGTH, meta.length)
            put(COL_ITERATION, meta.iteration)
        }
        val rows = db.update(TABLE, values, "$COL_ID = ?", arrayOf(meta.id.toString()))
        return rows > 0
    }

    fun deleteAccount(id: Int): Boolean {
        val db = writableDatabase
        val rows = db.delete(TABLE, "$COL_ID = ?", arrayOf(id.toString()))
        return rows > 0
    }

    fun deleteAll() {
        writableDatabase.delete(TABLE, null, null)
    }

    private fun nextPosition(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery("SELECT MAX($COL_POSITIONID) FROM $TABLE", null)
        var max = 0
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            max = cursor.getInt(0)
        }
        cursor.close()
        return max + 1
    }
}
