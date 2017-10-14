package de.bergwerklabs.party.server.listener.invite

import de.bergwerklabs.atlantis.api.party.packages.invite.InviteStatus
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.packageService
import de.bergwerklabs.party.server.pendingInvites

/**
 * Created by Yannic Rieger on 04.10.2017.
 *
 * Listens for the [PartyClientInviteResponsePackage]
 *
 * @author Yannic Rieger
 */
class PartyInviteClientResponseListener : AtlantisPackageListener<PartyClientInviteResponsePackage>() {
    
    override fun onResponse(pkg: PartyClientInviteResponsePackage) {
        val party = pendingInvites[pkg.partyId]
        if (party != null) {
            if (party.any { inviteInfo -> inviteInfo.player == pkg.player }) { // check if invite is not expired.
                packageService.sendPackage(PartyServerInviteResponsePackage(pkg.partyId, pkg.status, pkg.player))
            }
            else packageService.sendPackage(PartyServerInviteResponsePackage(pkg.partyId, InviteStatus.EXPIRED, pkg.player))
        }
        else packageService.sendPackage(PartyServerInviteResponsePackage(pkg.partyId, InviteStatus.PARTY_NOT_PRESENT, pkg.player))
    }
}