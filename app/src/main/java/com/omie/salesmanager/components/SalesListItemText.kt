package com.omie.salesmanager.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SalesListItemText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp),
    spacerHeight: Dp = 8.dp,
    spacerBefore: Boolean = true,
    spacerAfter: Boolean = true
) {
    Column(modifier = modifier) {
        if (spacerBefore) {
            Spacer(modifier = Modifier.height(spacerHeight))
        }
        Text(
            text = text,
            fontSize = fontSize,
            style = textStyle,
            color = color,
            modifier = Modifier.padding(paddingValues)
        )
        if (spacerAfter) {
            Spacer(modifier = Modifier.height(spacerHeight))
        }
    }
}
