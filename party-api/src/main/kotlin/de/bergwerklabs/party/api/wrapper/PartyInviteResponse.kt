package de.bergwerklabs.party.api.wrapper

import java.util.*

/**
 * Created by Yannic Rieger on 14.10.2017.
 * <p>
 * @author Yannic Rieger
 */
data class PartyInviteResponse(val playerName: String, val playerUuid: UUID, val status: PartyInviteStatus, val partyId: UUID)