package de.bergwerklabs.party.server

import java.util.*

/**
 * Created by Yannic Rieger on 21.09.2017.
 * <p>
 * @author Yannic Rieger
 */
data class AtlantisParty(var owner: UUID, val members: MutableList<UUID>)