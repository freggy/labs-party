package de.bergwerklabs.party.api.wrapper

import de.bergwerklabs.atlantis.api.packages.APackage
import de.bergwerklabs.atlantis.api.party.packages.PartyChangeOwnerPackage
import de.bergwerklabs.atlantis.api.party.packages.PartyDisbandPackage
import de.bergwerklabs.atlantis.api.party.packages.PartyPlayerLeavePackage
import de.bergwerklabs.atlantis.api.party.packages.PartySavePackage
import de.bergwerklabs.atlantis.api.party.packages.invite.InviteStatus
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyPlayerInvitePackage
import de.bergwerklabs.atlantis.api.party.packages.invite.PartyPlayerInviteResponsePackage
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageCallback
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageUtil
import de.bergwerklabs.party.api.Party
import java.util.*
import kotlin.collections.HashSet

/**
 * Created by Yannic Rieger on 07.09.2017.
 *
 * Wraps the AtlantisParty class.
 *
 * @author Yannic Rieger
 */
internal class PartyWrapper(val id: UUID, var owner: UUID, val membersList: MutableList<UUID>) : Party {
    
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
     * Gets whether or not the player is only a member of this party.
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
        this.owner = newOwner
    }
    
    /**
     * Invites a player to a party.
     *
     * @return the [PartyInviteStatus]
     */
    override fun invite(player: UUID): PartyInviteStatus {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        var status = PartyInviteStatus.ACCEPTED
        AtlantisPackageUtil.sendPackage(PartyPlayerInvitePackage(this.id, player), AtlantisPackageCallback { pkg: APackage ->
            if (pkg is PartyPlayerInviteResponsePackage) {
                status = when (pkg.status!!) {
                    InviteStatus.ACCEPTED -> PartyInviteStatus.ACCEPTED
                    InviteStatus.DENIED   -> PartyInviteStatus.DENIED
                    InviteStatus.EXPIRED  -> PartyInviteStatus.EXPIRED
                }
            }
        })
        return status
    }
    
    /**
     * Removes a member from the party.
     *
     * If member was the owner of the party, the owner will be changed by calling [PartyWrapper.changeOwner].
     *
     * @param member [UUID] of the member to remove from the party.
     */
    override fun removeMember(member: UUID) {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        if (member == this.owner) {
            this.membersList.remove(member)
            this.changeOwner(this.membersList[Random().nextInt(this.membersList.size)])
        }
        else {
            this.membersList.remove(member)
        }
        AtlantisPackageUtil.sendPackage(PartyPlayerLeavePackage(this.id, member))
    }
    
    /**
     * Saves the party.
     */
    override fun save() {
        if (this.disbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        AtlantisPackageUtil.sendPackage(PartySavePackage(this.id))
    }
}