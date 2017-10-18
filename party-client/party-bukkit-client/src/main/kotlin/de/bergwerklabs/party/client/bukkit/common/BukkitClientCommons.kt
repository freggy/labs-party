package de.bergwerklabs.party.client.bukkit.common

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.client.bukkit.bukkitClient
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * Handles the response of a party invitation.
 *
 * @param response     response of the invitation.
 * @param inviteSender player who sent the party invitation.
 */
internal fun handlePartyInviteResponse(response: PartyInviteResponse, inviteSender: Player) {
    val messenger = bukkitClient!!.messenger
    val playerName = PlayerResolver.resolveUuidToName(response.playerUuid)
    
    // TODO: use rank color
    
    when (response.status) {
        PartyInviteStatus.ACCEPTED          -> {
            // TODO: display to all members
            messenger.message("§a✚§r $playerName §bist der Party beigetreten.", inviteSender)
        }
        PartyInviteStatus.DENIED            -> messenger.message("§c✖§r $playerName §bhat die Einaldung abgelehnt.", inviteSender)
        PartyInviteStatus.ALREADY_PARTIED   -> messenger.message("§c§a$playerName ist bereits in einer Party", inviteSender)
        else                                -> messenger.message("§dHoppla, da ist wohl ein Fehlerchen aufgetreten.", inviteSender)
    }
}

/**
 * Sends party invites to players.
 *
 * @param inviter      player who send the party invitation.
 * @param potentialIds array containing potential player names.
 * @param party        party to invite them to.
 */
internal fun sendPartyInvites(inviter: Player, potentialIds: Array<out String>?, party: Party) {
    potentialIds?.forEach { pId ->
        // If the invited player is on the same server as the party client we can use the Bukkit method to resolve the name to a UUID.
        // It returns null if the player is not on the server.
        val invited = Bukkit.getServer().getPlayer(pId)
        if (invited == null) {
            PlayerResolver.resolveNameToUuid(pId).ifPresent({ id ->
                party.invite(id, inviter.uniqueId, Consumer { response: PartyInviteResponse -> handlePartyInviteResponse(response, inviter) })
            })
        }
        else party.invite(invited.uniqueId, inviter.uniqueId, Consumer { response: PartyInviteResponse -> handlePartyInviteResponse(response, inviter) })
    }
}

