package de.bergwerklabs.party.client.bukkit.event

import de.bergwerklabs.framework.commons.spigot.general.LabsEvent
import de.bergwerklabs.party.api.Party
import org.bukkit.entity.Player

// TODO: implement events.

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Base class for all party events.
 *
 * @author Yannic Rieger
 */
open class AbstractPartyEvent(val party: Party, val player: Player) : LabsEvent()