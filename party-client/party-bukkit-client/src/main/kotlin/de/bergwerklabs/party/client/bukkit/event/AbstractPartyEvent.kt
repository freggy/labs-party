package de.bergwerklabs.party.client.bukkit.event

import de.bergwerklabs.framework.commons.spigot.general.LabsEvent
import de.bergwerklabs.party.api.Party
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 30.09.2017.
 * <p>
 * @author Yannic Rieger
 */
open class AbstractPartyEvent(val party: Party, val player: Player) : LabsEvent()