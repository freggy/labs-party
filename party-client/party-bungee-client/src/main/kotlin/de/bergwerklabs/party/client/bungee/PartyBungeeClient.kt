package de.bergwerklabs.party.client.bungee

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.PartySwitchServerPacket
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteRequestPacket
import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.framework.commons.bungee.chat.PluginMessenger
import de.bergwerklabs.framework.commons.bungee.chat.text.MessageUtil
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyUpdateAction
import de.bergwerklabs.party.client.bungee.command.*
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.event.ServerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.util.*

var partyBungeeClient: PartyBungeeClient? = null

/**
 * Created by Yannic Rieger on 17.10.2017.
 *
 * Main class for the Bungee client
 *
 * @author Yannic Rieger
 */
class PartyBungeeClient : Plugin(), Listener {
    
    val invitedFor = HashMap<UUID, PartyServerInviteRequestPacket>()
    
    val messenger = PluginMessenger("Party")
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    
    private val packageService = AtlantisPackageService(
            PartyServerInviteRequestPacket::class.java,
            PartySwitchServerPacket::class.java)
    
    override fun onEnable() {
        partyBungeeClient = this
        
        
        this.proxy.pluginManager.registerListener(this, this)
        this.proxy.pluginManager.registerCommand(this, PartyParentCommand(
                "",
                "",
                "",
                PartyKickCommand(),
                PartyInviteCommand(),
                PartyInviteAcceptCommand(),
                PartyInviteDenyCommand()
        ))
        
        packageService.addListener(PartySwitchServerPacket::class.java, { pkg ->
            PartyApi.getParty(pkg.partyId).ifPresent {
                it.getMembers().forEach { member ->
                    val player = this.proxy.getPlayer(member)
                    // only move players registered to this client
                    if (player != null) {
                        // TODO: connect
                    }
                }
            }
        })
    
        packageService.addListener(PartyServerInviteRequestPacket::class.java, { pkg ->
            this.proxy.getPlayer(pkg.responder).let {
                val initialSenderName = PlayerResolver.resolveUuidToName(pkg.initalSender).get()
            
                // nasty little workaround to get the fancy message centered as well.
                val spaces = MessageUtil.getSpacesToCenter("§a[ANNEHMEN]§6 | §c[ABLEHNEN]")
                val builder = StringBuilder()
                for (i in 0..spaces) builder.append(" ")
            
                val message = ComponentBuilder("$builder§a[ANNEHMEN]").color(ChatColor.GREEN)
                            .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept"))
                        .append(" ❘ ").color(ChatColor.GOLD)
                        .append("[ABLEHNEN]").color(ChatColor.RED)
                            .event(ClickEvent(ClickEvent.Action.RUN_COMMAND,"/party deny"))
                        .create()
            
                MessageUtil.sendCenteredMessage(it, "§6§m-------§b Party-Einladung §6§m-------")
                MessageUtil.sendCenteredMessage(it, " ")
                MessageUtil.sendCenteredMessage(it, "§7Du hast eine Einladung von §a$initialSenderName §7erhalten.")
                MessageUtil.sendCenteredMessage(it," ")
                it.sendMessage(ChatMessageType.CHAT, *message)
                MessageUtil.sendCenteredMessage(it," ")
                MessageUtil.sendCenteredMessage(it, "§6§m--------------")
                invitedFor[it.uniqueId] = pkg
            }
        })
    }
    
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
                    this.logger.info("Party member left the server, he will be removed from the party.")
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
                    val from = player.server.info.name
                    val to = event.target.name
                    
                    val lobbyToGameserver = from.contains("lobby") && !to.contains("lobby")
                    val gameserverToLobby = !from.contains("lobby") && to.contains("lobby")
                    
                    if (lobbyToGameserver || gameserverToLobby) {
                        this.logger.info("Party owner switched the server, party members will be moved as well.")
                        this.packageService.sendPackage(PartySwitchServerPacket(it.getPartyId(), event.target.name))
                    }
                }
            }
        }
    }
}