package de.bergwerklabs.party.api.common

import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPacket
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponsePacket
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponseType
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoRequestPacket
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoResponsePacket
import de.bergwerklabs.atlantis.api.party.packages.invite.InviteStatus
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePacket
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.PartyCreateResult
import de.bergwerklabs.party.api.packageService
import de.bergwerklabs.party.api.wrapper.PartyCreateStatus
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.api.wrapper.PartyWrapper
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.Consumer
import kotlin.collections.HashMap

internal val invites = HashMap<UUID, Consumer<PartyInviteResponse>>()

/**
 * Sends a [PartyInfoRequestPackage] and waits until the packet is received.
 *
 * @param player player to get the info from.
 */
internal fun sendInfoPacketAndGetResponse(player: UUID): PartyInfoResponsePacket {
    val future = packageService.sendRequestWithFuture(PartyInfoRequestPacket(player), PartyInfoResponsePacket::class.java)
    
    return try {
        future.get(4, TimeUnit.SECONDS)
    }
    catch (ex: Exception) {
        ex.printStackTrace()
        future.cancel(true)
        // return dummy object to prevent crashes.
        return PartyInfoResponsePacket(UUID.randomUUID(), AtlantisParty(UUID.randomUUID(), arrayListOf(UUID.randomUUID()), UUID.randomUUID()))
    }
}

/**
 * Creates a new [Party].
 *
 * @param owner [UUID]   of the owner of the party.
 * @param members [List] of members of the party.
 * @return               a [PartyCreateResult]
 */
internal fun tryPartyCreation(owner: UUID, members: List<UUID>): PartyCreateResult {
    val future = packageService.sendRequestWithFuture(PartyCreateRequestPacket(owner, members.toHashSet()), PartyCreateResponsePacket::class.java)
    val response: PartyCreateResponsePacket
    
    try {
        response = future.get(2, TimeUnit.SECONDS)
    }
    catch (ex: TimeoutException) {
        ex.printStackTrace()
        future.cancel(true)
        return PartyCreateResult(Optional.empty(), PartyCreateStatus.UNKNOWN_ERROR)
    }
    
    return when {
        response.type == PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_DEFAULT -> PartyCreateResult(Optional.empty(), PartyCreateStatus.DENY_TOO_MANY_MEMBERS_DEFAULT)
        response.type == PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_PREMIUM -> PartyCreateResult(Optional.empty(), PartyCreateStatus.DENY_TOO_MANY_MEMBERS_PREMIUM)
        response.type == PartyCreateResponseType.SUCCESS                       -> PartyCreateResult(Optional.of(PartyWrapper(response.party.id, owner, members.toMutableList())), PartyCreateStatus.SUCCESS)
        response.type == PartyCreateResponseType.ALREADY_PARTIED               -> PartyCreateResult(Optional.empty(), PartyCreateStatus.ALREADY_PARTIED)
        else                                                                   -> PartyCreateResult(Optional.empty(), PartyCreateStatus.UNKNOWN_ERROR)
    }
}

/**
 * Wraps a PartyServerInviteResponsePackage.
 *
 * @param response party invite response package
 */
internal fun wrapPartyInviteResponse(response: PartyServerInviteResponsePacket): PartyInviteResponse {
    val status = when (response.status) {
        InviteStatus.ACCEPTED          -> PartyInviteStatus.ACCEPTED
        InviteStatus.DENIED            -> PartyInviteStatus.DENIED
        InviteStatus.EXPIRED           -> PartyInviteStatus.EXPIRED
        InviteStatus.PARTY_FULL        -> PartyInviteStatus.PARTY_FULL
        InviteStatus.PARTY_NOT_PRESENT -> PartyInviteStatus.PARTY_NOT_PRESENT
        InviteStatus.ALREADY_PARTIED   -> PartyInviteStatus.ALREADY_PARTIED
    }
    return PartyInviteResponse(response.responder, status, response.party.id)
}