package de.bergwerklabs.party.api

import de.bergwerklabs.atlantis.api.party.packages.AbstractPartyPackage
import de.bergwerklabs.atlantis.intern.networkComponent.shared.ConsumerPipeline
import de.bergwerklabs.atlantis.intern.networkComponent.shared.PackageReceivedListener

/**
 * Created by Yannic Rieger on 11.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyPackageListener : PackageReceivedListener<AbstractPartyPackage> {
    
    constructor() : super(AbstractPartyPackage::class.java)
    
    constructor(acceptedClass: Class<AbstractPartyPackage>) : super(acceptedClass)
    
    override fun onPackageReceived(pkg: AbstractPartyPackage?, pipeline: ConsumerPipeline?) {}
    
}