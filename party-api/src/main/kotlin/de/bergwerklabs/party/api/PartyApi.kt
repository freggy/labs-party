package de.bergwerklabs.party.api

import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponseType
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoResponsePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.api.wrapper.PartyCreateStatus
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
            val response = this.sendInfoPacketAndGetResponse(player)
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
            val response = this.sendInfoPacketAndGetResponse(player)
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
            val response = this.sendInfoPacketAndGetResponse(player)
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
            val response = this.sendInfoPacketAndGetResponse(player)
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
        fun createParty(owner: UUID, members: List<UUID>): PartyCreateResult {
            val future = AtlantisPackageUtil.sendPackageWithFuture<PartyCreateResponsePackage>(PartyCreateRequestPackage(owner, members))
            
            val response = future.get()
            
            return when {
                response.type == PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_DEFAULT -> PartyCreateResult(Optional.empty(), PartyCreateStatus.DENY_TOO_MANY_MEMBERS_DEFAULT)
                response.type == PartyCreateResponseType.DENY_TOO_MANY_MEMBERS_PREMIUM -> PartyCreateResult(Optional.empty(), PartyCreateStatus.DENY_TOO_MANY_MEMBERS_PREMIUM)
                response.type == PartyCreateResponseType.SUCCESS                       -> PartyCreateResult(Optional.of(PartyWrapper(response.partyId, owner, members.toMutableList())), PartyCreateStatus.SUCCESS)
                else                                                                   -> PartyCreateResult(Optional.empty(), PartyCreateStatus.UNKNOWN_ERROR)
            }
        }
    
        /**
         * Sends a [PartyInfoRequestPackage] and waits until the packet is received.
         *
         * @param player player to get the info from.
         */
        private fun sendInfoPacketAndGetResponse(player: UUID): PartyInfoResponsePackage {
            val future = AtlantisPackageUtil.sendPackageWithFuture<PartyInfoResponsePackage>(PartyInfoRequestPackage(player))
            return future.get()
        }
    }
}