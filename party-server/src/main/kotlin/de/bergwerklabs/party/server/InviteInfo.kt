package de.bergwerklabs.party.server

import java.util.*

/**
 * Created by Yannic Rieger on 04.10.2017.
 *
 * Basic information of a invite.
 *
 * @param player  player that got invited.
 * @param invited time in milliseconds when the player got invited.
 *
 * @author Yannic Rieger
 */
data class InviteInfo(val player: UUID, val invited: Long)