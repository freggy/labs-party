package de.bergwerklabs.party.client.bukkit.common

import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import org.bukkit.entity.Player

/**
 * Handles the response of a party invitation.
 *
 * @param response     response of the invitation.
 * @param inviteSender player who sent the party invitation.
 */
internal fun handlePartyInviteResponse(response: PartyInviteResponse, inviteSender: Player) {
    when (response.status) {
        PartyInviteStatus.ACCEPTED          -> {}
        PartyInviteStatus.DENIED            -> {}
        PartyInviteStatus.PARTY_FULL        -> TODO()
        PartyInviteStatus.PARTY_NOT_PRESENT -> TODO()
    }
}

