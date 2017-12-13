package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * Created by Yannic Rieger on 13.12.2017.
 * <p>
 * @author Yannic Rieger
 */
class PartyHelpCommand : BungeeCommand {
    override fun getUsage() = "/party help"
    
    override fun getName() = "help"
    
    override fun getDescription() = "Zeigt die Hilfe an."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        partyBungeeClient!!.helpDisplay.display(sender as? ProxiedPlayer ?: return, partyBungeeClient!!.messenger)
    }
}