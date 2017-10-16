package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Accepts a party invitation.
 *
 * @author Yannic Rieger
 */
class PartyInviteAcceptCommand : ChildCommand {
    
    override fun getName() = "accept"
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            PartyApi.respondToInvite(PartyInviteStatus.ACCEPTED, sender.uniqueId, bukkitClient!!.invitedFor[sender.uniqueId]!!)
        }
        return true
    }
}