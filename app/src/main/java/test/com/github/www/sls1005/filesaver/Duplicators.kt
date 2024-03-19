package test.com.github.www.sls1005.filesaver

import android.app.Activity
import android.content.Intent
import androidx.documentfile.provider.DocumentFile
import android.net.Uri
import android.os.Build
import android.os.Bundle
import java.io.File

open class DuplicatorI : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveFile()
    }
    protected fun saveFile(uri: Uri?) {
        if (uri == null) {
            displayGeneralErrorMsgAndFinish()
            return
        }
        val stored = File(
            applicationContext.filesDir,
            "URI.txt"
        )
        if (!stored.exists()) {
            displayErrorMsgAndFinish(getString(R.string.error1))
            return
        }
        val cr = this.getContentResolver()
        val storedUri = Uri.parse(stored.readText())
        if (! hasPermission(this, storedUri)) {
            displayErrorMsgAndFinish(getString(R.string.error2))
            return
        }
        val dir = DocumentFile.fromTreeUri(this, storedUri)
        if (dir == null) {
            displayGeneralErrorMsgAndFinish()
            return
        }
        val filename = DocumentFile.fromSingleUri(this, uri).let {
            if (it == null) {
                displayGeneralErrorMsgAndFinish()
                return
            } else {
                it.name ?: run {
                    displayGeneralErrorMsgAndFinish()
                    return
                }
            }
        }
        val f = dir.createFile(
            cr.getType(uri).let {
                it ?: run {
                    displayGeneralErrorMsgAndFinish()
                    return
                }
            }, filename
        ).let {
            it ?: run {
                displayGeneralErrorMsgAndFinish()
                return
            }
        }
        val dst = cr.openOutputStream(f.uri) ?: run {
            displayGeneralErrorMsgAndFinish()
            return
        }
        val src = cr.openInputStream(uri) ?: run {
            displayGeneralErrorMsgAndFinish()
            return
        }
        src.copyTo(dst)
        src.close()
        dst.close()
        showMsg(this, getString(R.string.success1))
        setResult(RESULT_OK)
        finish()
    }
    private fun displayErrorMsgAndFinish(msg: String) {
        showMsg(this, msg)
        setResult(RESULT_CANCELED)
        finish()
    }
    private fun displayGeneralErrorMsgAndFinish() {
        displayErrorMsgAndFinish(getString(R.string.error0))
    }
    protected open fun saveFile() {
        saveFile(intent.data)
    }
}

class DuplicatorII : DuplicatorI() {
    override fun saveFile() {
        saveFile(
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            } else {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            }
        )
    }
}
