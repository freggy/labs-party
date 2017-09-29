package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPackage

import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponseType
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.currentParties
import java.util.*

/**
 * Created by Yannic Rieger on 21.09.2017.
 *
 * @author Yannic Rieger
 */
class PartyCreateRequestListener : AtlantisPackageListener<PartyCreateRequestPackage>() {
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    
    override fun onResponse(pkg: PartyCreateRequestPackage) {
        if (pkg.members.size > 7) { // TODO: make configurable && maybe check if owner is premium
            logger.warn("Too much party members for party ${pkg.partyId}. Party member count: ${pkg.members.size}")
            AtlantisPackageUtil.sendResponse(PartyCreateResponsePackage(pkg.partyId, PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_DEFAULT), pkg)
            return
        }
        
        logger.info("Creating party ${pkg.partyId} with member count of ${pkg.members.size}")
        val partyId = this.determineId()
        currentParties.put(partyId, AtlantisParty(pkg.owner, pkg.members, partyId))
        AtlantisPackageUtil.sendResponse(PartyCreateResponsePackage(partyId, PartyCreateResponseType.SUCCESS), pkg)
    }
    
    /**
     * Determines a [UUID] that isn't already taken.
     */
    private fun determineId(): UUID {
        var partyId: UUID
        do {
            partyId = UUID.randomUUID()
        } while (currentParties.containsKey(partyId))
        return partyId
    }
}
