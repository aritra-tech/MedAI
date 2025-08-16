package com.aritradas.medai.ui.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsCard(
    isFirstItem: Boolean = false,
    isLastItem: Boolean = false,
    itemName: String,
    itemSubText: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconVector: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isFirstItem) Modifier.clip(
                    RoundedCornerShape(
                        topStart = 25.dp,
                        topEnd = 20.dp,
                        bottomEnd = 4.dp,
                        bottomStart = 4.dp
                    )
                )
                else if (isLastItem) Modifier.clip(
                    RoundedCornerShape(
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp,
                        topEnd = 4.dp,
                        topStart = 4.dp
                    )
                )
                else Modifier.clip(
                    RoundedCornerShape(
                        4.dp
                    )
                )
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (iconVector != null) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = iconVector,
                contentDescription = "Icon",
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(14.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.titleMedium
            )

            if (!itemSubText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = itemSubText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
