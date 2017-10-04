package de.bergwerklabs.party.server.listener.invite

import de.bergwerklabs.atlantis.api.party.packages.invite.InviteStatus
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.InviteInfo
import de.bergwerklabs.party.server.currentParties
import de.bergwerklabs.party.server.pendingInvites
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
                AtlantisPackageUtil.sendPackage(PartyServerInviteResponsePackage(pkg.partyId, InviteStatus.PARTY_FULL, pkg.playerUuid))
            }
        }
        else AtlantisPackageUtil.sendPackage(PartyServerInviteResponsePackage(pkg.partyId, InviteStatus.PARTY_NOT_PRESENT, pkg.playerUuid))
        
        pendingInvites.computeIfAbsent(pkg.partyId, { uuid -> CopyOnWriteArrayList() })
        pendingInvites[pkg.partyId]!!.add(InviteInfo(pkg.playerUuid, System.currentTimeMillis()))
    }
}
