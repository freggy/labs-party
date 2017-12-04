package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.party.packages.PartySavePacket
import de.bergwerklabs.party.server.AtlantisPackageListener

/**
 * Created by Yannic Rieger on 21.09.2017.
 *
 * @author Yannic Rieger
 */
class PartySavePackageListener : AtlantisPackageListener<PartySavePacket>() {
    
    override fun onResponse(pkg: PartySavePacket) {
        // TODO: save party to db
    }
}