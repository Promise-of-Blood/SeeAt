package com.pob.seeat

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import com.pob.seeat.databinding.ActivityMainBinding
import com.pob.seeat.presentation.view.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContentView(binding.root)

        initBottomNavigation()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Timber.tag("token").d(it)
        }

//        askNotificationPermission()
//        checkLocationPermission()
    }

    private fun initBottomNavigation() = with(binding) {
        val homeFragment = HomeFragment() // HomeFragment의 인스턴스 생성

        supportFragmentManager.beginTransaction().replace(R.id.home_fragment, homeFragment).commit()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.navigation_home, true).build()

        navMain.setupWithNavController(navController)
        navMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    showNavHostFragment(false)
                    navController.setGraph(R.navigation.main_navigation)
                    navController.navigate(R.id.navigation_home, null, navOptions)
                    true
                }

                else -> {
                    navController.navigate(item.itemId)
                    showNavHostFragment(true)
                    true
                }
            }
        }
    }

    fun showNavHostFragment(isVisible: Boolean) {
        if (isVisible) {
            binding.navHostFragment.visibility = View.VISIBLE
        } else {
            binding.navHostFragment.visibility = View.INVISIBLE
        }
    }

    fun getCurrentLocation(callback: (Location?) -> Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                callback(location)
            }.addOnFailureListener { exception ->
                Timber.tag("SelectLocateFragment").e(exception, "위치 정보를 가져오는 중 에러 발생")
                callback(null)
            }
        } catch (e: SecurityException) {
            Timber.tag("SelectLocateFragment").e(e, "위치 권한이 없습니다.")
            callback(null)
        }
    }


    /**
     * Bottom Navigation의 Visibility를 설정합니다.
     * @param visibility Bottom Navigation의 Visibility ex. View.GONE, View.VISIBLE
     * */
    fun setBottomNavigationVisibility(visibility: Int) = with(binding) {
        navMain.visibility = visibility
        navShadow.visibility = visibility
    }

    /**
     * Bottom Navigation의 Selected Item을 설정합니다.
     * @param itemId Bottom Navigation의 Selected Item ex. R.id.navigation_home
     * */
    fun setBottomNavigationSelectedItem(itemId: Int) = with(binding) {
        navMain.selectedItemId = itemId
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, ACCESS_FINE_LOCATION
                )
            ) {
                // 권한이 없을 경우 권한 요청
                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
                    ), LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                // 권한 요청
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
                    ), LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
}