package io.github.griffenx.CityZen.Tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.griffenx.CityZen.Citizen;

public class AlertNotifyTask extends BukkitRunnable {
	private Citizen target;
	
	public AlertNotifyTask(Citizen target) {
		if (target != null) this.target = target;
	}
	
	@Override
	public void run() {
		if (target != null && target.getAlerts().size() > 0) target.getPlayer().sendMessage(ChatColor.BLUE + "You have " + ChatColor.GOLD 
				+ target.getAlerts().size() + ChatColor.BLUE + " unread Alerts! Type \""
				+ ChatColor.GOLD + "/alert" + ChatColor.BLUE + "\" to read.");
		
	}

}
