package com.omie.salesmanager.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.omie.salesmanager.R
import com.omie.salesmanager.ui.theme.Blue

@Composable
fun SalesClearIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.sales_order_clear_description_text),
            tint = Blue
        )
    }
}