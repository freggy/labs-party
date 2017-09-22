package de.bergwerklabs.party.api

import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.api.wrapper.PartyWrapper
import java.util.*


internal val currentParties = hashMapOf<UUID, Party>()

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
         * @return whether or not the player is in a party.
         */
        @JvmStatic
        fun isPartied(player: UUID) = currentParties.values.any { party -> party.isOwner(player) || party.isMember(player) }
    
        /**
         * Determines whether or not the player is the owner of the [Party]
         *
         * @param player [UUID] of the potential owner.
         * @return whether or not the player is the party owner.
         */
        @JvmStatic
        fun isPartyOwner(player: UUID) = currentParties.values.any { party -> party.isOwner(player) }
    
        /**
         * Determines whether or not the player is a party member.
         *
         * @param player [UUID] of the player to check.
         * @return whether or not the player is only a member.
         */
        @JvmStatic
        fun isPartyMember(player: UUID) = currentParties.values.any { party -> party.isMember(player) }
    
    
        /**
         * Gets the [Optional] that contains the [Party] the player is currently a member of.
         *
         * @param player [UUID] of the player to check.
         * @return [Optional] that contains the party of the player if he is in one.
         */
        @JvmStatic
        fun getParty(player: UUID): Optional<Party> = Optional.ofNullable(currentParties.values.filter { party -> party.isMember(player) }.getOrNull(0))
        
    
        /**
         * Creates a new [Party].
         *
         * @param owner [UUID] of the owner of the party.
         * @param members [List] of members of the party.
         * @return a [Party]
         */
        @JvmStatic
        fun createParty(owner: UUID, members: List<UUID>): Party {
            val partyId = UUID.randomUUID()
            AtlantisPackageUtil.sendPackage(PartyCreatePackage(partyId, owner, members))
            return PartyWrapper(partyId, owner, members.toMutableList())
        }
    }
}