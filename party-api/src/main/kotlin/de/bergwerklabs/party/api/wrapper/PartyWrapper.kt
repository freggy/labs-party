package de.bergwerklabs.party.api.wrapper

import de.bergwerklabs.atlantis.party.packages.AtlantisParty
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
internal class PartyWrapper(private val atlantisParty: AtlantisParty) : Party {
    
    /**
     * Gets a [Set] of [UUID]s representing the members of this party.
     */
    val members: Set<UUID>
        get() = HashSet(this.atlantisParty.members)
    
    /**
     * Gets the [UUID] of owner of the party.
     */
    var owner: UUID = this.atlantisParty.owner
        get
        private set
    
    /**
     * Gets whether or not the party was is disbanded.
     */
    var isDisbanded: Boolean = false
        get
        private set
    
    /**
     * Disbands the party.
     */
    override fun disband() {
        if (this.isDisbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        this.isDisbanded = true
        // TODO: send disband package
    }
    
    /**
     * Changes the party owner.
     *
     * @param newOwner [UUID] of the new owner.
     */
    override fun changeOwner(newOwner: UUID) {
        if (this.isDisbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        this.owner = newOwner
        // TODO: send party owner changed package
    }
    
    /**
     * Adds a new member to the party.
     *
     * @param member [UUID] of the new party member.
     */
    override fun addMember(member: UUID) {
        if (this.isDisbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
    
        this.atlantisParty.members.add(member)
        // TODO: send atlantis party member add
    }
    
    /**
     * Removes a member from the party.
     *
     * If member was the owner of the party, the owner will be changed by calling [PartyWrapper.changeOwner].
     *
     * @param member [UUID] of the member to remove from the party.
     */
    override fun removeMember(member: UUID) {
        if (this.isDisbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        if (member == this.owner) {
            this.atlantisParty.members.remove(member)
            this.changeOwner(this.atlantisParty.members[Random().nextInt(this.members.size)])
        }
        else {
            this.atlantisParty.members.remove(member)
        }
    
        // TODO: send atlantisParty member remove
    }
    
    /**
     * Saves the party.
     */
    override fun save() {
        if (this.isDisbanded) throw IllegalStateException("Party is not available anymore, since it was disbanded")
        // TODO: send atlantis party save package
    }
}