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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Checkbox
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
import test.com.github.www.sls1005.filesaver.ui.theme.FileSaverTheme
import java.io.File

class MainActivity : ComponentActivity() {
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
                if (hasPermission(this, previouslyStored)) {
                    contentResolver.releasePersistableUriPermission(
                        previouslyStored,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                file.delete()
            }
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            file.writeText(uri.toString())
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
                        val pathSet = (getStoredUri() != null)
                        var duplicator1Enabled by remember { mutableStateOf(false) }
                        var duplicator2Enabled by remember { mutableStateOf(false) }
                        var checkbox1Checked by remember { mutableStateOf(false) }
                        var checkbox2Checked by remember { mutableStateOf(false) }
                        val expired = getStoredUri().let {
                            if (it != null) {
                                (! hasPermission(this@MainActivity, it))
                            } else {
                                false
                            }
                        }
                        getDuplicatorEnabled(1, this@MainActivity).let {
                            duplicator1Enabled = it
                            checkbox1Checked = it
                        }
                        getDuplicatorEnabled(2, this@MainActivity).let {
                            duplicator2Enabled = it
                            checkbox2Checked = it
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
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = checkbox1Checked,
                                    onCheckedChange = { checkbox1Checked = it },
                                    modifier = Modifier.padding(10.dp)
                                )
                                Text(
                                    stringResource(id = R.string.text1),
                                    fontSize = 24.sp,
                                    lineHeight = 26.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = checkbox2Checked,
                                    onCheckedChange = { checkbox2Checked = it },
                                    modifier = Modifier.padding(10.dp)
                                )
                                Text(
                                    stringResource(id = R.string.text2),
                                    fontSize = 24.sp,
                                    lineHeight = 26.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Box (
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (!(
                                    (checkbox1Checked == duplicator1Enabled)
                                            &&
                                    (checkbox2Checked == duplicator2Enabled)
                                )) {
                                    val msg = stringResource(id = R.string.plz)
                                    OutlinedButton(
                                        onClick = {
                                            if (
                                                (checkbox1Checked || checkbox2Checked)
                                                        &&
                                                (getStoredUri() == null)
                                            ) {
                                                launcher.launch(null)
                                                showMsg(this@MainActivity, msg)
                                            }
                                            listOf(
                                                checkbox1Checked,
                                                checkbox2Checked
                                            ).forEachIndexed { idx, checked ->
                                                if (checked != getDuplicatorEnabled(idx + 1, this@MainActivity)) {
                                                    setDuplicatorEnabled(idx + 1, checked, this@MainActivity)
                                                }
                                            }
                                            getDuplicatorEnabled(1, this@MainActivity).let {
                                                duplicator1Enabled = it
                                                checkbox1Checked = it
                                            }
                                            getDuplicatorEnabled(2, this@MainActivity).let {
                                                duplicator2Enabled = it
                                                checkbox2Checked = it
                                            }
                                        },
                                        modifier = Modifier.padding(15.dp)
                                    ) {
                                        Text(stringResource(id = R.string.button1), fontSize = 26.sp)
                                    }
                                }
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
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
                                },
                                modifier = Modifier.fillMaxWidth().padding(10.dp)
                            ) {
                                Text(
                                    if (pathSet) {
                                        getStoredUri()?.path ?: ""
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
                                        if (expired) {
                                            stringResource(id = R.string.hint1_2)
                                        } else {
                                            stringResource(id = R.string.hint1_1)
                                        }
                                    } else {
                                        ""
                                    }, fontSize = 24.sp,
                                    lineHeight = 36.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
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
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.title4),
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                            Text(
                                stringResource(id = R.string.text3_1),
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
                                stringResource(id = R.string.text3_2),
                                fontSize = 24.sp,
                                lineHeight = 36.sp,
                                modifier = Modifier.padding(5.dp)
                            )
                            TextButton(
                                onClick = {
                                    startActivity(
                                        Intent(this@MainActivity, ShowOpenSourceLibrariesActivity::class.java)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().padding(10.dp)
                            ) {
                                Text(stringResource(id = R.string.display_licenses), fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun getStoredUri(): Uri? {
        val stored = File(
            applicationContext.filesDir,
            "URI.txt"
        )
        if (! stored.exists()) {
            return null
        }
        return Uri.parse(stored.readText())
    }
}

internal fun showMsg(ctx: Context, msg: String) {
    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
}

internal fun hasPermission(ctx: Context, uri: Uri): Boolean {
    ctx.contentResolver.persistedUriPermissions.forEach {
        if (it.uri == uri) {
            return true
        }
    }
    return false
}

internal fun getDuplicatorEnabled(id: Int, ctx: Context): Boolean {
    return if (id in 1..2) {
        ctx.getPackageManager().getComponentEnabledSetting(
            ComponentName(
                ctx,
                when(id) {
                    1 -> DuplicatorI::class.java
                    2 -> DuplicatorII::class.java
                    else -> return false
                }
            )
        ) in listOf(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
    } else {
        false
    }
}

internal fun setDuplicatorEnabled(id: Int, e: Boolean, ctx: Context) {
    if (id in 1..2) {
        ctx.getPackageManager().setComponentEnabledSetting(
            ComponentName(
                ctx,
                when(id) {
                    1 -> DuplicatorI::class.java
                    2 -> DuplicatorII::class.java
                    else -> return
                }
            ),
            if (e) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            },
            PackageManager.DONT_KILL_APP
        )
    }
}
