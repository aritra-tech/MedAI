package com.aritradas.medai.ui.presentation.settings.component

import android.R.attr.textColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun SwitchCard(
    itemName: String,
    itemSubText: String? = null,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onChecked: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = itemName,
            )

            if (!itemSubText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = itemSubText,
                )
            }
        }

        Switch(
            modifier = Modifier.semantics {
                contentDescription = "Theme switcher"
            },
            checked = isChecked,
            onCheckedChange = onChecked
        )
    }
}
