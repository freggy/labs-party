package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
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
    
    private val logger = AtlantisLogger.getLogger(PartyCreateRequestListener::class.java)
    
    override fun onResponse(pkg: PartyCreateRequestPackage) {
        if (pkg.members.size > 7) { // TODO: make configurable && maybe check if owner is premium
            logger.warn("Too much party members for party ${pkg.partyId}. Party member count: ${pkg.members.size}")
            AtlantisPackageUtil.sendPackage(PartyCreateResponsePackage(pkg.partyId, PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_DEFAULT), pkg.sentFrom)
            return
        }
        logger.info("Creating party ${pkg.partyId} with member count of ${pkg.members}")
        currentParties.put(pkg.partyId, AtlantisParty(pkg.owner, pkg.members))
        AtlantisPackageUtil.sendResponse(PartyCreateResponsePackage(pkg.partyId, PartyCreateResponseType.SUCCESS), pkg)
    }
}