package io.horizontalsystems.bankwallet.modules.balance2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonSecondaryDefault

@Composable
fun BalanceItemsEmptyRegularAccount(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(ComposeAppTheme.colors.jeremy),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_to_wallet_2_48),
                contentDescription = null,
                tint = ComposeAppTheme.colors.grey
            )
        }
        Spacer(modifier = Modifier.height(26.dp))
        Text(
            modifier = Modifier.padding(horizontal = 48.dp),
            text = stringResource(id = R.string.Balance_NoCoinsAlert),
            color = ComposeAppTheme.colors.grey,
            style = ComposeAppTheme.typography.subhead2,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(38.dp))
        ButtonSecondaryDefault(
            title = stringResource(id = R.string.Balance_AddCoins),
            onClick = {
                navController.slideFromRight(R.id.manageWalletsFragment)
            }
        )
    }
}