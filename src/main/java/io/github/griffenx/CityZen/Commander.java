package io.github.griffenx.CityZen;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.griffenx.CityZen.Commands.CitizenCommand;
import io.github.griffenx.CityZen.Commands.CityCommand;
import io.github.griffenx.CityZen.Commands.CityDeputyCommand;
import io.github.griffenx.CityZen.Commands.CityExclusionCommand;
import io.github.griffenx.CityZen.Commands.CitySetCommand;
import io.github.griffenx.CityZen.Commands.InfoCommand;
import io.github.griffenx.CityZen.Commands.PlotCommand;

public class Commander implements CommandExecutor {
	private final CityZen plugin;
	
	public Commander(CityZen plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		String cmdName = command.getName().toLowerCase();
		
		switch (cmdName) {
			case "psp":
			case "passport":
				return InfoCommand.passport(sender,command,args);
			case "rep":
			case "reputation":
				return InfoCommand.reputation(sender,args);
			case "ctz":
			case "citizen":
				return CitizenCommand.delegate(sender, args);
			case "cty":
			case "city":
				if (args.length > 0) {
					switch (args[0].toLowerCase()) {
						case "deputy":
							return CityDeputyCommand.delegate(sender, args);
						case "exclusion":
							return CityExclusionCommand.delegate(sender, args);
						case "set":
							return CitySetCommand.delegate(sender, args);
						default:
							return CityCommand.delegate(sender, args);
					}
				} else {
					sender.sendMessage(Messaging.noArguments());
					break;
				}
			case "plt":
			case "plot":
				return PlotCommand.delegate(sender, args);
		}
		return false;
	}
}
