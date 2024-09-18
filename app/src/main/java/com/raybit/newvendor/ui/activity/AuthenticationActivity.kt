package com.raybit.newvendor.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.activity.BaseActivity
import com.raybit.newvendor.utils.getCurrentNavigationFragment

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : BaseActivity() {
    private var navController: NavController? = null
    lateinit var navHostFragment: NavHostFragment


    override val layoutResId: Int
        get() = R.layout.activity_authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        navController = navHostFragment.navController
        val inflater = navHostFragment.navController.navInflater

        var navGraph = inflater.inflate(R.navigation.authentication)

//        if(intent !=null){
//            if( intent.getStringExtra("openFragment")=="login_sign_up"){
//                navGraph.startDestination = R.id.selectLoginSignUpFragment
//                navController!!.graph = navGraph
//            }
//
//            else if( intent.getStringExtra("openFragment")=="SignUpUpload"){
//                navGraph.startDestination = R.id.SignUpUploadDocumentFragment
//                navController!!.graph = navGraph
//            }
//        }

    }

    private fun getIntentData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val currentFragment = supportFragmentManager.getCurrentNavigationFragment()
        currentFragment?.onActivityResult(requestCode, resultCode, data)

    }

}