package de.bergwerklabs.party.api

import de.bergwerklabs.party.api.wrapper.PartyCreateStatus
import java.util.*

/**
 * Created by Yannic Rieger on 28.09.2017.
 * <p>
 *
 * @author Yannic Rieger
 */
data class PartyCreateResult(val party: Optional<Party>, val status: PartyCreateStatus)