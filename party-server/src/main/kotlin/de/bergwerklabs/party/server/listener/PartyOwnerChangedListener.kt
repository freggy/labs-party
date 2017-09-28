package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPackage
import de.bergwerklabs.party.server.AtlantisPackageListener
import de.bergwerklabs.party.server.currentParties

/**
 * Created by Yannic Rieger on 21.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyOwnerChangedListener : AtlantisPackageListener<PartyChangeOwnerPackage>() {
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    
    override fun onResponse(pkg: PartyChangeOwnerPackage) {
        logger.info("Changed owner of party ${pkg.partyId} from ${pkg.oldOwner} to ${pkg.newOwner}")
        currentParties[pkg.partyId]?.owner = pkg.newOwner
    }
}