package io.github.griffenx.CityZen;

import org.bukkit.ChatColor;

public class Messaging {
	public static String noPermMessage(String node) {
		return ChatColor.RED + "You don't have permission to do that. Required permission: " + node;
	}
	
	public static String playersOnlyMessage() {
		return ChatColor.RED + "Only Players can run this command. Try \"/cityzen help\" for more info.";
	}
	
	public static String cityNotFoundMessage(String cityName) {
		return ChatColor.RED + "Could not find a city by the name of \"" + cityName + ".\" Try \"/city list\" for a list of cities. ";
	}
	
	public static String citizenNotFoundMessage(String citizenName) {
		return ChatColor.RED + "Could not find a Citizen named \"" + citizenName + ".\"";
	}
	
	public static String missingCitizenRecordMessage() {
		return ChatColor.RED + "Your Citizen record does not seem to exist. Try logging in again or contacting an admin for help with this issue.";
	}
}
