package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessageOption
import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessages
import de.bergwerklabs.party.client.bukkit.bukkitClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 18.10.2017.
 *
 * @author Yannic Rieger
 */
class PartyWarpCommand : BungeeCommand {
    
    override fun getUsage() = "/party tp <spieler>"
    
    override fun getName() = "tp"
    
    override fun getDescription() = "Teleportiert zu einem anderen Party-Mitglied."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            val to = args!![0]
            PlayerResolver.getOnlinePlayerCacheEntry(to).ifPresent {
                // TODO: connect
            }
        }
    }
    
}