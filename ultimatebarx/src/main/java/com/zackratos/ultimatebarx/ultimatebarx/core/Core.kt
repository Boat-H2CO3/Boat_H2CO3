package com.zackratos.ultimatebarx.ultimatebarx.core

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zackratos.kblistener.kblistener.BuildConfig
import com.zackratos.kblistener.kblistener.onKeyboardClose
import com.zackratos.kblistener.kblistener.onKeyboardOpen
import com.zackratos.ultimatebarx.ultimatebarx.*
import com.zackratos.ultimatebarx.ultimatebarx.bean.BarBackground
import com.zackratos.ultimatebarx.ultimatebarx.bean.BarConfig
import com.zackratos.ultimatebarx.ultimatebarx.extension.*
import com.zackratos.ultimatebarx.ultimatebarx.view.*

/**
 * @Author   : Zackratos
 * @Date     : 2020/11/28 17:55
 * @email    : 869649338@qq.com
 * @Describe :
 */
private const val TAG_WRAPPER = "${BuildConfig.LIBRARY_PACKAGE_NAME}_fragment_wrapper"

private val manager: UltimateBarXManager by lazy { UltimateBarXManager.instance }

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun FragmentActivity.ultimateBarXInitialization() {
    if (manager.getInitialization(this)) return
    manager.putOriginConfig(this)
    barInitialization()
    fixBottomNavigationViewPadding()
    addKeyboardListener()
    manager.putInitialization(this)
}


