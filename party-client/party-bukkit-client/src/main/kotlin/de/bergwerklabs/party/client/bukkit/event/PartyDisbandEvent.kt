package de.bergwerklabs.party.client.bukkit.event

import de.bergwerklabs.party.api.Party
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Gets fired when a party disbands.
 *
 * @author Yannic Rieger
 */
class PartyDisbandEvent(party: Party, player: Player) : AbstractPartyEvent(party, player)