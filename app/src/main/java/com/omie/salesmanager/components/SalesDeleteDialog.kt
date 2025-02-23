package com.omie.salesmanager.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.omie.salesmanager.R
import com.omie.salesmanager.ui.theme.Black
import com.omie.salesmanager.ui.theme.Dimens
import com.omie.salesmanager.ui.theme.White

@Composable
fun SalesDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.sales_dialog_delete_title_message)) },
        text = { Text(text = stringResource(R.string.sales_dialog_delete_subtitle_message)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = stringResource(R.string.sales_dialog_delete_button), color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.sales_dialog_cancel_button), color = Black)
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false),
        shape = RoundedCornerShape(Dimens.Size.small)
    )
}
