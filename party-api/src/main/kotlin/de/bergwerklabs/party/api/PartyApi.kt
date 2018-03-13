package de.bergwerklabs.party.api

import de.bergwerklabs.api.cache.pojo.PlayerNameToUuidMapping
import de.bergwerklabs.atlantis.api.party.AtlantisParty
import de.bergwerklabs.atlantis.api.party.packages.createparty.PartyCreateResponsePacket
import de.bergwerklabs.atlantis.api.party.packages.info.PartyInfoResponsePacket
import de.bergwerklabs.atlantis.api.party.packages.invite.*
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdate
import de.bergwerklabs.atlantis.api.party.packages.update.PartyUpdatePacket
import de.bergwerklabs.atlantis.client.base.util.AtlantisPackageService
import de.bergwerklabs.party.api.common.*
import de.bergwerklabs.party.api.wrapper.PartyInviteStatus
import de.bergwerklabs.party.api.wrapper.PartyWrapper
import java.util.*
import java.util.function.Consumer


internal val packageService = AtlantisPackageService(PartyInfoResponsePacket::class.java,
                                                     PartyCreateResponsePacket::class.java,
                                                     PartyClientInviteResponsePacket::class.java,
                                                     PartyServerInviteResponsePacket::class.java)

/**
 * Created by Yannic Rieger on 06.09.2017.
 *
 * API providing useful methods for interacting with the party system.
 *
 * @author Yannic Rieger
 */
class PartyApi {
    
    
    init {
        packageService.addListener(PartyServerInviteResponsePacket::class.java, { pkg ->
            invites[pkg.initalSender.uuid]?.accept(wrapPartyInviteResponse(pkg as PartyServerInviteResponsePacket))
        })
    }
    
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
    
        @JvmStatic
        fun isPartied(player: UUID, callback: Consumer<Boolean>) {
            sendInfoPacketAndGetResponse(player, Consumer {
                callback.accept(it.party != null)
            })
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
        
        @JvmStatic
        fun isPartyOwner(player: UUID, callback: Consumer<Boolean>) {
            sendInfoPacketAndGetResponse(player, Consumer {
                callback.accept(it.party.owner == player)
            })
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
    
        @JvmStatic
        fun isPartyMember(player: UUID, callback: Consumer<Boolean>) {
            sendInfoPacketAndGetResponse(player, Consumer {
                callback.accept(it.party.members.contains(player))
            })
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
    
        @JvmStatic
        fun getParty(player: UUID, callback: Consumer<Optional<Party>>) {
            sendInfoPacketAndGetResponse(player, Consumer { pkg ->
                val resp: Optional<Party> = when {
                    pkg.party == null -> Optional.empty()
                    else              -> Optional.of(PartyWrapper(pkg.party))
                }
                callback.accept(resp)
            })
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
    
    
        /**
         * Creates a new [Party].
         *
         * @param owner   [UUID] of the owner of the party.
         * @param callback gets invoked when the [PartyCreateResult] is received.
         * @param members [UUID]s of members of the party.
         */
        fun createParty(owner: UUID, callback: Consumer<PartyCreateResult>, vararg members: UUID) {
            return tryPartyCreationWithCallback(owner, members.toList(), callback)
        }
    
        /**
         * Checks if a player is partied with another player.
         *
         * @param player1 partied player
         * @param player2 partied player
         * @return        if a player is partied with another player.
         */
        @JvmStatic
        fun isPartiedWith(player1: UUID, player2: UUID, callback: Consumer<Boolean>) {
            getParty(player1, Consumer { optional ->
                if (optional.isPresent) {
                    val party = optional.get()
                    callback.accept(party.isOwner(player2) || party.isMember(player2))
                }
                else callback.accept(false)
            })
        }
        
        /**
         *
         */
        fun respondToInvite(status: PartyInviteStatus, respondingPlayer: PlayerNameToUuidMapping, request: PartyServerInviteRequestPacket) {
            val resolution = when (status) {
                PartyInviteStatus.ACCEPTED -> InviteStatus.ACCEPTED
                PartyInviteStatus.DENIED   -> InviteStatus.DENIED
                else                       -> InviteStatus.DENIED
            }
            packageService.sendResponse(PartyClientInviteResponsePacket(request.party, respondingPlayer, request.initalSender, resolution), request)
        }
        
        fun toParty(party: AtlantisParty): Party = PartyWrapper(party.id, party.owner, party.members)
    }
}