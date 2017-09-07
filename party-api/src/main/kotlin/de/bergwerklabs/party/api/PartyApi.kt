package de.bergwerklabs.party.api

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
         */
        @JvmStatic
        fun isPartied(player: UUID) {
        
        }
    
        /**
         * Determines whether or not the player is the owner of the [Party]
         *
         * @param member [UUID] of the potential owner.
         */
        @JvmStatic
        fun isPartyOwner(member: UUID): Boolean {
        
        }
    
        /**
         * Determines whether or not the player is a party member.
         *
         * @param member [UUID] of the player to check.
         */
        @JvmStatic
        fun isPartyMember(member: UUID): Boolean {
        
        }
    
    
        /**
         * Gets the [Party] the player is currently a member of, null he is not a member of any party.
         *
         * @param player [UUID] of the player to check.
         */
        @JvmStatic
        fun getParty(player: UUID): Party? {

        }
    
        /**
         * Creates a new [Party].
         *
         * @param owner [UUID] of the owner of the party.
         * @param members [List] of members of the party.
         */
        @JvmStatic
        fun createParty(owner: UUID, members: List<UUID>): Party {
        
        }
    }
}