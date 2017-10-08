package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessageOption
import de.bergwerklabs.framework.commons.spigot.pluginmessage.PluginMessages
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.apache.commons.lang.ArrayUtils
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 08.10.2017.
 *
 * @author Yannic Rieger
 */
class PartyChatCommand : CommandExecutor {
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (label!!.equals("p", true)) {
            if (sender is Player) {
                val optional = PartyApi.getParty(sender.uniqueId)
                if (optional.isPresent) {
                    val party = optional.get()
                    party.getMembers().forEach { uuid ->
                        val name = "" // TODO: resolve uuid
                        PluginMessages.sendPluginMessage(sender, bukkitClient, PluginMessageOption.MESSAGE, name, "${bukkitClient!!.messenger.prefix}§a${sender.displayName}§f: ${ArrayUtils.toString(args)}")
                    }
                }
                else {
                    bukkitClient!!.messenger.message("§cDu bist in keiner Party.", sender)
                    sender.playSound(sender.eyeLocation, Sound.NOTE_BASS, 1F, 100F)
                }
            }
        }
        return true
    }
}