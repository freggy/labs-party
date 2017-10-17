package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bukkit.bukkitClient
import de.bergwerklabs.party.client.bukkit.common.sendPartyInvites
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 17.10.2017.
 *
 * Invites one or multiple players to a party.
 *
 * @author Yannic Rieger
 */
class PartyInviteCommand : ChildCommand {
    
    override fun getName() = "invite"
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val optional = PartyApi.getParty(sender.uniqueId)
            if (optional.isPresent) {
                val party = optional.get()
                sendPartyInvites(sender, args, party)
            }
            else bukkitClient!!.messenger.message("§cDu bist in keiner Party.", sender)
        }
        return true
    }
}