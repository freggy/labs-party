package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.atlantis.client.base.resolve.PlayerResolver
import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.api.wrapper.PartyCreateStatus
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.client.bungee.canSendInvite
import de.bergwerklabs.party.client.bungee.handlePartyInviteResponse
import de.bergwerklabs.party.client.bungee.partyBungeeClient
import de.bergwerklabs.party.client.bungee.sendPartyInvites
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.function.Consumer

/**
 * Created by Yannic Rieger on 03.10.2017.
 *
 * Creates a party.
 *
 * Command usage: /party create {members}
 * Example: /party create Fraklez Bw2801 Herul freggyy
 *
 * @author Yannic Rieger
 */
class PartyCreateCommand : BungeeCommand {
    
    override fun getUsage() = "/party create <member|members>"
    
    override fun getName(): String = "create"
    
    override fun getDescription() = "Lade einen oder mehrere Spieler zu einer Party ein."
    
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender is ProxiedPlayer) {
            PartyApi.createParty(sender.uniqueId, Consumer { result ->
                when {
                    result.status == PartyCreateStatus.SUCCESS -> {
                        partyBungeeClient!!.messenger.message("§aParty wurde erfolgreich erstellt.", sender)
                        sendPartyInvites(sender, args, result.party.get())
                    }
                    result.status == PartyCreateStatus.DENY_TOO_MANY_MEMBERS_DEFAULT -> {
                        errorMessage(sender, 4) // TODO: insert fitting value
                    }
                    result.status == PartyCreateStatus.DENY_TOO_MANY_MEMBERS_PREMIUM -> {
                        errorMessage(sender, 4) // TODO: insert fitting value
                    }
                    result.status == PartyCreateStatus.UNKNOWN_ERROR -> {
                        partyBungeeClient!!.messenger.message("§4FEHLER", sender)
                    }
                    result.status == PartyCreateStatus.ALREADY_PARTIED -> {
                        partyBungeeClient!!.messenger.message("§cDu bist bereits in einer Party", sender)
                    }
                }
            })
        }
    }
    
    /**
     * Displays the error message and plays a sound.
     *
     * @param player player to play the sound to (Sound.NOTE_BASS, 1F, 100F).
     * @param count  maximum party player count.
     */
    private fun errorMessage(player: ProxiedPlayer, count: Int) {
        partyBungeeClient!!.messenger.message("§cDu kannst maximal §b$count Spieler einladen.", player)
    }
}