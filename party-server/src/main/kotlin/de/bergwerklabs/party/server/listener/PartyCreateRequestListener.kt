package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponseType
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.atlantis.intern.networkComponent.shared.ConsumerPipeline
import de.bergwerklabs.atlantis.intern.networkComponent.shared.PackageReceivedListener
import de.bergwerklabs.party.server.AtlantisParty
import de.bergwerklabs.party.server.currentParties

/**
 * Created by Yannic Rieger on 21.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyCreateRequestListener(acceptedClass: Class<PartyCreateRequestPackage>?) : PackageReceivedListener<PartyCreateRequestPackage>(acceptedClass) {
    
    override fun onPackageReceived(pkg: PartyCreateRequestPackage?, p1: ConsumerPipeline?) {
        if (pkg != null) {
            if (pkg.members.size > 7) { // TODO: make configurable
                AtlantisPackageUtil.sendPackage(PartyCreateResponsePackage(pkg.partyId, PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_DEFAULT), pkg.sentFrom)
                return
            }
            currentParties.put(pkg.partyId, AtlantisParty(pkg.owner, pkg.members))
            AtlantisPackageUtil.sendPackage(PartyCreateResponsePackage(pkg.partyId, PartyCreateResponseType.SUCCESS), pkg.sentFrom)
        }
    }
}