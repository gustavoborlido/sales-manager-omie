package com.omie.salesmanager.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.omie.salesmanager.ui.theme.Black
import com.omie.salesmanager.ui.theme.Blue

@Composable
fun SalesFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentDescription: String? = null,
    icon: ImageVector = Icons.Filled.Add,
    iconTint: Color = Black,
    containerColor: Color = Blue
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = containerColor
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint
        )
    }
}
