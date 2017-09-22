package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.party.packages.invite.PartyPlayerInviteRequestPackage
import de.bergwerklabs.party.server.AtlantisPackageListener

/**
 * Created by Yannic Rieger on 21.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyPlayerInviteRequestListener : AtlantisPackageListener<PartyPlayerInviteRequestPackage>() {
    
    override fun onResponse(pkg: PartyPlayerInviteRequestPackage) {
    
    }
    
}