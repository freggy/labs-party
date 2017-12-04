package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.PartyDisbandPacket
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.currentParties
import de.bergwerklabs.party.server.pendingInvites

/**
 * Created by Yannic Rieger on 21.09.2017.
 *
 * @author Yannic Rieger
 */
class PartyDisbandListener : AtlantisPackageListener<PartyDisbandPacket>() {
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    
    override fun onResponse(pkg: PartyDisbandPacket) {
        logger.info("Disbanding party ${pkg.partyId}")
        currentParties.remove(pkg.partyId)
        pendingInvites.remove(pkg.partyId)
    }
}
