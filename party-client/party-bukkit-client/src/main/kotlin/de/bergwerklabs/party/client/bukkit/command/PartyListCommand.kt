package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Lists the player currently in a party.
 *
 * @author Yannic Rieger
 */
class PartyListCommand : ChildCommand {
    
    override fun getName() = "list"
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        
        if (sender is Player) {
            
            val optional = PartyApi.getParty(sender.uniqueId)
            
            if (optional.isPresent) {
                val party = optional.get()
                bukkitClient!!.messenger.message("§bDeine Party: ", sender)
                // TODO: names are in rank color.
                sender.sendMessage("§6♕ ${PlayerResolver.resolveUuidToName(party.getPartyOwner()).get()}")
                
                party.getMembers().forEach { member ->
                    sender.sendMessage("§a■§r ${PlayerResolver.resolveUuidToName(party.getPartyOwner()).get()}")
                }
            }
            else bukkitClient!!.messenger.message("§cDu bist in keiner Party.", sender)
        }
        return true
    }
}