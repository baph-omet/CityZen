package io.github.griffenx.CityZen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CityZenCommander implements CommandExecutor {
	private final CityZen plugin;
	
	public CityZenCommander(CityZen plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equalsIgnoreCase("city") || command.getName().equalsIgnoreCase("cityzen") || command.getName().equalsIgnoreCase("citizen")) {
			if (args[0].equalsIgnoreCase(""))
		}
		return false;
	}
}
