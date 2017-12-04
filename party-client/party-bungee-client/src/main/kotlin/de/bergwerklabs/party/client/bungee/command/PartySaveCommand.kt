package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import net.md_5.bungee.api.CommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

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
    
    override fun execute(p0: CommandSender?, p1: Array<out String>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    
}