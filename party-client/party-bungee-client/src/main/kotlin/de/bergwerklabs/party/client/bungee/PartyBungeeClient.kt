package de.bergwerklabs.party.client.bungee

import de.bergwerklabs.atlantis.api.logging.AtlantisLogger
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPacket
import de.bergwerklabs.atlantis.api.party.packages.PartyChatPacket
import de.bergwerklabs.atlantis.api.party.packages.PartyDisbandPacket
import de.bergwerklabs.atlantis.api.party.packages.PartySwitchServerPacket
import de.bergwerklabs.atlantis.api.party.packages.invite.InviteStatus
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteRequestPacket
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePacket
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdate
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePacket
import de.bergwerklabs.atlantis.client.base.resolve.PlayerResolver
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.framework.commons.bungee.chat.PluginMessenger
import de.bergwerklabs.framework.commons.bungee.chat.text.MessageUtil
import de.bergwerklabs.framework.commons.bungee.command.help.CommandHelpDisplay
import de.bergwerklabs.framework.commons.bungee.permissions.ZBridge
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyUpdateAction
import de.bergwerklabs.party.client.bungee.command.*
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.util.*
import java.util.function.Consumer

var partyBungeeClient: PartyBungeeClient? = null

/**
 * Created by Yannic Rieger on 17.10.2017.
 *
 * Main class for the Bungee client
 *
 * @author Yannic Rieger
 */
class PartyBungeeClient : Plugin(), Listener {
    
    internal val invitedFor = HashMap<UUID, PartyServerInviteRequestPacket>()
    
    internal val messenger = PluginMessenger("Party")
    
    internal val zBridge = ZBridge("admin", "LphX3VULzQVgp2ry3f2ypkZKE5YeufMtaamfeNNNwZbLWyqm")
    
    lateinit var  helpDisplay: CommandHelpDisplay
    
    internal val packageService = AtlantisPackageService(
            PartyServerInviteRequestPacket::class.java,
            PartySwitchServerPacket::class.java,
            PartyUpdatePacket::class.java,
            PartyServerInviteResponsePacket::class.java,
            PartyDisbandPacket::class.java,
            PartyChatPacket::class.java,
            PartyChangeOwnerPacket::class.java
    )
    
    private val logger = AtlantisLogger.getLogger(this::class.java)
    
