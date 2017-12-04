package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyUpdateAction
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Kicks a player from the party.
 *
 * @author Yannic Rieger
 */
class PartyKickCommand : BungeeCommand {
    
    override fun getUsage() = "/party kick <spieler>"
    
    override fun getName() = "kick"
    
    override fun getDescription() = "Entfernt einen Spieler aus der Party."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
        
            if (args != null && args.isEmpty()) {
                partyBungeeClient!!.messenger.message("§cDu musst mindestens einen Spieler angeben.", sender)
                return
            }
        
            val optional = PartyApi.getParty(sender.uniqueId)
        
            if (optional.isPresent) {
                val party = optional.get()
                val toKick = PlayerResolver.resolveNameToUuid(args!![0]).get()
                party.removeMember(toKick, PartyUpdateAction.PLAYER_KICK)
            }
            else partyBungeeClient!!.messenger.message("§cDu bist in keiner Party.", sender)
        }
    }
}