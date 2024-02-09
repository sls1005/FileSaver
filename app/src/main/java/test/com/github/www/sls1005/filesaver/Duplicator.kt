package test.com.github.www.sls1005.filesaver

import android.app.Activity
import androidx.documentfile.provider.DocumentFile
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import java.io.File

class Duplicator: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stored = File(
            applicationContext.filesDir,
            "URI.txt"
        )
        if (! stored.exists()) {
            displayErrorMsgAndFinish(getString(R.string.error1))
            return
        }
        val uri = Uri.parse(stored.readText())
        val cr = this.getContentResolver()
        val data = this.getIntent().getData()
        val dir = DocumentFile.fromTreeUri(this, uri)
        if (data == null || dir == null) {
            displayGeneralErrorMsgAndFinish()
            return
        }
        val filename = DocumentFile.fromSingleUri(this, data).let {
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
            cr.getType(data).let {
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
        val src = cr.openInputStream(data) ?: run {
            displayGeneralErrorMsgAndFinish()
            return
        }
        src.copyTo(dst)
        src.close()
        dst.close()
        Toast.makeText(this, getString(R.string.success1), Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
    private fun displayErrorMsgAndFinish(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        setResult(RESULT_CANCELED)
        finish()
    }
    private fun displayGeneralErrorMsgAndFinish() {
        displayErrorMsgAndFinish(getString(R.string.error0))
    }
}