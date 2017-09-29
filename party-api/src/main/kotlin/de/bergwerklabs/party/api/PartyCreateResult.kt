package de.bergwerklabs.party.api

import de.bergwerklabs.party.api.wrapper.PartyCreateStatus
import java.util.*

/**
 * Created by Yannic Rieger on 28.09.2017.
 *
 * Will be returned when the a party create request has been sent.
 *
 * @param party  contains the party if the request was successful.
 * @param status status of the request.
 *
 * @author Yannic Rieger
 */
data class PartyCreateResult(val party: Optional<Party>, val status: PartyCreateStatus)