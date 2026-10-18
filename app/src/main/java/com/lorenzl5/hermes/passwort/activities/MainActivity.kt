package com.lorenzl5.hermes.passwort.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.lorenzl5.hermes.passwort.R
import com.lorenzl5.hermes.passwort.database.MetaData
import com.lorenzl5.hermes.passwort.database.MetaDataSQLiteHelper
import com.lorenzl5.hermes.passwort.dialogs.AddMetaDataDialog
import com.lorenzl5.hermes.passwort.dialogs.GeneratePasswordDialog
import com.lorenzl5.hermes.passwort.dialogs.UpdateMetadataDialog
import com.lorenzl5.hermes.passwort.helpers.MetaDataAdapter
import com.lorenzl5.hermes.passwort.recycler.SwipeToDismissRecyclerView
import android.content.Intent
import com.lorenzl5.hermes.passwort.recycler.SwipeLayout

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var db: MetaDataSQLiteHelper
    private lateinit var adapter: MetaDataAdapter
    private val items = mutableListOf<MetaData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> { /* stay */ }
                R.id.nav_tutorial -> {
                    startActivity(Intent(this, TutorialActivity::class.java))
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_help -> {
                    startActivity(Intent(this, HelpActivity::class.java))
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        db = MetaDataSQLiteHelper(this)

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(SwipeToDismissRecyclerView(this))

        adapter = MetaDataAdapter(
            items,
            onClick = { meta -> showGenerateDialog(meta) },
            onLongClick = { meta -> showUpdateDialog(meta) },
            onSwipeDelete = { meta -> deleteAccount(meta) }
        )
        recyclerView.adapter = adapter

        // Swipe-to-dismiss support
        val swipeLayout = SwipeLayout(recyclerView)
        recyclerView.addOnItemTouchListener(swipeLayout)
        swipeLayout.onSwipeDelete = { meta -> deleteAccount(meta) }

        loadAccounts()

        val fab = findViewById<FloatingActionButton>(R.id.add_fab)
        fab.setOnClickListener {
            AddMetaDataDialog { loadAccounts() }.show(supportFragmentManager, "add")
        }

        val emptyHint = findViewById<android.widget.TextView>(R.id.empty_hint)
        emptyHint.visibility = if (items.isEmpty())
            android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun onResume() {
        super.onResume()
        loadAccounts()
    }

    private fun loadAccounts() {
        items.clear()
        items.addAll(db.getAllAccounts())
        adapter.updateItems(items)
        val emptyHint = findViewById<android.widget.TextView>(R.id.empty_hint)
        emptyHint.visibility = if (items.isEmpty())
            android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun showGenerateDialog(meta: MetaData) {
        GeneratePasswordDialog(meta, db) { loadAccounts() }
            .show(supportFragmentManager, "generate")
    }

    private fun showUpdateDialog(meta: MetaData) {
        UpdateMetadataDialog(meta, db) { loadAccounts() }
            .show(supportFragmentManager, "update")
    }

    private fun deleteAccount(meta: MetaData) {
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.delete_confirm_title)
            .setMessage(R.string.delete_confirm_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                db.deleteAccount(meta.id)
                loadAccounts()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }
}
