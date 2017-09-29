package de.bergwerklabs.party.server

import de.bergwerklabs.atlantis.api.packages.APackage

/**
 * Created by Yannic Rieger on 22.09.2017.
 * <p>
 * Base class for listeners
 *
 * @author Yannic Rieger
 */
abstract class AtlantisPackageListener<in T : APackage> {
    
    /**
     * Will be invoked when the packages has been received.
     *
     * @param pkg package that has been received.
     */
    abstract fun onResponse(pkg: T)
}