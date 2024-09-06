package com.pob.seeat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.ActivityMainBinding
import com.pob.seeat.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        initBottomNavigation()
        initViewModel()

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Timber.tag("token").d(it)
        }

        askNotificationPermission()

        // 주제 정해서 보낼 수 있음
//        Firebase.messaging.subscribeToTopic("weather")
//            .addOnCompleteListener { task ->
//                var msg = "Subscribed"
//                if (!task.isSuccessful) {
//                    msg = "Subscribe failed"
//                }
//                Timber.tag(TAG).d(msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            }

    }

    private fun initBottomNavigation() = with(binding) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navMain.setupWithNavController(navController)
    }

    private fun initViewModel() = with(mainViewModel) {
        getUnReadAlarmCount()
        lifecycleScope.launch {
            unreadAlarmCount.flowWithLifecycle(lifecycle).collectLatest { response ->
                when (response) {
                    is Result.Error -> Timber.e("Error: ${response.message}")
                    is Result.Loading -> {}
                    is Result.Success -> setUpBadge(response.data)
                }
            }
        }
    }

    private fun setUpBadge(count: Long) = with(binding) {
        val badge = navMain.getOrCreateBadge(R.id.navigation_alarm)
        badge.backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.tertiary)
        badge.isVisible = count != 0L
        badge.text = if (count <= 9) count.toString() else "9+"
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


}