package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.api.cache.pojo.PlayerNameToUuidMapping
import de.bergwerklabs.atlantis.api.party.packages.PartyChatPacket
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import kotlin.collections.HashSet

/**
 * Created by Yannic Rieger on 08.10.2017.
 *
 * Sends a text message to all party members.
 *
 * @author Yannic Rieger
 */
class PartyChatCommand : Command("p"), BungeeCommand {
    
    override fun getUsage() = "/party chat <message>"
    
    override fun getName() = "chat"
    
    override fun getDescription() = "Sendet eine Nachricht an alle Mitglieder"
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            val optional = PartyApi.getParty(sender.uniqueId)
            if (optional.isPresent) {
                val party = optional.get()
                
                val toSend = HashSet(party.getMembers().toHashSet())
                toSend.add(party.getPartyOwner())
                
                partyBungeeClient!!.packageService.sendPackage(PartyChatPacket(
                        party.toAtlantisParty(),
                        toSend,
                        PlayerNameToUuidMapping(sender.name, sender.uniqueId),
                        args!!.copyOfRange(0, args.size).joinToString(" ")
                ))
            }
            else partyBungeeClient!!.messenger.message("§cDu bist in keiner Party.", sender)
        }
    }
}