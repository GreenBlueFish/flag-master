package com.greenbluefish.flagmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.greenbluefish.flagmaster.nav.NavGraph
import com.greenbluefish.flagmaster.ui.theme.FlagMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlagMasterTheme {
                NavGraph()
            }
        }
    }
}
