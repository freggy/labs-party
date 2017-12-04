package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.api.PartyApi
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * Created by Yannic Rieger on 08.10.2017.
 *
 * Sends a text message to all party members.
 *
 * @author Yannic Rieger
 */
class PartyChatCommand : BungeeCommand {
    
    override fun getUsage() = "/party chat <message>"
    
    override fun getName() = "chat"
    
    override fun getDescription() = "Message to party memebers"
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            val optional = PartyApi.getParty(sender.uniqueId)
            if (optional.isPresent) {
                val party = optional.get()
                party.getMembers().forEach { uuid ->
                    val name = PlayerResolver.resolveUuidToName(uuid).get()
                    // TODO: use rank color
                    // TODO: send message
                }
            }
            else {
                /*
                bukkitClient!!.messenger.message("§cDu bist in keiner Party.", sender)
                sender.playSound(sender.eyeLocation, Sound.NOTE_BASS, 1F, 100F) */
            }
        }
    }
}