package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyUpdateAction
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Kicks a player from the party.
 *
 * @author Yannic Rieger
 */
class PartyKickCommand : ChildCommand {
    
    override fun getName() = "kick"
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
    
            if (args != null && args.isEmpty()) {
                bukkitClient!!.messenger.message("§cDu musst mindestens einen Spieler angeben.", sender)
                return true
            }
            
            val optional = PartyApi.getParty(sender.uniqueId)
            
            if (optional.isPresent) {
                val party = optional.get()
                val toKick = PlayerResolver.resolveNameToUuid(args!![0]).get()
                party.removeMember(toKick, PartyUpdateAction.PLAYER_KICK)
            }
            else bukkitClient!!.messenger.message("§cDu bist in keiner Party.", sender)
        }
        return true
    }
}