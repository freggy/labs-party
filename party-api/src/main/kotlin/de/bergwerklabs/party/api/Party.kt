package de.bergwerklabs.party.api

import java.util.*

/**
 * Created by Yannic Rieger on 07.09.2017.
 * <p>
 * @author Yannic Rieger
 */
interface Party {
    
    fun disband()
    
    fun changeOwner(newOwner: UUID)
    
    fun addMember(member: UUID)
    
    fun removeMember(member: UUID);
    
    fun save()
}