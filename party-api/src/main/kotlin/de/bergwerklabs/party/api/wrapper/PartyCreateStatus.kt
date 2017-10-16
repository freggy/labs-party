package de.bergwerklabs.party.api.wrapper

/**
 * Created by Yannic Rieger on 28.09.2017.
 *
 * Results of a party create request.
 *
 * @author Yannic Rieger
 */
enum class PartyCreateStatus {
    DENY_TOO_MANY_MEMBERS_PREMIUM,
    DENY_TOO_MANY_MEMBERS_DEFAULT,
    UNKNOWN_ERROR,
    ALREADY_PARTIED,
    SUCCESS
}