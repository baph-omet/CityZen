package io.github.griffenx.CityZen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CityZenCommander implements CommandExecutor {
	private final CityZen plugin;
	
	public CityZenCommander(CityZen plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		String cmdName = command.getName();
		
		if ((cmdName.equalsIgnoreCase("reputation") || cmdName.equalsIgnoreCase("rep"))) {
			if (args.length == 0 && sender instanceof Player && sender.hasPermission("cityzen.reputation.self")) {
				//TODO: Get self reputation
			}
			if (args.length > 0 && (sender instanceof ConsoleCommandSender || sender.hasPermission("cityzen.reputation.other"))) {
				//TODO: Print rep of another player
			}
		}
		
		else if (cmdName.equalsIgnoreCase("city")) {
			
		}
		else if (cmdName.equalsIgnoreCase("citizen")) {
			if (args[0].equalsIgnoreCase("")) {}
		}
		return false;
	}
}
