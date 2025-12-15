package com.dominik.control.kidshield.ui.composable.components

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dominik.control.kidshield.data.model.dto.UserType

@Composable
fun UserTypeSelector(
    selected: UserType,
    onSelected: (UserType) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {

        UserType.entries.forEachIndexed { index, type ->
            if(type != UserType.ADMIN) {
                SegmentedButton(
                    selected = selected == type,
                    onClick = { onSelected(type) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index-1,
                        count = UserType.entries.size-1
                    )
                ) {
                    Text(
                        text = when (type) {
                            UserType.SUPERVISOR -> "Supervisor"
                            UserType.MONITORED -> "Child"
                            UserType.ADMIN -> "Admin"
                        }
                    )
                }
            }
        }
    }
}
