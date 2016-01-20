package io.github.griffenx.CityZen;

import org.bukkit.ChatColor;

public class Messaging {
	public static String noPerms(String node) {
		return ChatColor.RED + "You don't have permission to do that. Required permission: " + node;
	}
	
	public static String playersOnly() {
		return ChatColor.RED + "Only Players can run this command. Try \"/cityzen help\" for more info.";
	}
	
	public static String cityNotFound() {
		return ChatColor.RED + "You either do not have an affiliation or you are not in a City in which you can run this command.";
	}
	
	/*public static String cityNotFound(String cityName) {
		return ChatColor.RED + "Could not find a city by the name of \"" + cityName + ".\" Try \"/city list\" for a list of cities. ";
	}*/
	
	public static String noAffiliation(Citizen citizen) {
		return ChatColor.RED + citizen.getName() + " is not a Citizen of any City.";
	}
	
	public static String noAffiliation() {
		return ChatColor.RED + "You are not a Citizen of any City";
	}
	
	public static String citizenNotFound(String citizenName) {
		return ChatColor.RED + "Could not find a Citizen named \"" + citizenName + ".\"";
	}
	
	public static String missingCitizenRecord() {
		return ChatColor.RED + "Your Citizen record does not seem to exist. Try logging in again or contacting an admin for help with this issue.";
	}
	
	public static String notMayor() {
		return ChatColor.RED + "This command can only be run by the Mayor of this City.";
	}
	
	public static String notCityOfficial() {
		return ChatColor.RED + "This command can only be run by the Mayor or a Deputy of this City";
	}
	
	public static String noArguments() {
		return ChatColor.RED + "No arguments supplied. Type \"/cityzen help\" for a list of useable commands.";
	}
	
	public static String invalidArguments(String useage) {
		return ChatColor.RED + "Invalid arguments supplied. Useage: \"" + useage + "\"";
	}
	
	public static String notEnoughArguments(String useage) {
		return ChatColor.RED + "Not enough arguments. Useage: \"" + useage + "\""; 
	}
	
	public static String econDisabled() {
		return ChatColor.RED + "Economy features are disabled on this server.";
	}
	
	public static String noPlotFound() {
		return ChatColor.RED + "No plot found at your location.";
	}
	
	public static String tooManyPlots() {
		return ChatColor.RED + "You cannot own any more plots. Sell or remove some plots first.";
	}
	
	public static String notPlotOwner() {
		return ChatColor.RED + "You must be the owner of this Plot to run this command.";
	}
}
