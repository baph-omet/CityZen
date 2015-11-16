package io.github.griffenx.CityZen;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.griffenx.CityZen.Commands.InfoCommand;

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
				return InfoCommand.reputation(sender,command,args);
			case "ctz":
			case "citizen":
				return citizenCommand(sender, command, args);
			case "cty":
			case "city":
				return cityCommand(sender, command, args);
			case "plt":
			case "plot":
				return plotCommand(sender, command, args);
		}
		return false;
	}
	
	public boolean
}
