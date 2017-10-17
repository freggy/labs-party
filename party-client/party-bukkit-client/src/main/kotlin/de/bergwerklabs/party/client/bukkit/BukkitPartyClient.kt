package de.bergwerklabs.party.client.bukkit

import de.bergwerklabs.atlantis.api.party.packages.PartySwitchServerPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePackage
import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.commons.spigot.chat.MessageUtil
import de.bergwerklabs.commons.spigot.chat.messenger.PluginMessenger
import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessageOption
import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessages
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bukkit.command.PartyChatCommand
import de.bergwerklabs.party.client.bukkit.command.PartyCreateCommand
import de.bergwerklabs.party.client.bukkit.command.PartyParentCommand
import mkremins.fanciful.FancyMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

internal var bukkitClient: BukkitPartyClient? = null
internal val packageService: AtlantisPackageService = AtlantisPackageService(PartyServerInviteRequestPackage::class.java,
                                                                             PartySwitchServerPackage::class.java)
/**
 * Created by Yannic Rieger on 30.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class BukkitPartyClient : JavaPlugin() {
    
    val messenger = PluginMessenger("Party")
    val invitedFor = HashMap<UUID, PartyServerInviteRequestPackage>()
    
    override fun onEnable() {
        bukkitClient = this
        this.getCommand("party").executor = PartyParentCommand("party", PartyCreateCommand())
        this.getCommand("p").executor = PartyChatCommand()
        
        packageService.addListener(PartySwitchServerPackage::class.java, { pkg ->
            PartyApi.getParty(pkg.partyId).ifPresent {
                it.getMembers().forEach { member ->
                    val player = Bukkit.getPlayer(member)
                    // only move players registered to this client
                    if (player != null) {
                        PluginMessages.sendPluginMessage(this, PluginMessageOption.MESSAGE, player.displayName, pkg.serverName)
                    }
                }
            }
        })
    
        packageService.addListener(PartyServerInviteRequestPackage::class.java, { pkg ->
            Bukkit.getPlayer(pkg.responder).let {
                val initialSenderName = PlayerResolver.resolveUuidToName(pkg.initalSender).get()
                
                // nasty little workaround to get the fancy message centered as well.
                val spaces = MessageUtil.getSpacesToCenter("§a[ANNEHMEN]§6 | §c[ABLEHNEN]")
                val builder = StringBuilder()
                for (i in 0..spaces) builder.append(" ")
    
                val message = FancyMessage("$builder§a[ANNEHMEN]").color(ChatColor.GREEN).command("/say ANNEHMEN")
                        .then(" ❘ ").color(ChatColor.GOLD)
                        .then("[ABLEHNEN]").color(ChatColor.RED).command("/say ABLEHNEN")
    
                MessageUtil.sendCenteredMessage(it, "§6§m-------§b Party-Einladung §6§m-------")
                MessageUtil.sendCenteredMessage(it, " ")
                MessageUtil.sendCenteredMessage(it, "§7Du hast eine Einladung von §a$initialSenderName §7erhalten.")
                MessageUtil.sendCenteredMessage(it," ")
                message.send(it)
                MessageUtil.sendCenteredMessage(it," ")
                MessageUtil.sendCenteredMessage(it, "§6§m--------------")
                invitedFor[it.uniqueId] = pkg
            }
        })
    }
    
    override fun onDisable() {}
}