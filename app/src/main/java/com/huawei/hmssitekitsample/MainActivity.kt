package com.huawei.hmssitekitsample

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.huawei.hmssitekitsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),  View.OnClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        findViewById<View>(R.id.text_search_button).setOnClickListener(this)
        findViewById<View>(R.id.detail_search_button).setOnClickListener(this)
        findViewById<View>(R.id.nearby_search_button).setOnClickListener(this)
        findViewById<View>(R.id.query_suggestion_button).setOnClickListener(this)
        findViewById<View>(R.id.query_auto_complete_button).setOnClickListener(this)
        findViewById<View>(R.id.search_widget_button).setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // Jump to keyword search activity.
            R.id.text_search_button -> {
                startActivity(Intent(this, TextSearchActivity::class.java))
            }
            // Jump to place detail search activity.
            R.id.detail_search_button -> startActivity(Intent(this, DetailSearchActivity::class.java))
            // Jump to nearby place search activity.
            R.id.nearby_search_button -> startActivity(Intent(this, NearbySearchActivity::class.java))
            // Jump to keyword search activity.
            R.id.query_suggestion_button -> startActivity(Intent(this, QuerySuggestionActivity::class.java))
            // Jump to auto complete activity.
            R.id.query_auto_complete_button -> startActivity(Intent(this, QueryAutoCompleteActivity::class.java))
            // Jump to auto search intent activity.
            R.id.search_widget_button -> startActivity(Intent(this, SearchIntentActivity::class.java))
            else -> {
            }
        }
    }
}