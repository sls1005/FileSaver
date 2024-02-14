package test.com.github.www.sls1005.filesaver

import android.app.Activity
import android.content.Intent
import androidx.documentfile.provider.DocumentFile
import android.net.Uri
import android.os.Build
import android.os.Bundle
import java.io.File

class DuplicatorI : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveFile(intent.data, this)
    }
}

class DuplicatorII : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveFile(
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            } else {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            },
            this
        )
    }
}

private fun saveFile(uri: Uri?, ctx: Activity) {
    with(ctx) {
        if (uri == null) {
            displayGeneralErrorMsgAndFinish(this)
            return
        }
        val stored = File(
            applicationContext.filesDir,
            "URI.txt"
        )
        if (!stored.exists()) {
            displayErrorMsgAndFinish(this, getString(R.string.error1))
            return
        }
        val cr = this.getContentResolver()
        val storedUri = Uri.parse(stored.readText())
        val dir = DocumentFile.fromTreeUri(this, storedUri)
        if (dir == null) {
            displayGeneralErrorMsgAndFinish(this)
            return
        } else if (!dir.canWrite()) {
            displayErrorMsgAndFinish(this, getString(R.string.error2))
            return
        }
        val filename = DocumentFile.fromSingleUri(this, uri).let {
            if (it == null) {
                displayGeneralErrorMsgAndFinish(this)
                return
            } else {
                it.name ?: run {
                    displayGeneralErrorMsgAndFinish(this)
                    return
                }
            }
        }
        val f = dir.createFile(
            cr.getType(uri).let {
                it ?: run {
                    displayGeneralErrorMsgAndFinish(this)
                    return
                }
            }, filename
        ).let {
            it ?: run {
                displayGeneralErrorMsgAndFinish(this)
                return
            }
        }
        val dst = cr.openOutputStream(f.uri) ?: run {
            displayGeneralErrorMsgAndFinish(this)
            return
        }
        val src = cr.openInputStream(uri) ?: run {
            displayGeneralErrorMsgAndFinish(this)
            return
        }
        src.copyTo(dst)
        src.close()
        dst.close()
        showMsg(this, getString(R.string.success1))
        setResult(Activity.RESULT_OK)
        finish()
    }
}

private fun displayErrorMsgAndFinish(ctx: Activity, msg: String) {
    with(ctx) {
        showMsg(this, msg)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}

private fun displayGeneralErrorMsgAndFinish(ctx: Activity) {
    with(ctx) {
        displayErrorMsgAndFinish(ctx, getString(R.string.error0))
    }
}
