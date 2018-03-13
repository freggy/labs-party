package de.bergwerklabs.party.api

import de.bergwerklabs.api.cache.pojo.PlayerNameToUuidMapping
import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.party.api.wrapper.PartyInviteResponse
import de.bergwerklabs.party.api.wrapper.PartyUpdateAction
import de.bergwerklabs.party.api.wrapper.PartyWrapper
import java.util.*
import java.util.function.Consumer

/**
 * Created by Yannic Rieger on 07.09.2017.
 *
 * Interface for Wrapper abstraction.
 *
 * @author Yannic Rieger
 */
interface Party {
    
    /**
     * Gets the ID of the party.
     */
    fun getPartyId(): UUID
    
    /**
     * Gets the owner of the party.
     */
    fun getPartyOwner(): UUID
    
    /**
     * Gets a [Set] of [UUID]s representing the members of this party.
     */
    fun getMembers(): Set<UUID>
    
    /**
     * Gets whether or not the player is only a member of this party.
     */
    fun isMember(player: UUID): Boolean
    
    /**
     * Gets whether or not the player is the owner of a party.
     */
    fun isOwner(player: UUID): Boolean
    
    /**
     * Gets whether or not the party was is disbanded.
     */
    fun isDisbanded(): Boolean
    
    /**
     * Disbands the party.
     */
    fun disband()
    
    /**
     * Changes the party owner.
     *
     * @param newOwner [UUID] of the new owner.
     */
    fun changeOwner(newOwner: UUID)
    
    /**
     * Removes a member from the party.
     *
     * If member was the owner of the party, the owner will be changed by calling [PartyWrapper.changeOwner].
     *
     * @param member [UUID] of the member to remove from the party.
     */
    fun removeMember(member: PlayerNameToUuidMapping, update: PartyUpdateAction)
    
    /**
     * Invites a player to a party.
     *
     * @param  player   [UUID] of the player
     * @param  sender
     * @param  callback callback to execute when the packet is received.
     */
    fun invite(player: PlayerNameToUuidMapping, sender: PlayerNameToUuidMapping, callback: Consumer<PartyInviteResponse>)
    
    /**
     * Saves the party.
     */
    fun save()
    
    /**
     * Creates an [AtlantisParty] object.
     */
    fun toAtlantisParty(): AtlantisParty
}