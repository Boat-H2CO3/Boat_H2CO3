package com.zackratos.ultimatebarx.ultimatebarx.bean

import android.os.Build
import java.util.Objects

/**
 * @Author   : zhangwenchao
 * @Date     : 2020/6/26  7:27 PM
 * @email    : 869649338@qq.com
 * @Describe :
 */
class BarConfig {

    companion object {
        fun newInstance(): BarConfig =
            BarConfig().apply {
                background.defaultBg()
                lvlBackground.defaultBg()
                fitWindow = true
                light = false
            }
    }

    /**
     * 是否忽略状态栏或导航栏的占位高度（相当于 [android.view.View.setFitsSystemWindows]）
     * true  : contentView 位于状态栏和导航栏之间（不占用状态栏和导航栏位置）
     * false : contentView 可以伸到状态栏和导航栏的位置（沉浸式）
     */
    var fitWindow: Boolean = false
    var background: BarBackground = BarBackground.newInstance()
        set(value) {
            field.update(value)
        }

    /**
     *  light 模式（状态栏字体颜色变灰，导航栏内部按钮颜色变灰）
     *  true  : 状态栏字体灰色，导航栏按钮灰色
     *  false : 状态栏字体白色，导航栏按钮白色
     */
    var light: Boolean = false

    /**
     *  低版本 light 模式下，状态栏或导航栏重新设置背景
     *  因为低版本不支持 Light 模式，为了防止状态栏文字和导航栏按钮看不到
     *  只在 light 模式下使用，非 light 模式使用无效
     */
    var lvlBackground: BarBackground = BarBackground.newInstance()
        set(value) {
            field.update(value)
        }

    var color: Int
        get() = background.color
        set(value) {
            background.color = value
        }

    var colorRes: Int
        get() = background.colorRes
        set(value) {
            background.colorRes = value
        }

    var drawableRes: Int
        get() = background.drawableRes
        set(value) {
            background.drawableRes = value
        }

    var lvlColor: Int
        get() = lvlBackground.color
        set(value) {
            lvlBackground.color = value
        }

    var lvlColorRes: Int
        get() = lvlBackground.colorRes
        set(value) {
            lvlBackground.colorRes = value
        }

    var lvlDrawableRes: Int
        get() = lvlBackground.drawableRes
        set(value) {
            lvlBackground.drawableRes = value
        }


    fun transparent(): BarConfig =
        apply {
            fitWindow = false
            background.transparent()
        }


    fun update(config: BarConfig) {
        if (config == this) return
        this.fitWindow = config.fitWindow
        this.background.update(config.background)
        this.lvlBackground.update(config.lvlBackground)
        this.light = config.light
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BarConfig) {
            return false
        }
        if (this === other) {
            return true
        }
        return light == other.light
                && background == other.background
                && lvlBackground == other.lvlBackground
                && fitWindow == other.fitWindow
    }

    override fun hashCode(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(light, fitWindow, background, lvlBackground)
        }
        return super.hashCode()
    }

}