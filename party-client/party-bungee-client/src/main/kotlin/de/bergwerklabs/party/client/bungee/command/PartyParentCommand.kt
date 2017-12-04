package de.bergwerklabs.party.client.bungee.command

import de.bergwerklabs.framework.commons.bungee.command.BungeeCommand
import de.bergwerklabs.framework.commons.bungee.command.BungeeParentCommand


/**
 * Created by Yannic Rieger on 01.10.2017.
 *
 * Parent command for all party commands.
 *
 * @author Yannic Rieger
 */
class PartyParentCommand(name: String?, description: String?, usage: String?, defaultCommand: BungeeCommand?, vararg childCommands: BungeeCommand?) : BungeeParentCommand(name, description, usage, defaultCommand, *childCommands)