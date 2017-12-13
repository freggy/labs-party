package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

/**
 * Created by Yannic Rieger on 18.10.2017.
 *
 * @author Yannic Rieger
 */
class PartyWarpCommand : BungeeCommand {
    
    override fun getUsage() = "/party tp <spieler>"
    
    override fun getName() = "tp"
    
    override fun getDescription() = "Teleportiert zu einem anderen Party-Mitglied."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            val to = args!![0]
    
            if (args.isEmpty()) {
                partyBungeeClient!!.messenger.message("§cDu musst einen Namen angeben.", sender)
                return
            }
            
            partyBungeeClient!!.runAsync {
                PlayerResolver.getOnlinePlayerCacheEntry(to).ifPresent { entry ->
                    var execute = true
                    val nameOptional = PlayerResolver.resolveNameToUuid(to)
                    if (nameOptional.isPresent) {
                        val partyOptional = PartyApi.getParty(sender.uniqueId)
                        if (partyOptional.isPresent) {
                            val party = partyOptional.get()
                            val uuid = nameOptional.get()
                            if (party.isOwner(uuid) || party.isMember(uuid)) {
                                partyBungeeClient!!.messenger.message("§cDieser Spieler ist nicht in deiner Freundesliste.", sender)
                                execute = false
                            }
                        }
                    }
                    
                    if (execute) {
                        val info = entry.currentServer
                        if (entry != info) {
                            partyBungeeClient!!.proxy.getServerInfo("${info.containerId}_${info.service}")?.let {
                                sender.connect(it)
                            }
                        }
                    }
                }
            }
        }
    }
}