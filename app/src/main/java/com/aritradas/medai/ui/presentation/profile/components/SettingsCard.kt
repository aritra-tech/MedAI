package com.aritradas.medai.ui.presentation.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingsCard(
    itemName: String,
    itemSubText: String? = null,
    iconId: Int? = null,
    iconVector: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconId != null || iconVector != null) {
            when {
                iconVector != null -> {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        imageVector = iconVector,
                        contentDescription = "Icon",
                    )
                }
                iconId != null -> {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(iconId),
                        contentDescription = "Icon",
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))

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
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
