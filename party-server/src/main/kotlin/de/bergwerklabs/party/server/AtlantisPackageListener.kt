package de.bergwerklabs.party.server

import de.bergwerklabs.atlantis.api.packages.APackage

/**
 * Created by Yannic Rieger on 22.09.2017.
 * <p>
 * @author Yannic Rieger
 */
abstract class AtlantisPackageListener<T : APackage> {
    abstract fun onResponse(pkg: T)
}