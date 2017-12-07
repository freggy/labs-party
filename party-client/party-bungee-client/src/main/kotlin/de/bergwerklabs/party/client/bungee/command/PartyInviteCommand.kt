package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import de.bergwerklabs.party.client.bungee.sendPartyInvites
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * Created by Yannic Rieger on 17.10.2017.
 *
 * Invites one or multiple players to a party.
 *
 * @author Yannic Rieger
 */
class PartyInviteCommand : BungeeCommand {
    
    override fun getUsage() = "/party invite <spieler>"
    
    override fun getName() = "invite"
    
    override fun getDescription() = "Lädt einen Spieler zur Party ein."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            partyBungeeClient!!.runAsync {
                val optional = PartyApi.getParty(sender.uniqueId)
                if (optional.isPresent) {
                    val party = optional.get()
                    sendPartyInvites(sender, args, party)
                }
                else partyBungeeClient!!.messenger.message("§cDu bist in keiner Party.", sender)
            }
        }
    }
}