package de.bergwerklabs.party.server

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPackage
import de.bergwerklabs.atlantis.api.party.packages.PartyDisbandPackage
import de.bergwerklabs.atlantis.api.party.packages.PartySavePackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyPlayerInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePackage
import de.bergwerklabs.atlantis.client.base.ConfiguredClientBootstrap
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.server.listener.*
import java.util.*

var server: PartyServer? = null

val currentParties = HashMap<UUID, AtlantisParty>()


/**
 * Created by Yannic Rieger on 06.09.2017.
 * <p>
 *
 * @author Yannic Rieger
 */
class PartyServer {
    
    init {
        server = this
    }
    
    companion object {
        val logger = AtlantisLogger.getLogger(PartyServer::class.java)
        
        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Starting party server instance...")
    
            logger.info("Loading configured producers.")
            ConfiguredClientBootstrap.startConfiguredClients(PartyServer::class.java.getResource("/atlantisConfig.xml"))
            
            AtlantisPackageUtil.addListener(PartyUpdatePackage::class.java, { pkg -> PartyUpdatePackageListener().onResponse(pkg) })
            AtlantisPackageUtil.addListener(PartyChangeOwnerPackage::class.java, { pkg -> PartyOwnerChangedListener().onResponse(pkg) })
            AtlantisPackageUtil.addListener(PartyCreateRequestPackage::class.java, { pkg -> PartyCreateRequestListener().onResponse(pkg) })
            
            AtlantisPackageUtil.addListener(PartyPlayerInviteRequestPackage::class.java, { pkg -> PartyPlayerInviteRequestListener().onResponse(pkg) })
            AtlantisPackageUtil.addListener(PartySavePackage::class.java, { pkg -> PartySavePackageListener().onResponse(pkg) })
            AtlantisPackageUtil.addListener(PartyDisbandPackage::class.java, { pkg -> PartyDisbandListener().onResponse(pkg) })
            
        }
    }
}