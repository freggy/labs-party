package de.bergwerklabs.party.server.listener.invite

import de.bergwerklabs.atlantis.api.packages.APackage
import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.invite.*
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageCallback
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.server.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Yannic Rieger on 04.10.2017.
 *
 * Listens for the [PartyClientInviteRequestPackage]
 *
 * @author Yannic Rieger
 */
class PartyInviteRequestListener : AtlantisPackageListener<PartyClientInviteRequestPackage>() {
    
    override fun onResponse(pkg: PartyClientInviteRequestPackage) {
        val party = currentParties[pkg.partyId]
        
        if (party != null) {
            if (party.members.size >= 7) {
                packageService.sendResponse(PartyServerInviteResponsePackage(pkg.partyId, InviteStatus.PARTY_FULL, pkg.playerUuid), pkg)
                return
            }
        }
        else {
            packageService.sendResponse(PartyServerInviteResponsePackage(pkg.partyId, InviteStatus.PARTY_NOT_PRESENT, pkg.playerUuid), pkg)
            return
        }
        
        pendingInvites.computeIfAbsent(pkg.partyId, { uuid -> CopyOnWriteArrayList() })
        pendingInvites[pkg.partyId]!!.add(InviteInfo(pkg.playerUuid, System.currentTimeMillis()))
        
        AtlantisPackageUtil.sendPackage(PartyServerInviteRequestPackage(party.id, pkg.playerUuid), AtlantisPackageCallback { response ->
            val responseParty = pendingInvites[pkg.partyId]
            val clientResponse = response as PartyClientInviteResponsePackage
            
            if (responseParty != null) { // check if party is present
                if (responseParty.any { inviteInfo -> inviteInfo.player == clientResponse.player }) { // check if invite is not expired.
                    
                    if (party.members.size >= 7) {
                        packageService.sendResponse(PartyServerInviteResponsePackage(pkg.partyId, InviteStatus.PARTY_FULL, pkg.playerUuid), pkg)
                    }
                    else packageService.sendResponse(PartyServerInviteResponsePackage(clientResponse.partyId, clientResponse.status, clientResponse.player), pkg)
                }
                else packageService.sendResponse(PartyServerInviteResponsePackage(clientResponse.partyId, InviteStatus.EXPIRED, clientResponse.player), pkg)
            }
            else packageService.sendResponse(PartyServerInviteResponsePackage(clientResponse.partyId, InviteStatus.PARTY_NOT_PRESENT, clientResponse.player), pkg)
        })
    }
}
