package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoRequestPacket
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoResponsePacket
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.currentParties
import de.bergwerklabs.party.server.packageService

/**
 * Created by Yannic Rieger on 28.09.2017.
 *
 * @author Yannic Rieger
 */
class PartyInfoRequestListener : AtlantisPackageListener<PartyInfoRequestPacket>() {
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    
    override fun onResponse(pkg: PartyInfoRequestPacket) {
        logger.info("Sending back party information for player ${pkg.player}")
        val party = currentParties.values.firstOrNull { atlantisParty -> atlantisParty.members.contains(pkg.player) }
        logger.info("Party is: $party")
        logger.info("Party can be 'null' if player is not partied.")
        packageService.sendResponse(PartyInfoResponsePacket(pkg.player, party), pkg)
    }
}