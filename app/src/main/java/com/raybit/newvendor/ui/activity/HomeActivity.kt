package com.raybit.newvendor.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import com.raybit.newvendor.R

import com.raybit.newvendor.utils.SocketManager
import com.raybit.newvendor.utils.getCurrentNavigationFragment
import com.raybit.newvendor.utils.toStart

import com.raybit.newvendor.base.presentation.activity.BaseActivity
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HomeActivity : BaseActivity(), SocketManager.OnMessageReceiver{
    private lateinit var binding: ActivityHomeBinding

    private var userData: LoginResponse? = null
    private var isHome: Boolean = true
    private var navController: NavController? = null
    var serviceType = ""
    private val navBuilder: NavOptions.Builder by lazy {
        NavOptions.Builder()
    }
    override val layoutResId: Int
        get() = R.layout.activity_home

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper

    private val socketManager = SocketManager.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.bind(findViewById(R.id.rootView))

        intialize()
        setData()
        listeners()

    }

    override fun onStart() {
        super.onStart()
    }
    private fun listeners() {

        binding.tvLogout.setOnClickListener {
            logoutUser()
        }

    }

    fun logoutUser() {
        lifecycleScope.launch {
            mDataStoreHelper.clear()
            mDataStoreHelper.logOut()
        }

        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }

    private fun intialize() {

        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
        // Connect to WebSocket

        val actionBarDrawerToggle: ActionBarDrawerToggle =
            object :
                ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close) {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)
                    val slideX = drawerView.width * slideOffset
                    binding.content.translationX = slideX
                }
            }
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
//                imProfile.loadImage(it?.profile_pic_url!!)
                userData = it
                if (userData != null) {
                    binding.tvName.text = it!!.name
                    socketManager.connect(this@HomeActivity, userData!!, this@HomeActivity)
                } else {
                    toStart()
                }
            }
        }
        binding.ivDrawer.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(binding.drawer)){
                binding.drawerLayout.closeDrawer(binding.drawer)
            }else{
                binding.drawerLayout.openDrawer(binding.drawer)
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.closeDrawer(binding.drawer)
        }
    }

    override fun onPause() {
        val arguments = JSONObject()
//        arguments.putOpt("online_status", 0)
//        arguments.putOpt("user_id", userData?.id)
        SocketManager.destroy()
//        myUserRef.child( userData?.id.toString()).setValue("Offline")
        super.onPause()
    }

    private fun setData() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.homeFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.userMenu.setupWithNavController(navController!!)


        var inflater = navHostFragment.navController.navInflater

            navController!!.graph = inflater.inflate(R.navigation.doctor_home_navigation)

        navController?.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.fragmentChat -> {
                    binding.ivLogo.hide()
                    binding.ivDrawer.hide()
                    binding.userMenu.hide()

                    isHome = false
                }
                R.id.profileFragment -> {
                    binding.ivLogo.hide()
                    binding. ivDrawer.hide()
                    binding.userMenu.hide()

                    isHome = false
                }
                R.id.historyFragment -> {
                    binding. ivLogo.hide()
                    binding. ivDrawer.hide()
                    binding. userMenu.hide()

                    isHome = false
                }
                else -> {
                    isHome = true
                    binding.ivLogo.show()
                    binding. ivDrawer.show()
                    binding. userMenu.show()
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val currentFragment = supportFragmentManager.getCurrentNavigationFragment()
        currentFragment?.onActivityResult(requestCode, resultCode, data)

    }

    override fun onMessageReceive(message: String, event: String) {
      Log.e(event,message)
    }

//    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
//        Log.e("success",p0.toString())
//    }
//
//    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
//        Log.e("error",p1.toString())
//    }
//
//    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
//
//    }


}