package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.atlantis.client.base.resolve.PlayerResolver
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.function.Consumer

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

            partyBungeeClient!!.messenger.message("§bDieses Feature wird zur Zeit überarbeitet.", sender)

            val to = args!![0]

            if (args.isEmpty()) {
                partyBungeeClient!!.messenger.message("§cDu musst einen Namen angeben.", sender)
                return
            }

            PlayerResolver.resolveNameToUuidAsync(to).thenApply { opt ->

                opt.ifPresent { mapping ->
                    PlayerResolver.getOnlinePlayerCacheEntry(mapping.uuid).thenAccept { optional ->
                        optional.ifPresent { entry ->

                            PartyApi.getParty(sender.uniqueId, Consumer { partyOptional ->
                                if (partyOptional.isPresent) {
                                    val party = partyOptional.get()
                                    val uuid = mapping.uuid
                                    if (party.isOwner(uuid) || party.isMember(uuid)) {
                                        partyBungeeClient!!.messenger.message(
                                                "§cDieser Spieler ist nicht in deiner Freundesliste.", sender
                                        )
                                    } else {
                                        partyBungeeClient!!.proxy.getServerInfo("${entry.server.id}_${entry.server.service}")?.let {
                                            sender.connect(it)
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}