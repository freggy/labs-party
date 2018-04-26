package de.bergwerklabs.party.server.listener

import de.bergwerklabs.api.cache.pojo.PlayerNameToUuidMapping
import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.invite.*
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdate
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePacket
import de.bergwerklabs.atlantis.client.base.resolve.PlayerResolver
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.party.server.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Yannic Rieger on 04.10.2017.
 *
 * @author Yannic Rieger
 */
class PartyInviteRequestListener : AtlantisPackageListener<PartyClientInviteRequestPacket>() {
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    
    override fun onResponse(pkg: PartyClientInviteRequestPacket) {
        val party = currentParties[pkg.party.id]
        val opt = PlayerResolver.resolveNameToUuidAsync(pkg.invitedPlayer.name).join()
        
        if (!opt.isPresent) return
        val invited = opt.get()
        
        logger.info("Received invite request from ${pkg.sender} for ${pkg.invitedPlayer} to party ${pkg.party.id}")
        
        if (party != null) {
            if (party.members.size >= 7) {
                logger.info("Party is already full, sending error message back.")
                packageService.sendResponse(PartyServerInviteResponsePacket(pkg.party, null, pkg.sender, InviteStatus.PARTY_FULL), pkg)
                return
            }
        }
        else if (currentParties.values.any { p -> p.members.contains(invited.uuid) || p.owner == invited.uuid }) {
            logger.info("Invited player ${pkg.invitedPlayer} is already partied.")
            packageService.sendResponse(PartyServerInviteResponsePacket(pkg.party, PlayerNameToUuidMapping("", UUID.randomUUID()), pkg.sender, InviteStatus.ALREADY_PARTIED), pkg)
            return
        }
        else {
            logger.info("Party does not exist anymore, sending error message back.")
            packageService.sendResponse(PartyServerInviteResponsePacket(pkg.party, PlayerNameToUuidMapping("", UUID.randomUUID()), pkg.sender, InviteStatus.PARTY_NOT_PRESENT), pkg)
            return
        }
    
        logger.info("Invite is now pending, after 30 seconds it will be removed.")
        pendingInvites[invited.uuid] = System.currentTimeMillis()
        
        packageService.sendPackage(PartyServerInviteRequestPacket(party, invited, pkg.sender, null), PartyClientInviteResponsePacket::class.java, AtlantisPackageService.Callback { response ->
            val responseParty = currentParties[pkg.party.id]
            val clientResponse = response as PartyClientInviteResponsePacket
            
            if (responseParty != null) { // check if party is present
                if (pendingInvites.containsKey(clientResponse.responder.uuid)) { // check if invite is not expired.
                    if (party.members.size >= 7) {
                        logger.info("Party is already full, sending error message back...")
                        packageService.sendPackage(PartyServerInviteResponsePacket(responseParty, clientResponse.responder, clientResponse.initalSender, InviteStatus.PARTY_FULL))
                    }
                    else {
                        logger.info("Sending response of invited player...")
                        if (clientResponse.status == InviteStatus.ACCEPTED) {
                            currentParties[clientResponse.party.id]?.members?.add(clientResponse.responder.uuid)
                            pendingInvites.remove(clientResponse.responder.uuid)
                            packageService.sendPackage(PartyUpdatePacket(clientResponse.party, clientResponse.responder, PartyUpdate.PLAYER_JOIN))
                        }
                        packageService.sendPackage(PartyServerInviteResponsePacket(clientResponse.party, clientResponse.responder, clientResponse.initalSender, clientResponse.status))
                    }
                }
                else {
                    logger.info("Party invitation expired, sending error message back...")
                    packageService.sendPackage(PartyServerInviteResponsePacket(clientResponse.party, clientResponse.responder, clientResponse.initalSender, InviteStatus.EXPIRED))
                }
            }
            else {
                logger.info("Party not present anymore, sending error message back...")
                packageService.sendPackage(PartyServerInviteResponsePacket(clientResponse.party, clientResponse.responder, clientResponse.initalSender, InviteStatus.PARTY_NOT_PRESENT))
            }
        })
    }
}