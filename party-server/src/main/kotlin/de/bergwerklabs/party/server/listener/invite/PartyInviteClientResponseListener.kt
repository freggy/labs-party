package de.bergwerklabs.party.server.listener.invite

import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteResponsePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.pendingInvites

/**
 * Created by Yannic Rieger on 04.10.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyClientResponseListener : AtlantisPackageListener<PartyClientInviteResponsePackage>() {
    
    override fun onResponse(pkg: PartyClientInviteResponsePackage) {
        val party = pendingInvites[pkg.partyId]
        if (party != null) {
            if (party.any { inviteInfo -> inviteInfo.player == pkg.player }) {
                AtlantisPackageUtil.sendPackage()
            }
        }
        
    }
}