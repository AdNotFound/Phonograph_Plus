/*
 * Copyright (c) 2022 chr_56
 */

package player.phonograph.ui.dialogs

import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import player.phonograph.R
import player.phonograph.ui.compose.BridgeDialogFragment
import player.phonograph.ui.compose.PhonographTheme
import player.phonograph.ui.compose.components.DialogContent
import player.phonograph.ui.compose.dialogs.ClickModeSettingDialogContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class ClickModeSettingDialog : BridgeDialogFragment() {
    @Composable
    override fun Content() {
        val dialogState = rememberMaterialDialogState(true)
        PhonographTheme {
            MaterialDialog(
                dialogState = dialogState,
                elevation = 0.dp,
                onCloseRequest = { dismiss() },
                buttons = {
                    button(
                        res = android.R.string.ok,
                        textStyle = MaterialTheme.typography.button.copy(color = MaterialTheme.colors.secondary)
                    ) {
                        dismiss()
                    }
                }
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                title(res = R.string.pref_title_click_behavior)
                customView {
                    DialogContent {
                        ClickModeSettingDialogContent(requireContext())
                    }
                }
            }
        }
    }
}