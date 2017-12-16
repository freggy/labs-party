package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * Created by Yannic Rieger on 30.09.2017.
 * 
 * Saves a party.
 *
 * @author Yannic Rieger
 */
class PartySaveCommand : BungeeCommand {
    
    override fun getUsage() = "/party save"
    
    override fun getName() = "save"
    
    override fun getDescription() = "Speichert die momentane Party ab."
    
    override fun execute(sender: CommandSender?, p1: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            partyBungeeClient!!.messenger.message("§7Dieses Feature ist im Moment noch nicht verfügbar.", sender)
        }
    }
    
}