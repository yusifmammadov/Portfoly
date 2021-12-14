package cryptocurrency.portfolio.tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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

        val sharedPrefs = getSharedPreferences("Portfolio", MODE_PRIVATE)

        val hasPortfolio = sharedPrefs.getBoolean("hasPortfolio", false)

        if(hasPortfolio) {
            navGraph.startDestination = R.id.portfolioFragment
            navController.graph = navGraph
        } else {
            navGraph.startDestination = R.id.welcomeFragment
            navController.graph = navGraph
        }
    }


}