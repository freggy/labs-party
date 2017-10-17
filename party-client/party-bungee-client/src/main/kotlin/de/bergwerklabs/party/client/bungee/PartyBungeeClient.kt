package de.bergwerklabs.party.client.bungee

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.PartySwitchServerPackage
import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyUpdateAction
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.event.ServerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler

/**
 * Created by Yannic Rieger on 17.10.2017.
 *
 * Main class for the Bungee client
 *
 * @author Yannic Rieger
 */
class PartyBungeeClient : Plugin(), Listener {
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    private val packageService = AtlantisPackageService()
    
    @EventHandler
    fun onPlayerDisconnectServer(event: ServerDisconnectEvent) {
        val player = event.target
        PlayerResolver.resolveNameToUuid(player.name).ifPresent { uuid ->
            PartyApi.getParty(uuid).ifPresent {
                if (it.isOwner(uuid)) {
                    this.logger.info("Party owner left the server, disbanding party.")
                    it.disband()
                }
                else {
                    logger.info("Party member left the server, he will be removed from the party.")
                    it.removeMember(uuid, PartyUpdateAction.PLAYER_LEAVE)
                }
            }
        }
    }
    
    @EventHandler
    fun onPlayerConnectServer(event: ServerConnectEvent) {
        val player = event.player
        PlayerResolver.resolveNameToUuid(player.name).ifPresent { uuid ->
            PartyApi.getParty(uuid).ifPresent {
                if (it.isOwner(uuid)) {
                    this.logger.info("Party owner switched the server, party members will be moved as well.")
                    this.packageService.sendPackage(PartySwitchServerPackage(it.getPartyId(), event.target.name))
                }
            }
        }
    }
}