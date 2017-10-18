package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.atlantis.client.base.PlayerResolver
import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.party.api.Party
import de.bergwerklabs.party.api.PartyApi
import de.bergwerklabs.party.client.bukkit.bukkitClient
import mkremins.fanciful.FancyMessage
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by Yannic Rieger on 30.09.2017.
 *
 * Lists the player currently in a party.
 *
 * @author Yannic Rieger
 */
class PartyListCommand : ChildCommand {
    
    override fun getName() = "list"
    
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val optional = PartyApi.getParty(sender.uniqueId)
            if (optional.isPresent) {
                val party = optional.get()
                if (party.isOwner(sender.uniqueId)) {
                    this.displayOwnerView(sender, party)
                }
                else this.displayMemberView(sender, party)
            }
            else bukkitClient!!.messenger.message("§cDu bist in keiner Party.", sender)
        }
        return true
    }
    
    /**
     * Display the owner view of a party, providing methods to easily promote or kick players.
     *
     * @param player player to send the messages to.
     * @param party  party that the player is the owner of.
     */
    private fun displayOwnerView(player: Player, party: Party) {
        player.sendMessage("§6§m-----§b Party-Übersicht §6§m-----")
        party.getMembers().forEach { member ->
            val name = PlayerResolver.resolveUuidToName(member).get()
            FancyMessage("✖").color(ChatColor.RED).command("/party kick $name").formattedTooltip(FancyMessage("Entfernt $name von der Party."))
                    .then("☗").color(ChatColor.GREEN).command("/party promote $name").formattedTooltip(FancyMessage("Befördert $name zun neuen Party-Owner."))
                    .then("➥").color(ChatColor.AQUA).command("/party tp $name").formattedTooltip(FancyMessage("Du wirst zu $name teleportiert."))
                    .then(" $name")
                    .send(player)
        }
        player.sendMessage("§6§m-------------------------")
    }
    
    /**
     * Displays the basic view of a party to the given player.
     *
     * @param player player to send the member view to.
     * @param party  party the player is a member of.
     */
    private fun displayMemberView(player: Player, party: Party) {
        player.sendMessage("§6§m-----§b Party-Übersicht §6§m-----")
        val ownerName = PlayerResolver.resolveUuidToName(party.getPartyOwner()).get()
        FancyMessage("■").color(ChatColor.GOLD)
                .then("➥").color(ChatColor.AQUA).command("/party tp $ownerName").formattedTooltip(FancyMessage("Du wirst zu $ownerName teleportiert."))
                .then(ownerName)
                .send(player)
        
        party.getMembers().forEach { member ->
            val memberName = PlayerResolver.resolveUuidToName(member)
            FancyMessage("■").color(ChatColor.GREEN)
                    .then("➥").color(ChatColor.AQUA).command("/party tp $memberName").formattedTooltip(FancyMessage("Du wirst zu $memberName teleportiert."))
                    .send(player)
        }
        player.sendMessage("§6§m-------------------------")
    }
}