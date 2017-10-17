package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 17.10.2017.
 *
 * Promotes a party member to party owner.
 *
 * @author Yannic Rieger
 */
class PartyPromoteCommand : ChildCommand{
    
    override fun getName() = "promote"
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val optional = PartyApi.getParty(sender.uniqueId)
            if (optional.isPresent) {
                val party = optional.get()
                if (party.isOwner(sender.uniqueId)) {
                    PlayerResolver.resolveNameToUuid(args!![0]).ifPresent {
                        // TODO: display message
                        party.changeOwner(it)
                    }
                }
                else bukkitClient!!.messenger.message("§cDu bist nicht der Party-Owner", sender)
            }
            else bukkitClient!!.messenger.message("§cDu bist in keiner Party.", sender)
        }
        return true
    }
}