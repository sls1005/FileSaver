package test.com.github.www.sls1005.filesaver

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

open class TileServiceI: TileService() {
    override fun onTileAdded() {
        super.onTileAdded()
        update()
    }
    override fun onStartListening() {
        super.onStartListening()
        update()
    }
    override fun onClick() {
        super.onClick()
        respond()
    }
    protected fun update(id: Int) {
        qsTile.state = (
            if (getDuplicatorEnabled(id, this)) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
        )
        qsTile.updateTile()
    }
    protected open fun update() {
        update(1)
    }
    protected fun respond(id: Int) {
        setDuplicatorEnabled(
            id, !getDuplicatorEnabled(id, this), this
        )
        update()
    }
    protected open fun respond() {
        respond(1)
    }
}

class TileServiceII: TileServiceI() {
    override fun update() {
        update(2)
    }
    override fun respond() {
        respond(2)
    }
}