internal fun Fragment.ultimateBarXInitialization() {
    if (manager.getInitialization(this)) return
    addFrameLayoutWrapper()
    val actStaConfig = manager.getStatusBarConfig(requireActivity())
    val staConfig = manager.getStatusBarConfig(this)
    staConfig.light = actStaConfig.light
    manager.putStatusBarConfig(this, staConfig)
    // 取 Activity 的 NavigationBarLight
    // 不能取 Activity 的 originColor 然后计算 light
    // 防止 Activity 之前设置了 light ，但是被通过 originColor 计算的 light 覆盖掉
//    manager.putNavigationBarLight(this, manager.getNavigationBarLight(requireActivity()))
    val actNavConfig = manager.getNavigationBarConfig(requireActivity())
    val navConfig = manager.getNavigationBarConfig(this)
    navConfig.light = actNavConfig.light
    manager.putNavigationBarConfig(this, navConfig)
    fixBottomNavigationViewPadding()
    manager.putInitialization(this)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun FragmentActivity.updateStatusBar(config: BarConfig) {
    updateStatusBarView(config)
    manager.putStatusBarDefault(this)
    manager.putStatusBarConfig(this, config)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun FragmentActivity.updateNavigationBar(config: BarConfig) {
    updateNavigationBarView(config)
    manager.putNavigationBarDefault(this)
    manager.putNavigationBarConfig(this, config)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun Fragment.updateStatusBar(config: BarConfig) {
    val transparentConfig = BarConfig.newInstance().apply {
        transparent()
        light = config.light
    }
    requireActivity().updateStatusBar(transparentConfig)
    updateStatusBarView(config)
    manager.putStatusBarDefault(this)
    manager.putStatusBarConfig(this, config)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun Fragment.updateNavigationBar(config: BarConfig) {
    val transparentConfig = BarConfig.newInstance().apply {
        transparent()
        light = config.light
    }
    requireActivity().updateNavigationBar(transparentConfig)
    updateNavigationBarView(config)
    manager.putNavigationBarDefault(this)
    manager.putNavigationBarConfig(this, config)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun FragmentActivity.defaultStatusBar() {
    if (manager.getStatusBarDefault(this)) return
    updateStatusBar(manager.getStatusBarConfig(this))
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun FragmentActivity.defaultNavigationBar() {
    if (manager.getNavigationBarDefault(this)) return
    updateNavigationBar(manager.getNavigationBarConfig(this))
}

internal fun LifecycleOwner.addObserver() {
    if (manager.getAddObserver(this)) return
    lifecycle.addObserver(UltimateBarXObserver())
    manager.putAddObserver(this)
}


@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun FragmentActivity.barInitialization() {
    contentView?.clipToPadding = false
    rootView?.fitsSystemWindows = false
    barTransparent()
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun FragmentActivity.updateStatusBarView(config: BarConfig) {
    contentView?.setStatusBarPadding(config.fitWindow)
    val landscape = manager.context.landscape
    val statusBar = contentView?.getCreator(ActivityTag.instance, landscape)
        ?.getStatusBarView(this, config.fitWindow)
    statusBar?.updateBackground(config, Build.VERSION_CODES.M)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun FragmentActivity.updateNavigationBarView(config: BarConfig) {
    if (!manager.rom.navigationBarExist(this)) return
    val landscape = manager.context.landscape
    contentView?.setNavigationBarPadding(landscape, config.fitWindow)
    val navigationBar = contentView?.getCreator(ActivityTag.instance, landscape)
        ?.getNavigationBarView(this, config.fitWindow)
    navigationBar?.updateBackground(config, Build.VERSION_CODES.O)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun Fragment.updateStatusBarView(config: BarConfig) {
    val rootView = addFrameLayoutWrapper()
    rootView.setStatusBarPadding(config.fitWindow)
    val landscape = manager.context.landscape
    val statusBar = rootView.getCreator(FragmentTag.instance, landscape)
        ?.getStatusBarView(requireContext(), config.fitWindow)
    statusBar?.updateBackground(config, Build.VERSION_CODES.M)
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
private fun Fragment.updateNavigationBarView(config: BarConfig) {
    if (!manager.rom.navigationBarExist(requireActivity())) return
    val rootView = addFrameLayoutWrapper()
    val landscape = manager.context.landscape
    rootView.setNavigationBarPadding(landscape, config.fitWindow)
    val navigationBar = rootView.getCreator(FragmentTag.instance, landscape)
        ?.getNavigationBarView(requireContext(), config.fitWindow)
    navigationBar?.updateBackground(config, Build.VERSION_CODES.O)
}

// 给 Fragment 的根 View 外面套一层 FrameLayout(用反射拿到根 View)
private fun Fragment.addFrameLayoutWrapper(): ViewGroup {
    val view = requireView()
    if (view is FrameLayout && view.tag == TAG_WRAPPER) {
        view.clipToPadding = false
        return view
    }
    val flWrapper = FrameLayout(requireContext())
    flWrapper.clipToPadding = false
    flWrapper.tag = TAG_WRAPPER
    flWrapper.setTag(androidx.fragment.R.id.fragment_container_view_tag, this)
    val parent = view.parent
    if (parent is ViewGroup) {
        val index = parent.indexOfChild(view)
        parent.removeViewAt(index)
        parent.addView(flWrapper, index)
    }
    flWrapper.addView(view)
    manager.fragmentViewFiled.set(this, flWrapper)
    return flWrapper
}

private fun ViewGroup.getCreator(tag: Tag, landscape: Boolean): Creator? {
    return when (this) {
        is FrameLayout -> FrameLayoutCreator(this, tag, landscape)
        is RelativeLayout -> RelativeLayoutCreator(this, tag, landscape)
        else -> null
    }
}

private fun ViewGroup.setStatusBarPadding(fitWindow: Boolean) {
    setPadding(
        paddingLeft,
        if (fitWindow) statusBarHeight else 0,
        paddingRight,
        paddingBottom
    )
}

private fun ViewGroup.setNavigationBarPadding(landscape: Boolean, fitWindow: Boolean) {
    if (landscape) {
        setPadding(
            paddingLeft,
            paddingTop,
            if (fitWindow) navigationBarHeight else 0,
            paddingBottom
        )
    } else {
        setPadding(
            paddingLeft,
            paddingTop,
            paddingRight,
            if (fitWindow) navigationBarHeight else 0
        )
    }
}

private fun View.updateBackground(config: BarConfig, endVersion: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        && Build.VERSION.SDK_INT < endVersion
        && config.light
        && updateBackground(config.lvlBackground)
    ) {
        return
    }
    updateBackground(config.background)
}

private fun View.updateBackground(background: BarBackground): Boolean {
    when {
        background.drawableRes > 0 -> {
            setBackgroundResource(background.drawableRes)
            return true
        }

        background.colorRes > 0 -> {
            setBackgroundColor(context.getColorInt(background.colorRes))
            return true
        }

        background.color > Color.BLACK - 1 -> {
            setBackgroundColor(background.color)
            return true
        }

        else -> setBackgroundColor(Color.TRANSPARENT)
    }
    return false
}

/**
 *  给 View 的顶部增加状态栏高度的 padding
 *  一般在状态栏透明且可被侵入的时候使用
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun View.addStatusBarTopPadding() {
    setPadding(paddingLeft, paddingTop + statusBarHeight, paddingRight, paddingBottom)
    val lp = layoutParams
    when (this) {
        is Toolbar -> {
            when (lp.height) {
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT -> {
                    post {
                        lp.height = height + statusBarHeight
                        layoutParams = lp
                    }
                }

                else -> {
                    lp.height += statusBarHeight
                    layoutParams = lp
                }
            }
        }

        else -> {
            when (lp.height) {
                ViewGroup.LayoutParams.WRAP_CONTENT -> return
                ViewGroup.LayoutParams.MATCH_PARENT -> {
                    post {
                        lp.height = height + statusBarHeight
                        layoutParams = lp
                    }
                }

                else -> {
                    lp.height += statusBarHeight
                    layoutParams = lp
                }
            }
        }
    }
}

/**
 *  给 View 的底部增加导航栏高度的 padding
 *  一般在导航栏透明且可被侵入的时候使用
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
internal fun View.addNavigationBarBottomPadding() {
    val ctx = context
    if (ctx is FragmentActivity && !manager.rom.navigationBarExist(ctx)) {
        return
    }
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + navigationBarHeight)
    val lp = layoutParams
    when (this) {
        is Toolbar -> {
            when (lp.height) {
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT -> {
                    post {
                        lp.height = height + navigationBarHeight
                        layoutParams = lp
                    }
                }

                else -> {
                    lp.height += navigationBarHeight
                    layoutParams = lp
                }
            }
        }

        else -> {
            when (lp.height) {
                ViewGroup.LayoutParams.WRAP_CONTENT -> return
                ViewGroup.LayoutParams.MATCH_PARENT -> {
                    post {
                        lp.height = height + navigationBarHeight
                        layoutParams = lp
                    }
                }

                else -> {
                    lp.height += navigationBarHeight
                    layoutParams = lp
                }
            }
        }
    }
}

private fun FragmentActivity.fixBottomNavigationViewPadding() {
    rootView?.fixBottomNavigationViewPadding()
}

private fun Fragment.fixBottomNavigationViewPadding() {
    view?.fixBottomNavigationViewPadding()
}

private fun View.fixBottomNavigationViewPadding() {
    for (view in children) {
        if (view is BottomNavigationView) {
            val originBottomPadding = view.paddingBottom
            view.post {
                view.setPadding(
                    view.paddingLeft,
                    view.paddingTop,
                    view.paddingRight,
                    originBottomPadding
                )
            }
        }
    }
}

internal fun FragmentActivity.addKeyboardListener() {
    rootView?.run {
        onKeyboardOpen {
            if (window?.attributes?.softInputMode?.contain(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) == true) {
                val lp = layoutParams ?: return@onKeyboardOpen
                lp.height = height - it
                layoutParams = lp
            }
        }
        onKeyboardClose {
            if (window?.attributes?.softInputMode?.contain(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) == true) {
                val lp = layoutParams ?: return@onKeyboardClose
                lp.height = it
                layoutParams = lp
            }
        }
    }
}