package com.zackratos.ultimatebarx.ultimatebarx

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.zackratos.ultimatebarx.ultimatebarx.bean.BarConfig
import com.zackratos.ultimatebarx.ultimatebarx.core.*
import com.zackratos.ultimatebarx.ultimatebarx.extension.setSystemUiFlagWithLight

/**
 * @Author   : zackratos
 * @Date     : 2021/8/26 10:26 上午
 * @email    : zhangwenchao@soulapp.cn
 * @Describe :
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun FragmentActivity.applyStatusBar(config: BarConfig) {
    ultimateBarXInitialization()
    val navLight = manager.getNavigationBarConfig(this).light
    setSystemUiFlagWithLight(config.light, navLight)
    updateStatusBar(config)
    defaultNavigationBar()
    addObserver()
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun FragmentActivity.applyNavigationBar(config: BarConfig) {
    ultimateBarXInitialization()
    val staLight = manager.getStatusBarConfig(this).light
    setSystemUiFlagWithLight(staLight, config.light)
    updateNavigationBar(config)
    defaultStatusBar()
    addObserver()
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun Fragment.applyStatusBar(config: BarConfig) {
    requireActivity().ultimateBarXInitialization()
    ultimateBarXInitialization()
    val navLight = manager.getNavigationBarConfig(this).light
    requireActivity().setSystemUiFlagWithLight(config.light, navLight)
    updateStatusBar(config)
    requireActivity().defaultNavigationBar()
    addObserver()
    requireActivity().addObserver()
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun Fragment.applyNavigationBar(config: BarConfig) {
    requireActivity().ultimateBarXInitialization()
    ultimateBarXInitialization()
    val staLight = manager.getStatusBarConfig(this).light
    requireActivity().setSystemUiFlagWithLight(staLight, config.light)
    updateNavigationBar(config)
    requireActivity().defaultStatusBar()
    addObserver()
    requireActivity().addObserver()
}

