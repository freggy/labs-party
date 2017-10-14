package de.bergwerklabs.party.api.common

import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponseType
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.invite.InviteStatus
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.PartyCreateResult
import de.bergwerklabs.party.api.wrapper.PartyCreateStatus
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.api.wrapper.PartyWrapper
import java.util.*

/**
 * Sends a [PartyInfoRequestPackage] and waits until the packet is received.
 *
 * @param player player to get the info from.
 */
internal fun sendInfoPacketAndGetResponse(player: UUID): PartyInfoResponsePackage {
    val future = AtlantisPackageUtil.sendPackageWithFuture<PartyInfoResponsePackage>(PartyInfoRequestPackage(player))
    return future.get()
}

/**
 * Creates a new [Party].
 *
 * @param owner [UUID]   of the owner of the party.
 * @param members [List] of members of the party.
 * @return               a [PartyCreateResult]
 */
internal fun tryPartyCreation(owner: UUID, members: List<UUID>): PartyCreateResult {
    
    AtlantisPackageService().sendPackage((PartyCreateRequestPackage(owner, members)))
    
    //val future = AtlantisPackageUtil.sendPackageWithFuture<PartyCreateResponsePackage>(PartyCreateRequestPackage(owner, members))
    //val response = future.get()
    //return when {
        //response.type == PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_DEFAULT -> PartyCreateResult(Optional.empty(), PartyCreateStatus.DENY_TOO_MANY_MEMBERS_DEFAULT)
        //response.type == PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_PREMIUM -> PartyCreateResult(Optional.empty(), PartyCreateStatus.DENY_TOO_MANY_MEMBERS_PREMIUM)
        //response.type == PartyCreateResponseType.SUCCESS                       -> PartyCreateResult(Optional.of(PartyWrapper(response.partyId, owner, members.toMutableList())), PartyCreateStatus.SUCCESS)
        //else                                                                   -> PartyCreateResult(Optional.empty(), PartyCreateStatus.UNKNOWN_ERROR)
    //}
    return PartyCreateResult(Optional.empty(), PartyCreateStatus.DENY_TOO_MANY_MEMBERS_PREMIUM)
}

/**
 * Wraps a PartyServerInviteResponsePackage.
 *
 * @param response party invite response package
 */
internal fun wrapPartyInviteResponse(response: PartyServerInviteResponsePackage): PartyInviteResponse {
    val status = when (response.status) {
        InviteStatus.ACCEPTED          -> PartyInviteStatus.ACCEPTED
        InviteStatus.DENIED            -> PartyInviteStatus.DENIED
        InviteStatus.EXPIRED           -> PartyInviteStatus.EXPIRED
        InviteStatus.PARTY_FULL        -> PartyInviteStatus.PARTY_FULL
        InviteStatus.PARTY_NOT_PRESENT -> PartyInviteStatus.PARTY_NOT_PRESENT
    }
    return PartyInviteResponse(response.responder, status, response.partyId)
}