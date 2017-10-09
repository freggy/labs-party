package de.bergwerklabs.party.client.bukkit

import de.bergwerklabs.commons.spigot.chat.messenger.PluginMessenger
import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessages
import de.bergwerklabs.party.client.bukkit.command.PartyChatCommand
import de.bergwerklabs.party.client.bukkit.command.PartyCreateCommand
import de.bergwerklabs.party.client.bukkit.command.PartyParentCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener

var bukkitClient: BukkitPartyClient? = null

/**
 * Created by Yannic Rieger on 30.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class BukkitPartyClient : JavaPlugin(), PluginMessageListener {
    
    val messenger = PluginMessenger("Party")
    
    override fun onEnable() {
        bukkitClient = this
        this.server.messenger.registerOutgoingPluginChannel(this, PluginMessages.PLUGIN_CHANNEL)
        this.server.messenger.registerIncomingPluginChannel(this, PluginMessages.PLUGIN_CHANNEL, this)
        this.getCommand("party").executor = PartyParentCommand("party", PartyCreateCommand())
        this.getCommand("p").executor = PartyChatCommand()
    }
    
    override fun onDisable() {}
    
    override fun onPluginMessageReceived(p0: String?, p1: Player?, p2: ByteArray?) {}
}