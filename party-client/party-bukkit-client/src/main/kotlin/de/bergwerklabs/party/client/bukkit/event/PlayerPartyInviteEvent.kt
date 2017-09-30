package de.bergwerklabs.party.client.bukkit.event

import de.bergwerklabs.party.api.Party
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 30.09.2017.
 * <p>
 * @author Yannic Rieger
 */
class PlayerPartyInviteEvent(party: Party, player: Player) : AbstractPartyEvent(party, player) {

}