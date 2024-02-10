package test.com.github.www.sls1005.filesaver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import test.com.github.www.sls1005.filesaver.ui.theme.FileSaverTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private var shouldEnableDuplicatorInCallback = false
    private val launcher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            val file = File(
                applicationContext.filesDir,
                "URI.txt"
            )
            if (file.exists()) {
                val previouslyStored = Uri.parse(file.readText())
                contentResolver.releasePersistableUriPermission(
                    previouslyStored,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                file.delete()
            }
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            file.writeText(uri.toString())
            if (shouldEnableDuplicatorInCallback) {
                if (!duplicatorEnabled()) {
                    setDuplicatorEnabled(true)
                }
                shouldEnableDuplicatorInCallback = false
            }
        }
    }
    override fun onResume() {
        super.onResume()
        setContent {
            FileSaverTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        val pathSet = (getStoredPath() != null)
                        var enabled by remember { mutableStateOf(false) }
                        enabled = duplicatorEnabled().let {
                            if (it && !pathSet) {
                                setDuplicatorEnabled(false)
                                false
                            } else {
                                it
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.title1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                            Box (
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    if (enabled) {
                                        stringResource(id = R.string.main_function_enabled)
                                    } else {
                                        stringResource(id = R.string.main_function_disabled)
                                    }, fontSize = 24.sp,
                                    lineHeight = 36.sp,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                            Box (
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val msg = stringResource(id = R.string.plz)
                                OutlinedButton(
                                    onClick = {
                                        if (duplicatorEnabled()) {
                                            setDuplicatorEnabled(false)
                                        } else {
                                            if (getStoredPath() == null) {
                                                launcher.launch(null)
                                                showMsg(this@MainActivity, msg)
                                                shouldEnableDuplicatorInCallback = true
                                            } else {
                                                setDuplicatorEnabled(true)
                                            }
                                        }
                                        enabled = duplicatorEnabled()
                                    },
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Text(
                                        if (enabled) {
                                            stringResource(id = R.string.stop_saving_files)
                                        } else {
                                            stringResource(id = R.string.start_saving_files)
                                        }, fontSize = 24.sp
                                    )
                                }
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.title2),
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                            TextButton(
                                onClick = {
                                    launcher.launch(null)
                                }, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Text(
                                    if (pathSet) {
                                        getStoredPath() ?: ""
                                    } else {
                                        stringResource(id = R.string.setting_up)
                                    }, fontSize = 24.sp
                                )
                            }
                            Box (
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (pathSet) {
                                        stringResource(id = R.string.hint1)
                                    } else {
                                        ""
                                    }, fontSize = 24.sp,
                                    lineHeight = 36.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.title3),
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                            Box(
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    stringResource(id = R.string.hint2_1),
                                    fontSize = 24.sp,
                                    lineHeight = 36.sp,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    stringResource(id = R.string.hint2_2),
                                    fontSize = 24.sp,
                                    lineHeight = 36.sp,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.title4),
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                            Text(
                                stringResource(id = R.string.text1_1),
                                fontSize = 24.sp,
                                lineHeight = 36.sp,
                                modifier = Modifier.padding(5.dp)
                            )
                            val url = stringResource(id = R.string.url1)
                            Text(
                                url,
                                fontSize = 24.sp,
                                lineHeight = 36.sp,
                                color = Color(0xFF3792FA),
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable {
                                        startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        )
                                    }
                            )
                            Text(
                                stringResource(id = R.string.text1_2),
                                fontSize = 24.sp,
                                lineHeight = 36.sp,
                                modifier = Modifier.padding(5.dp)
                            )
                            val title = stringResource(id = R.string.title5)
                            TextButton(
                                onClick = {
                                    startActivity(
                                        Intent(this@MainActivity, OssLicensesMenuActivity::class.java).apply {
                                            putExtra("title", title)
                                        }
                                    )
                                }, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Text(stringResource(id = R.string.display_licenses), fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getStoredPath(): String? {
        val stored = File(
            applicationContext.filesDir,
            "URI.txt"
        )
        if (! stored.exists()) {
            return null
        }
        return Uri.parse(stored.readText()).path
    }
    private fun duplicatorEnabled(): Boolean {
        return (packageManager.getComponentEnabledSetting(ComponentName(this, Duplicator::class.java)) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
    }
    private fun setDuplicatorEnabled(e: Boolean) {
        packageManager.setComponentEnabledSetting(
            ComponentName(this, Duplicator::class.java),
            if (e) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                   },
            PackageManager.DONT_KILL_APP
        )
    }
}

internal fun showMsg(ctx: Context, msg: String) {
    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
}