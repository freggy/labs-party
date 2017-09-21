package de.bergwerklabs.party.server.listener

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPackage
import de.bergwerklabs.atlantis.intern.networkComponent.shared.ConsumerPipeline
import de.bergwerklabs.atlantis.intern.networkComponent.shared.PackageReceivedListener
import de.bergwerklabs.party.server.currentParties

/**
 * Created by Yannic Rieger on 21.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyOwnerChangedListener(acceptedClass: Class<PartyChangeOwnerPackage>?) : PackageReceivedListener<PartyChangeOwnerPackage>(acceptedClass) {
    
    private val logger = AtlantisLogger.getLogger(PartyOwnerChangedListener::class.java)
    
    override fun onPackageReceived(pkg: PartyChangeOwnerPackage?, p1: ConsumerPipeline?) {
        if (pkg != null) {
            logger.info("Changed owner of party ${pkg.partyId} from ${pkg.oldOwner} to ${pkg.newOwner}")
            currentParties[pkg.partyId]?.owner = pkg.newOwner
        }
    }
}