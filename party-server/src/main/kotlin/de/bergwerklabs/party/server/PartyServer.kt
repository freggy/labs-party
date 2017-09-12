package de.bergwerklabs.party.server

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.client.base.ConfiguredClientBootstrap

var server: PartyServer? = null

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
        }
    }
}