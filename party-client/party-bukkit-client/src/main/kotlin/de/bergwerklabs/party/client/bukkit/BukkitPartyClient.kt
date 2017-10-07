package de.bergwerklabs.party.client.bukkit

import de.bergwerklabs.commons.spigot.chat.messenger.PluginMessenger
import de.bergwerklabs.party.client.bukkit.command.PartyCreateCommand
import de.bergwerklabs.party.client.bukkit.command.PartyParentCommand
import org.bukkit.plugin.java.JavaPlugin

var bukkitClient: BukkitPartyClient? = null

/**
 * Created by Yannic Rieger on 30.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class BukkitPartyClient : JavaPlugin() {
    
    val messenger = PluginMessenger("Party")
    
    override fun onEnable() {
        bukkitClient = this
        this.getCommand("party").executor = PartyParentCommand("party", PartyCreateCommand())
    }
    
    override fun onDisable() {
    
    }
}