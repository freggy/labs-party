package de.bergwerklabs.party.api.wrapper

/**
 * Created by Yannic Rieger on 28.09.2017.
 * <p>
 * @author Yannic Rieger
 */
enum class PartyCreateStatus {
    DENY_TOO_MANY_MEMBERS_PREMIUM,
    DENY_TOO_MANY_MEMBERS_DEFAULT,
    UNKNOWN_ERROR,
    SUCCESS
}