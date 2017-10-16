package de.bergwerklabs.party.client.bukkit.command

import de.bergwerklabs.framework.commons.spigot.command.ChildCommand
import de.bergwerklabs.framework.commons.spigot.command.ParentCommand

/**
 * Created by Yannic Rieger on 01.10.2017.
 *
 * Parent command for all party commands.
 *
 * @author Yannic Rieger
 */
class PartyParentCommand(command: String, vararg subCommands: ChildCommand) : ParentCommand(command, *subCommands)