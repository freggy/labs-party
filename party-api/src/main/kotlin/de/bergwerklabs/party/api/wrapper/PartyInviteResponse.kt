package de.bergwerklabs.party.api.wrapper

import java.util.*

/**
 * Created by Yannic Rieger on 14.10.2017.
 *
 * Contains basic data about a invitation response.
 *
 * @author Yannic Rieger
 */
data class PartyInviteResponse(val playerUuid: UUID, val status: PartyInviteStatus, val partyId: UUID)