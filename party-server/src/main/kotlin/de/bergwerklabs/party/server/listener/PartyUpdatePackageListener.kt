package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdate
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePackage
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.currentParties
import java.util.*

/**
 * Created by Yannic Rieger on 21.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyUpdatePackageListener : AtlantisPackageListener<PartyUpdatePackage>() {
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    
    override fun onResponse(pkg: PartyUpdatePackage) {
        when (pkg.update) {
            PartyUpdate.PLAYER_JOIN  -> this.handlePlayerJoin(pkg.player, pkg.partyId)
            PartyUpdate.PLAYER_KICK  -> this.handlePlayerKick(pkg.player, pkg.partyId)
            PartyUpdate.PLAYER_LEAVE -> this.handlePlayerLeave(pkg.player, pkg.partyId)
        }
    }
    
    private fun handlePlayerJoin(player: UUID, partyId: UUID) {
        logger.info("Player $player joined party $partyId")
        currentParties[partyId]?.members?.add(player)
    }
    
    private fun handlePlayerKick(player: UUID, partyId: UUID) {
        logger.info("Player $player was kicked from party $partyId")
        this.removeFromParty(currentParties[partyId], player)
    }
    
    private fun handlePlayerLeave(player: UUID, partyId: UUID) {
        logger.info("Player $player left party $partyId")
        this.removeFromParty(currentParties[partyId], player)
    }
    
    private fun removeFromParty(party: AtlantisParty?, player: UUID) {
        if (party?.owner != player) {
            party?.members?.remove(player)
        }
    }
}