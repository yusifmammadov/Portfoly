package cryptocurrency.portfolio.tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController


private const val LOG_TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModelFactory = MainActivityViewModelFactory(application)
        val viewModel: MainActivityViewModel by viewModels(){viewModelFactory}
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        var navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        val sharedPrefs = getSharedPreferences("Portfolio", MODE_PRIVATE)

        var hasPortfolio = sharedPrefs.getBoolean("hasPortfolio", false)
        val hasAssetList = sharedPrefs.getBoolean("hasAssetList", false)

        if(hasPortfolio) {
            navGraph.startDestination = R.id.portfolioFragment
            navController.graph = navGraph
        } else {
            navGraph.startDestination = R.id.welcomeFragment
            navController.graph = navGraph
        }

        viewModel.listAllCryptoAssets.observe(this) {
             Log.v(LOG_TAG, "asset list: $it")
            sharedPrefs.edit().putBoolean("hasAssetList", true).apply()
        }

        if (!hasAssetList) {
            viewModel.getAssetListFromApi()
        }

    }


}