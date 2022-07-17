package cryptocurrency.portfolio.tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import cryptocurrency.portfolio.tracker.util.Constants
import dagger.hilt.android.AndroidEntryPoint


private const val LOG_TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        var navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        val sharedPrefs = getSharedPreferences(Constants.PORTFOLY_SHARED_PREFS, MODE_PRIVATE)

        val hasPortfolio = sharedPrefs.getBoolean(Constants.HAS_PORTFOLIO, false)

        if(hasPortfolio) {
            navGraph.startDestination = R.id.portfolioFragment
            navController.graph = navGraph
        } else {
            navGraph.startDestination = R.id.welcomeFragment
            navController.graph = navGraph
        }



    }






}