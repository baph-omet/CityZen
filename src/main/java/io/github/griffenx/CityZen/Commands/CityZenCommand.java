package io.github.griffenx.CityZen.Commands;

public class CityZenCommand {
	public static void delegate(CommandSender sender, String[] args) {
		if (args.length > 0) {
			switch (args[0].toLowerCase().substring(0,1)) {
				case "h":
					//TODO: call help command
					break;
				case "r":
					reload(sender);
					break;
				case "s":
					save(sender);
					break;
			}
		} else {
			info(sender);
		}
	}
	
	private static void save(CommandSender sender) {
		if (sender.hasPermission("cityzen.save")) {
			CityZen.getPlugin().getConfig().save();
			CityZen.getPlugin().cityConfig.save();
			CityZen.getPlugin().citizenConfig.save();
			//TODO: Save rewards config
		} else sender.sendMessage(Messaging.noPerms("cityzen.save"));
	}
	
	private static void reload(CommandSender sender) {
		if (sender.hasPermission("cityzen.reload")) {
			CityZen.getPlugin().getConfig().reload();
			CityZen.getPlugin().cityConfig.reload();
			CityZen.getPlugin().citizenConfig.reload();
			//TODO: Reload rewards config
		} else sender.sendMessage(Messaging.noPerms("cityzen.reload"));
	}
	
	private static void info(CommandSender sender) {
		if (sender.hasPermission("cityzen.info")) {
			//TODO: Print out plugin version, bukkitdev page, etc.
		} else sender.sendMessage(Messaging.noPerms("cityzen.info"));
	}
	
	private static void help(CommandSender sender, String[] args) {
		//TODO: create array of useable commands, then show the ones for the current page
	}
}
