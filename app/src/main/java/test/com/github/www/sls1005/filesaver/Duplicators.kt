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
        var errFlag = false
        if (uri == null) {
            showMsg(this, getString(R.string.error0))
            setResult(RESULT_CANCELED)
            finish()
            return
        }
        val stored = File(
            applicationContext.filesDir,
            "URI.txt"
        )
        if (!stored.exists()) {
            showMsg(this, getString(R.string.error1))
            setResult(RESULT_CANCELED)
            finish()
            return
        }
        val cr = getContentResolver()
        val storedUri = Uri.parse(stored.readText())
        if (! hasPermission(this, storedUri)) {
            showMsg(this, getString(R.string.error2))
            setResult(RESULT_CANCELED)
            finish()
            return
        }
        DocumentFile.fromTreeUri(this, storedUri)?.also { dir ->
            DocumentFile.fromSingleUri(this, uri)?.name?.also { filename ->
                cr.getType(uri)?.also { mime ->
                    dir.createFile(mime, filename)?.also { outputFile ->
                        cr.openOutputStream(outputFile.uri)?.also { dst ->
                            cr.openInputStream(uri)?.also { src ->
                                src.copyTo(dst)
                                src.close()
                                dst.close()
                            } ?: run {
                                errFlag = true
                            }
                        } ?: run {
                            errFlag = true
                        }
                    } ?: run {
                        errFlag = true
                    }
                } ?: run {
                    errFlag = true
                }
            } ?: run {
                errFlag = true
            }
        } ?: run {
            errFlag = true
        }
        if (errFlag) {
            showMsg(this, getString(R.string.error0))
            setResult(RESULT_CANCELED)
        } else {
            showMsg(this, getString(R.string.success1))
            setResult(RESULT_OK)
        }
        finish()
    }
    protected open fun saveFile() {
        saveFile(intent?.data /*?: null*/)
    }
}

class DuplicatorII : DuplicatorI() {
    override fun saveFile() {
        saveFile(
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            } else {
                intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            }
        )
    }
}
