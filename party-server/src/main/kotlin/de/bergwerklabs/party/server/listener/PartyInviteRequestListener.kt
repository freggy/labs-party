package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.party.packages.invite.PartyPlayerInviteRequestPackage
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.InviteInfo
import de.bergwerklabs.party.server.pendingInvites

/**
 * Created by Yannic Rieger on 04.10.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyInviteRequestListener : AtlantisPackageListener<PartyPlayerInviteRequestPackage>() {
    
    override fun onResponse(pkg: PartyPlayerInviteRequestPackage) {
        pendingInvites[pkg.partyId] = InviteInfo(pkg.playerUuid, System.currentTimeMillis())
    }
}
