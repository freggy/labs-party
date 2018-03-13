package de.bergwerklabs.party.api.wrapper

import de.bergwerklabs.api.cache.pojo.PlayerNameToUuidMapping
import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPacket
import de.bergwerklabs.atlantis.api.party.packages.PartyDisbandPacket
import de.bergwerklabs.atlantis.api.party.packages.PartySavePacket
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteRequestPacket
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePacket
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdate
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePacket
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.common.invites
import de.bergwerklabs.party.api.common.wrapPartyInviteResponse
import de.bergwerklabs.party.api.packageService
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Created by Yannic Rieger on 07.09.2017.
 *
 * Wraps the AtlantisParty class.
 *
 * @param id          id of the party.
 * @param owner       owner of the party.
 * @param membersList members of the party
 *
 * @author Yannic Rieger
 */
internal class PartyWrapper(val id: UUID, var owner: UUID, private val membersList: MutableList<UUID>) : Party {
    
    private var disbanded: Boolean = false
    
    /**
     * @param party party object sent by Atlantis
     */
    constructor(party: AtlantisParty) : this(party.id, party.owner, party.members)
    
    /**
     * Gets the ID of this party.
     */
    override fun getPartyId(): UUID = id
    
    /**
     * Gets a [Set] of [UUID]s representing the members of this party.
     */
    override fun getMembers(): Set<UUID> = HashSet(membersList).toSet()
    
    /**
     * Gets whether or not the player is the owner of a party.
     */
    override fun isOwner(player: UUID): Boolean = this.owner == player
    
    /**
     * Gets whether or not the player is only a member of this party. The party owner also counts as a member.
     */
    override fun isMember(player: UUID): Boolean = this.membersList.contains(player)
    
    /**
     * Gets whether or not the party was is disbanded.
     */
    override fun isDisbanded(): Boolean = disbanded
    
    /**
     * Gets the owner of this party.
     */
    override fun getPartyOwner(): UUID = owner
    
    /**
     * Disbands the party.
     */
    override fun disband() {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        this.disbanded = true
        packageService.sendPackage(PartyDisbandPacket(this.toAtlantisParty()))
        packageService.sendPackage(PartyUpdatePacket(this.toAtlantisParty(), PlayerNameToUuidMapping("Owner", this.owner), PartyUpdate.DISBAND))
    }
    
    /**
     * Changes the party owner.
     *
     * @param newOwner [UUID] of the new owner.
     */
    override fun changeOwner(newOwner: UUID) {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        packageService.sendPackage(PartyChangeOwnerPacket(this.toAtlantisParty(), this.owner, newOwner))
        this.membersList.remove(this.owner)
        this.owner = newOwner
        this.membersList.add(newOwner)
        
    }
    
    /**
     * Invites a player to a party.
     *
     * @param player [UUID] of the player to invite.
     * @return       the [PartyInviteStatus]
     */
    override fun invite(player: PlayerNameToUuidMapping, sender: PlayerNameToUuidMapping, callback: Consumer<PartyInviteResponse>) {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        invites[sender.uuid] = callback
        packageService.sendPackage(PartyClientInviteRequestPacket(this.toAtlantisParty(), sender, player))
    }
    
    /**
     * Removes a member from the party.
     *
     * If member was the owner of the party, the owner will be changed by calling [PartyWrapper.changeOwner].
     *
     * @param member [UUID] of the member to remove from the party.
     */
    override fun removeMember(member: PlayerNameToUuidMapping, update: PartyUpdateAction) {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        
        if (!this.membersList.contains(member.uuid)) return
        
        if (member.uuid == this.owner) {
            this.membersList.remove(member.uuid)
            this.changeOwner(this.membersList[Random().nextInt(this.membersList.size)])
        }
        else {
            this.membersList.remove(member.uuid)
        }
        
        val status = when (update) {
            PartyUpdateAction.PLAYER_JOIN  -> PartyUpdate.PLAYER_JOIN
            PartyUpdateAction.PLAYER_LEAVE -> PartyUpdate.PLAYER_LEAVE
            PartyUpdateAction.PLAYER_KICK  -> PartyUpdate.PLAYER_KICK
        }
    
        packageService.sendPackage(PartyUpdatePacket(this.toAtlantisParty(), member, status))
    }
    
    /**
     * Saves the party.
     */
    override fun save() {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        packageService.sendPackage(PartySavePacket(this.toAtlantisParty()))
    }
    
    override fun toAtlantisParty() = AtlantisParty(this.owner, ArrayList(this.membersList), this.id)
}