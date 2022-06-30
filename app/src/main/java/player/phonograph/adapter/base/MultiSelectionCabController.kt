/*
 * Copyright (c) 2022 chr_56 & Abou Zeid (kabouzeid) (original author)
 */

package player.phonograph.adapter.base

import android.content.Context
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import lib.phonograph.cab.CabStatus
import lib.phonograph.cab.ToolbarCab
import player.phonograph.R
import player.phonograph.util.ImageUtil.getTintedDrawable
import player.phonograph.util.PhonographColorUtil
import util.mdcolor.pref.ThemeColor

class MultiSelectionCabController(val cab: ToolbarCab) {

    fun showContent(context: Context, checkedListSize: Int, @MenuRes menuRes: Int): Boolean {
        return if (cab.status != CabStatus.STATUS_DESTROYED || cab.status != CabStatus.STATUS_DESTROYING) {
            if (checkedListSize < 1) {
                cab.hide()
            } else {

                cab.backgroundColor = PhonographColorUtil.shiftBackgroundColorForLightText(ThemeColor.primaryColor(context))
                cab.titleText = context.getString(R.string.x_selected, checkedListSize)
                cab.titleTextColor = Color.WHITE
                cab.navigationIcon = context.getTintedDrawable(R.drawable.ic_close_white_24dp, Color.WHITE)!!

                cab.menuRes = menuRes
                cab.menuItemClickListener = Toolbar.OnMenuItemClickListener {
                    onMenuItemClick(it)
                }
                cab.closeClickListener = View.OnClickListener {
                    dismiss()
                }
                cab.show()
            }
            true
        } else {
            false
        }
    }

    var onMenuItemClick: (MenuItem) -> Boolean = { false }
    var onDismiss: () -> Unit = {}
    fun dismiss(): Boolean {
        if (cab.status == CabStatus.STATUS_ACTIVE) {
            cab.hide()
            onDismiss()
            return true
        }
        return false
    }

    fun distroy(): Boolean = cab.destroy()

    fun isActive(): Boolean = cab.status == CabStatus.STATUS_ACTIVE
}
