package de.bergwerklabs.party.api

import de.bergwerklabs.party.api.common.sendInfoPacketAndGetResponse
import de.bergwerklabs.party.api.common.tryPartyCreation
import de.bergwerklabs.party.api.wrapper.PartyWrapper
import java.util.*

/**
 * Created by Yannic Rieger on 06.09.2017.
 *
 * API providing useful methods for interacting with the party system.
 *
 * @author Yannic Rieger
 */
class PartyApi {
    
    companion object {
        
        /**
         * Determines whether or not a player is in a [Party].
         *
         * @param player [UUID] of the player to check.
         * @return              whether or not the player is in a party.
         */
        @JvmStatic
        fun isPartied(player: UUID): Boolean {
            val response = sendInfoPacketAndGetResponse(player)
            return response.party != null
        }
    
        /**
         * Determines whether or not the player is the owner of the [Party]
         *
         * @param player [UUID] of the potential owner.
         * @return       whether or not the player is the party owner.
         */
        @JvmStatic
        fun isPartyOwner(player: UUID): Boolean {
            val response = sendInfoPacketAndGetResponse(player)
            return response.party.owner == player
        }
    
        /**
         * Determines whether or not the player is a party member.
         *
         * @param player [UUID] of the player to check.
         * @return       whether or not the player is only a member.
         */
        @JvmStatic
        fun isPartyMember(player: UUID): Boolean {
            val response = sendInfoPacketAndGetResponse(player)
            return response.party.members.contains(player)
        }
    
    
        /**
         * Gets the [Optional] that contains the [Party] the player is currently a member of.
         *
         * @param player [UUID] of the player to check.
         * @return       [Optional] that contains the party of the player if he is in one.
         */
        @JvmStatic
        fun getParty(player: UUID): Optional<Party> {
            val response = sendInfoPacketAndGetResponse(player)
            return when {
                response.party == null -> Optional.empty()
                else                   -> Optional.of(PartyWrapper(response.party))
            }
        }
        
    
        /**
         * Creates a new [Party].
         *
         * @param owner [UUID]   of the owner of the party.
         * @param members [List] of members of the party.
         * @return               a [PartyCreateResult]
         */
        @JvmStatic
        fun createParty(owner: UUID, members: List<UUID>): PartyCreateResult = tryPartyCreation(owner, members)
    
        /**
         * Creates a new [Party].
         *
         * @param owner   [UUID] of the owner of the party.
         * @param members [UUID]s of members of the party.
         * @return        a [PartyCreateResult]
         */
        @JvmStatic
        fun createParty(owner: UUID, vararg members: UUID): PartyCreateResult = tryPartyCreation(owner, Arrays.asList(*members))
    }
}