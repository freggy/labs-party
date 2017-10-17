package de.bergwerklabs.party.client.bukkit

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Created by Yannic Rieger on 17.10.2017.
 *
 * Handles Bukkit events.
 *
 * @author Yannic Rieger
 */
class PartyEventListener : Listener {
    
    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        // TODO: update player client data.
    }
}