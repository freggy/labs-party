package de.bergwerklabs.party.client.bukkit

import de.bergwerklabs.commons.spigot.chat.messenger.PluginMessenger
import org.bukkit.plugin.java.JavaPlugin

var bukkitClient: BukkitPartyClient = BukkitPartyClient()

/**
 * Created by Yannic Rieger on 30.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class BukkitPartyClient : JavaPlugin() {
    
    val messenger = PluginMessenger("Party")
    
    override fun onEnable() {
        bukkitClient = this
    }
    
    override fun onDisable() {
    
    }
}