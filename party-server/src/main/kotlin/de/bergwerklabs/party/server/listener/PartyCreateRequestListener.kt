package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPackage

import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponseType
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.AtlantisParty
import de.bergwerklabs.party.server.currentParties
/**
 * Created by Yannic Rieger on 21.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyCreateRequestListener : AtlantisPackageListener<PartyCreateRequestPackage>() {
    
    override fun onResponse(pkg: PartyCreateRequestPackage) {
        if (pkg.members.size > 7) { // TODO: make configurable && maybe check if owner is premium
            AtlantisPackageUtil.sendPackage(PartyCreateResponsePackage(pkg.partyId, PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_DEFAULT), pkg.sentFrom)
            return
        }
        currentParties.put(pkg.partyId, AtlantisParty(pkg.owner, pkg.members))
        AtlantisPackageUtil.sendResponse(PartyCreateResponsePackage(pkg.partyId, PartyCreateResponseType.SUCCESS), pkg)
    }
}
