package de.bergwerklabs.party.api.wrapper

import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPackage
import de.bergwerklabs.atlantis.api.party.packages.PartyDisbandPackage
import de.bergwerklabs.atlantis.api.party.packages.PartySavePackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyClientInviteRequestPackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyServerInviteResponsePackage
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdate
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageCallback
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.common.wrapPartyInviteResponse
import remote.base.RemoteTaskSeedData
import java.util.*
import java.util.function.Consumer
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
    
    /**
     * @param party party object sent by Atlantis
     */
    constructor(party: AtlantisParty) : this(party.id, party.owner, party.members)
    
    private var disbanded: Boolean = false
    
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
     * Disbands the party.
     */
    override fun disband() {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        this.disbanded = true
        AtlantisPackageUtil.sendPackage(PartyDisbandPackage(this.id))
    }
    
    /**
     * Changes the party owner.
     *
     * @param newOwner [UUID] of the new owner.
     */
    override fun changeOwner(newOwner: UUID) {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        AtlantisPackageUtil.sendPackage(PartyChangeOwnerPackage(id, this.owner, newOwner))
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
    override fun invite(player: UUID, sender: UUID, callback: Consumer<PartyInviteResponse>) {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        
        AtlantisPackageUtil.sendPackage(PartyClientInviteRequestPackage(this.id, player, sender), AtlantisPackageCallback { pkg ->
            callback.accept(wrapPartyInviteResponse(pkg as PartyServerInviteResponsePackage))
        })
    }
    
    /**
     * Removes a member from the party.
     *
     * If member was the owner of the party, the owner will be changed by calling [PartyWrapper.changeOwner].
     *
     * @param member [UUID] of the member to remove from the party.
     */
    override fun removeMember(member: UUID, update: PartyUpdateAction) {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        if (member == this.owner) {
            this.membersList.remove(member)
            this.changeOwner(this.membersList[Random().nextInt(this.membersList.size)])
        }
        else {
            this.membersList.remove(member)
        }
        
        val status = when (update) {
            PartyUpdateAction.PLAYER_JOIN  -> PartyUpdate.PLAYER_JOIN
            PartyUpdateAction.PLAYER_LEAVE -> PartyUpdate.PLAYER_LEAVE
            PartyUpdateAction.PLAYER_KICK  -> PartyUpdate.PLAYER_KICK
        }
        
        AtlantisPackageUtil.sendPackage(PartyUpdatePackage(this.id, member, status))
    }
    
    /**
     * Saves the party.
     */
    override fun save() {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        AtlantisPackageUtil.sendPackage(PartySavePackage(this.id))
    }
}