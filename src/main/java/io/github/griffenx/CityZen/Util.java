package io.github.griffenx.CityZen;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Util {
	public static String collapseArguments(String[] args, int start, int stop) {
		final StringBuilder builder = new StringBuilder(args[start]);
		for (int i = start + 1; i < stop && i < args.length; i++)
			builder.append(" " + args[i]);
		return builder.toString();
	}

	public static String collapseArguments(String[] args, int start) {
		return collapseArguments(args, start, args.length);
	}

	public static String collapseArgsWithoutCityName(String[] args, int start, String cityName) {
		return collapseArgsWithoutCityName(args, start, cityName, " ");
	}

	public static String collapseArgsWithoutCityName(String[] args, int start, String cityName, String delimiter) {
		int adjustment = cityName.split(" ").length;
		if (stringJoin(" ", args).contains(cityName))
			adjustment = 0;
		final StringBuilder joinedArgs = new StringBuilder(args[start]);
		for (int i = start + 1; i < args.length - adjustment; i++)
			joinedArgs.append(delimiter + args[i]);
		return joinedArgs.toString();
	}

	/**
	 * Works backwards from the end of the list of arguments of a command by
	 * building a list of City names matched, then picks the one with the largest
	 * number of words.
	 * 
	 * @param args The command arguments to parse
	 * @return Returns the name of a City found in the arguments with the longest
	 *         name. If no City name is found, returns null.
	 */
	public static String findCityName(String[] args) {
		return findCityName(args, 0);
	}

	/**
	 * Works backwards from the end of the list of arguments of a command to the
	 * specified stop index by building a list of City names matched, then picks the
	 * one with the largest number of words.
	 * 
	 * @param args The command arguments to parse
	 * @param stop The argument index after which to stop
	 * @return Returns the name of a City found in the arguments with the longest
	 *         name. If no City name is found, returns null.
	 */
	public static String findCityName(String[] args, int stop) {
		final List<City> citiesMatched = new Vector<>();
		for (int i = args.length - 1; i >= stop; i--) {
			final String name = findPartialCityName(collapseArguments(args, i, args.length));
			final City city = City.getCity(name);
			if (city != null)
				citiesMatched.add(city);
		}
		if (citiesMatched.size() == 0)
			return null;
		if (citiesMatched.size() == 1)
			return citiesMatched.get(0).getName();
		else {
			City mostComplexName = null;
			for (final City c : citiesMatched)
				if (mostComplexName == null
						|| c.getName().split(" ").length > mostComplexName.getName().split(" ").length)
					mostComplexName = c;
			if (mostComplexName == null)
				return null;
			else
				return mostComplexName.getName();
		}
	}

	public static String findPartialCityName(String nameForm) {
		final List<String> matchedNames = new ArrayList<>();
		for (final City c : City.getCities()) {
			if (nameForm.equalsIgnoreCase(c.getName()))
				return c.getName();
			if (c.getName().contains(nameForm))
				matchedNames.add(c.getName());
		}

		int highestRank = -1;
		String bestMatch = null;
		for (final String n : matchedNames) {
			final int position = n.indexOf(nameForm);
			if (position == 0)
				return n;
			if (highestRank == -1 || position < highestRank) {
				highestRank = position;
				bestMatch = n;
			}
		}
		return bestMatch;
	}

	public static double getDistace(Position pos1, Position pos2) {
		return Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.z - pos2.z, 2));
	}

	public static boolean canBuild(Player player, Location location) {
		final City city = City.getCity(location);
		if (city != null) {
			final Citizen citizen = Citizen.getCitizen(player);
			if (citizen == null)
				return false;
			final Plot plot = city.getPlot(location);
			if (plot != null)
				switch (plot.getProtectionLevel()) {
				case PUBLIC:
					break;
				case COMMUNAL:
					if (!city.getCitizens().contains(citizen))
						return false;
					break;
				case PROTECTED:
					if (!plot.getOwners().contains(citizen) && !citizen.isCityOfficial(city))
						return false;
					break;
				}
			else
				switch (city.getProtectionLevel()) {
				case PUBLIC:
					break;
				case COMMUNAL:
					if (!city.getCitizens().contains(citizen))
						return false;
					break;
				case PROTECTED:
					if (!citizen.isCityOfficial(city))
						return false;
					break;
				}
		}
		return true;
	}

	public static String stringJoin(String delimiter, String[] args) {
		if (args.length == 0)
			return "";
		final StringBuilder str = new StringBuilder(args[0]);
		for (int i = 1; i < args.length; i++)
			str.append(delimiter + args[i]);
		return str.toString();
	}
}