    override fun onEnable() {
        partyBungeeClient = this
        this.zBridge.init()
        
        this.proxy.pluginManager.registerListener(this, this)
        
        val partyChatCommand = PartyChatCommand()
        val partyHelpCommand = PartyHelpCommand()
        val parentCommand = PartyParentCommand(
                "party",
                "",
                "",
                partyHelpCommand,
                PartyKickCommand(),
                PartyDisbandCommand(),
                PartyInviteCommand(),
                PartyInviteAcceptCommand(),
                PartyInviteDenyCommand(),
                PartyCreateCommand(),
                PartyLeaveCommand(),
                PartyPromoteCommand(),
                partyHelpCommand,
                PartyListCommand()
        )
        
        this.helpDisplay = CommandHelpDisplay(parentCommand.subCommands.toSet())
        this.proxy.pluginManager.registerCommand(this, partyChatCommand)
        this.proxy.pluginManager.registerCommand(this, parentCommand)
    
        this.packageService.addListener(PartySwitchServerPacket::class.java, { pkg ->
            pkg.party.members.forEach { member ->
                val player = this.proxy.getPlayer(member)
                // only move players registered to this client
                if (player != null) {
                    val server = this.proxy.servers[pkg.serverName]
                    if (server != null) {
                        player.connect(server)
                    }
                    else player.sendMessage(ChatMessageType.CHAT, *TextComponent.fromLegacyText("§6>> §eColumbia §6❘ §cDieser Server ist nicht mehr online."))
                }
            }
        })
    
        this.packageService.addListener(PartyUpdatePacket::class.java, { pkg ->
            when (pkg.update) {
                PartyUpdate.PLAYER_JOIN  -> handlePartyUpdate(PartyApi.toParty(pkg.party), pkg.player, "§a✚ {c}{p} §7ist der Party §abeigetreten.", "§aDu bist der Party beigetreten.")
                PartyUpdate.PLAYER_LEAVE -> handlePartyUpdate(PartyApi.toParty(pkg.party), pkg.player, "§c✖ {c}{p} §7hat die Party §cverlassen.", "§cDu hast die Party verlassen.")
                PartyUpdate.PLAYER_KICK  -> handlePartyUpdate(PartyApi.toParty(pkg.party), pkg.player, "§c✖ {c}{p} §4wurde aus der Party entfernt", "§4Du wurdest aus der Party entfernt.")
                PartyUpdate.DISBAND      -> {
                    pkg.party.members.forEach { member ->
                        this.proxy.getPlayer(member)?.let {
                            this.messenger.message("§cDie Party wurde aufgelöst.", it)
                        }
                    }
                }
            }
        })
    
        this.packageService.addListener(PartyServerInviteResponsePacket::class.java, { pkg ->
            this.proxy.getPlayer(pkg.responder)?.let {
                when (pkg.status) {
                    InviteStatus.PARTY_NOT_PRESENT -> this.messenger.message("§cDie Party is nicht mehr verfügbar", it)
                    InviteStatus.PARTY_FULL        -> this.messenger.message("§cDie Party is voll.", it)
                    InviteStatus.EXPIRED           -> this.messenger.message("§cDeine Einladung ist abgelaufen.", it)
                }
            }
        })
        
        this.packageService.addListener(PartyChatPacket::class.java, { pkg ->
            pkg.recipients.forEach { recp ->
                this.proxy.getPlayer(recp)?.let {
                    val color = zBridge.getRankColor(pkg.sender.uuid).toString()
                    this.messenger.message("$color${pkg.sender.name} §8»§r ${pkg.message}", it)
                }
            }
        })
        
        this.packageService.addListener(PartyChangeOwnerPacket::class.java, { pkg ->
            this.proxy.getPlayer(pkg.newOwner)?.let {
                this.messenger.message("§bDu bist nun Party-Owner", it)
            }
        })
    
        this.packageService.addListener(PartyServerInviteRequestPacket::class.java, { pkg ->
                this.proxy.getPlayer(pkg.responder)?.let {
                    val initialSenderName = PlayerResolver.resolveUuidToName(pkg.initalSender).get()
                    val initalSenderColor = zBridge.getRankColor(pkg.initalSender).toString()
        
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
                    MessageUtil.sendCenteredMessage(it, "§7Du hast eine Einladung von $initalSenderColor$initialSenderName §7erhalten.")
                    MessageUtil.sendCenteredMessage(it," ")
                    it.sendMessage(ChatMessageType.CHAT, *message)
                    MessageUtil.sendCenteredMessage(it," ")
                    MessageUtil.sendCenteredMessage(it, "§6§m--------------")
                    invitedFor[it.uniqueId] = pkg
                }
        })
    }
    
    fun runAsync(method: (Unit) -> Unit) {
        this.proxy.scheduler.runAsync(this, { method.invoke(Unit) })
    }
    
    @EventHandler
    fun onPlayerDisconnectServer(event: PlayerDisconnectEvent) {
        val uuid = event.player.uniqueId
        PartyApi.getParty(uuid, Consumer {
            it.ifPresent {
                if (it.isOwner(uuid)) {
                    this.logger.info("Party owner left the server, disbanding party.")
                    it.disband()
                }
                else {
                    this.logger.info("Party member left the server, he will be removed from the party.")
                    it.removeMember(uuid, PartyUpdateAction.PLAYER_LEAVE)
                }
            }
        })
    }
    
    @EventHandler
    fun onPlayerConnectServer(event: ServerConnectEvent) {
        val player = event.player
        PartyApi.getParty(player.uniqueId, Consumer {
            it.ifPresent {
                if (it.isOwner(player.uniqueId)) {
                    val server = player.server ?: return@ifPresent
                    val from = server.info.name
                    val to = event.target.name
        
                    if (to == null || from == null) return@ifPresent
        
                    val lobbyToGameserver = from.contains("lobby") && !to.contains("lobby")
        
                    if (lobbyToGameserver) {
                        this.logger.info("Party owner switched the server, party members will be moved as well.")
                        this.packageService.sendPackage(PartySwitchServerPacket(it.toAtlantisParty(), event.target.name))
                    }
                }
            }
        })
    }
}