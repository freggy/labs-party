package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.client.bukkit.bukkitClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Denies a party invitation.
 *
 * @author Yannic Rieger
 */
class PartyInviteDenyCommand : BungeeCommand {
    
    override fun getUsage() = "/party deny"
    
    override fun getName() = "deny"
    
    override fun getDescription() = "Lehnt eine Party-Einladung ab."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            // TODO: display message
            PartyApi.respondToInvite(PartyInviteStatus.DENIED, sender.uniqueId, bukkitClient!!.invitedFor[sender.uniqueId]!!)
        }
    }
}