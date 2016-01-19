package io.github.griffenx.CityZen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.griffenx.CityZen.Commands.CitizenCommand;
import io.github.griffenx.CityZen.Commands.CityCommand;
import io.github.griffenx.CityZen.Commands.CityDeputyCommand;
import io.github.griffenx.CityZen.Commands.CityExclusionCommand;
import io.github.griffenx.CityZen.Commands.CitySetCommand;
import io.github.griffenx.CityZen.Commands.CityZenCommand;
import io.github.griffenx.CityZen.Commands.CityZenRewardCommand;
import io.github.griffenx.CityZen.Commands.InfoCommand;
import io.github.griffenx.CityZen.Commands.PlotCommand;
import io.github.griffenx.CityZen.Tasks.AlertNotifyTask;

public class Commander implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		String cmdName = command.getName().toLowerCase();
		if (sender instanceof Player) {
			Citizen citizen = Citizen.getCitizen(sender);
			if (citizen != null) if (citizen.getAlerts().size() > 0) new AlertNotifyTask(citizen).runTaskLater(CityZen.getPlugin(), 20 * 3);
		}
		
		switch (cmdName) {
			case "psp":
			case "passport":
				InfoCommand.passport(sender,args);
				return true;
			case "rep":
			case "reputation":
				InfoCommand.reputation(sender,args);
				return true;
			case "alert":
			case "alerts":
				InfoCommand.alert(sender);
				return true;
			case "ctz":
			case "citizen":
				if (args.length > 0) return CitizenCommand.delegate(sender, args);
				else {
					sender.sendMessage(Messaging.noArguments());
					break;
				}
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
			case "cityzen":
				if (args.length > 0 && args[0].contains("reward")) return CityZenRewardCommand.delegate(sender, args);
				else CityZenCommand.delegate(sender, args);
				return true;
		}
		return false;
	}
}
