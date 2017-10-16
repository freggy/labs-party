package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Command to disband a party.
 *
 * Command usage: /party disband
 *
 * @author Yannic Rieger
 */
class PartyDisbandCommand : ChildCommand {
    
    override fun getName() = "disband"
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val optional = PartyApi.getParty(sender.uniqueId)
            val uuid = sender.uniqueId
            
            if (optional.isPresent) {
                val party = optional.get()
                if (party.isOwner(uuid)) {
                    party.disband()
                }
                else bukkitClient!!.messenger.message("§cUm eine Party aufzul�sen, musst du Party-Leader sein.", sender)
            }
            else bukkitClient!!.messenger.message("§cDu befindest dich zur Zeit in keiner Party.", sender)
            
        }
        return true
    }
}