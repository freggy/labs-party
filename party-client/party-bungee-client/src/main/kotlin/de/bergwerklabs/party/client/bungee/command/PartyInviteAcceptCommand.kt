package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.function.Consumer

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Accepts a party invitation.
 *
 * @author Yannic Rieger
 */
class PartyInviteAcceptCommand : BungeeCommand {
    
    override fun getUsage() = "/party accept"
    
    override fun getName() = "accept"
    
    override fun getDescription() = "Akzeptiert eine Party-Einladung."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            
            if (partyBungeeClient!!.invitedFor[sender.uniqueId] == null) return
            
            PartyApi.isPartied(sender.uniqueId, Consumer {
                if (!it) {
                    PartyApi.respondToInvite(PartyInviteStatus.ACCEPTED, sender.uniqueId, partyBungeeClient!!.invitedFor[sender.uniqueId]!!)
                    // remove all entries because player is partied and should no longer respond to those invites
                    partyBungeeClient!!.invitedFor.remove(sender.uniqueId)
                }
                else partyBungeeClient!!.messenger.message("§cDu bist bereits in einer Party.", sender)
            })
        }
    }
}