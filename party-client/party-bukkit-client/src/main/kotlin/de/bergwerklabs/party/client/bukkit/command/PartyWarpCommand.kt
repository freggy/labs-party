package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessageOption
import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessages
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 18.10.2017.
 *
 * @author Yannic Rieger
 */
class PartyWarpCommand : ChildCommand {
    
    override fun getName() = "tp"
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val to = args!![0]
            PlayerResolver.getOnlinePlayerCacheEntry(to).ifPresent {
                PluginMessages.sendPluginMessage(bukkitClient, PluginMessageOption.CONNECT_OTHER, sender.displayName, it.currentServer.containerId)
            }
        }
        return true
    }
}