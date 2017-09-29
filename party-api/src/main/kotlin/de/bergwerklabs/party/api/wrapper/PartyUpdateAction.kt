package de.bergwerklabs.party.api.wrapper

/**
 * Created by Yannic Rieger on 21.09.2017.
 *
 * Action that can be performed on a party object by a player.
 *
 * @author Yannic Rieger
 */
enum class PartyUpdateAction {
    PLAYER_LEAVE,
    PLAYER_JOIN,
    PLAYER_KICK
}