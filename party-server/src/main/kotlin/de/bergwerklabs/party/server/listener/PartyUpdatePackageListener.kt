package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdate
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePackage
import de.bergwerklabs.atlantis.intern.networkComponent.shared.ConsumerPipeline
import de.bergwerklabs.atlantis.intern.networkComponent.shared.PackageReceivedListener
import de.bergwerklabs.party.server.AtlantisParty
import de.bergwerklabs.party.server.currentParties
import java.util.*

/**
 * Created by Yannic Rieger on 21.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyUpdatePackageListener(acceptedClass: Class<PartyUpdatePackage>?) : PackageReceivedListener<PartyUpdatePackage>(acceptedClass) {
    
    private val logger = AtlantisLogger.getLogger(PartyUpdatePackageListener::class.java)
    
    override fun onPackageReceived(pkg: PartyUpdatePackage?, p1: ConsumerPipeline?) {
        if (pkg != null) {
            when (pkg.update) {
                PartyUpdate.PLAYER_JOIN  -> this.handlePlayerJoin(pkg.player, pkg.partyId)
                PartyUpdate.PLAYER_KICK  -> this.handlePlayerKick(pkg.player, pkg.partyId)
                PartyUpdate.PLAYER_LEAVE -> this.handlePlayerLeave(pkg.player, pkg.partyId)
            }
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