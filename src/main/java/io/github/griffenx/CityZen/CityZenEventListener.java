package io.github.griffenx.CityZen;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.griffenx.CityZen.Tasks.AlertNotifyTask;



public class CityZenEventListener implements Listener {
	@EventHandler (priority = EventPriority.MONITOR)
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		Citizen citizen = Citizen.getCitizen(player);
		if (citizen == null) {
			citizen = Citizen.createCitizen(player);
		} else {
			if (!player.getName().equals(citizen.getName())) citizen.setName(player.getName());
			if (citizen.getAlerts().size() > 0) new AlertNotifyTask(citizen).runTaskLater(CityZen.getPlugin(), 20 * 3);
			if (citizen.getRewards().size() > 0) {
				for (Reward reward : citizen.getRewards()) {
					citizen.sendReward(reward);
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		CityZen.citizenConfig.save();
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Citizen citizen = Citizen.getCitizen(event.getPlayer());
		if (citizen != null) {
			for (Reward r : citizen.getQueuedRewards()) {
				citizen.sendReward(r);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		City city = City.getCity(block.getLocation());
		if (city != null) {
			Player player = event.getPlayer();
			Citizen citizen = Citizen.getCitizen(player);
			if (citizen != null) {
				Plot plot = city.getPlot(block.getLocation());
				if (plot != null) {
					switch (plot.getProtectionLevel()) {
						case PUBLIC:
							break;
						case COMMUNAL:
							if (!city.getCitizens().contains(citizen)){
								player.sendMessage(ChatColor.RED + "You can't build here. Only Citizens of " + city.getName() + " can build in this Plot.");
								event.setCancelled(true);
							}
							break;
						case PROTECTED:
							if (!plot.getOwners().contains(citizen) && !citizen.isCityOfficial()) {
								player.sendMessage(ChatColor.RED + "You can't build here. Only owners of this Plot and City officials of " + city.getName() 
									+ " can build in this Plot");
								event.setCancelled(true);
							}
							break;
					}
				} else {
					switch (city.getProtectionLevel()) {
						case PUBLIC:
							break;
						case COMMUNAL:
							if (!city.getCitizens().contains(citizen)){
								player.sendMessage(ChatColor.RED + "You can't build here. Only Citizens of " + city.getName() + " can build between the Plots of the City.");
								event.setCancelled(true);
							}
							break;
						case PROTECTED:
							if (!citizen.isCityOfficial()) {
								player.sendMessage(ChatColor.RED + "You can't build here. Only City officials of " + city.getName() + " can build between the Plots of the City");
								event.setCancelled(true);
							}
							break;
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "You cannot build here. " + Messaging.missingCitizenRecord());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		City city = City.getCity(block.getLocation());
		if (city != null) {
			Player player = event.getPlayer();
			Citizen citizen = Citizen.getCitizen(player);
			if (citizen != null) {
				Plot plot = city.getPlot(block.getLocation());
				if (plot != null) {
					switch (plot.getProtectionLevel()) {
						case PUBLIC:
							if (city.isBlockExclusion()) {
								if (city.isWhitelisted()) {
									if (!city.getBlacklist().contains(block.getType())) {
										player.sendMessage(ChatColor.RED + block.getType().toString() + " is not on the Whitelist for " + city.getName() + ". Please try a different material.");
										event.setCancelled(true);
									}
								} else {
									if (city.getBlacklist().contains(block.getType())) {
										player.sendMessage(ChatColor.RED + block.getType().toString() + " is on the Blacklist for " + city.getName() + ". Please try a different material.");
										event.setCancelled(true);
									}
								}
							}
							break;
						case COMMUNAL:
							if (!city.getCitizens().contains(citizen)){
								player.sendMessage(ChatColor.RED + "You can't build here. Only Citizens of " + city.getName() + " can build in this Plot.");
								event.setCancelled(true);
							} else {
								if (city.isBlockExclusion()) {
									if (city.isWhitelisted()) {
										if (!city.getBlacklist().contains(block.getType())) {
											player.sendMessage(ChatColor.RED + block.getType().toString() + " is not on the Whitelist for " + city.getName() + ". Please try a different material.");
											event.setCancelled(true);
										}
									} else {
										if (city.getBlacklist().contains(block.getType())) {
											player.sendMessage(ChatColor.RED + block.getType().toString() + " is on the Blacklist for " + city.getName() + ". Please try a different material.");
											event.setCancelled(true);
										}
									}
								}
							}
							break;
						case PROTECTED:
							if (!plot.getOwners().contains(citizen) && !citizen.isCityOfficial()) {
								player.sendMessage(ChatColor.RED + "You can't build here. Only owners of this Plot and City officials of " + city.getName() 
									+ " can build in this Plot");
								event.setCancelled(true);
							} else {
								if (city.isBlockExclusion()) {
									if (city.isWhitelisted()) {
										if (!city.getBlacklist().contains(block.getType())) {
											player.sendMessage(ChatColor.RED + block.getType().toString() + " is not on the Whitelist for " + city.getName() + ". Please try a different material.");
											event.setCancelled(true);
										}
									} else {
										if (city.getBlacklist().contains(block.getType())) {
											player.sendMessage(ChatColor.RED + block.getType().toString() + " is on the Blacklist for " + city.getName() + ". Please try a different material.");
											event.setCancelled(true);
										}
									}
								}
							}
							break;
					}
				} else {
					switch (city.getProtectionLevel()) {
						case PUBLIC:
							if (city.isBlockExclusion()) {
								if (city.isWhitelisted()) {
									if (!city.getBlacklist().contains(block.getType())) {
										player.sendMessage(ChatColor.RED + block.getType().toString() + " is not on the Whitelist for " + city.getName() + ". Please try a different material.");
										event.setCancelled(true);
									}
								} else {
									if (city.getBlacklist().contains(block.getType())) {
										player.sendMessage(ChatColor.RED + block.getType().toString() + " is on the Blacklist for " + city.getName() + ". Please try a different material.");
										event.setCancelled(true);
									}
								}
							}
							break;
						case COMMUNAL:
							if (!city.getCitizens().contains(citizen)){
								player.sendMessage(ChatColor.RED + "You can't build here. Only Citizens of " + city.getName() + " can build between the Plots of the City.");
								event.setCancelled(true);
							} else {
								if (city.isBlockExclusion()) {
									if (city.isWhitelisted()) {
										if (!city.getBlacklist().contains(block.getType())) {
											player.sendMessage(ChatColor.RED + block.getType().toString() + " is not on the Whitelist for " + city.getName() + ". Please try a different material.");
											event.setCancelled(true);
										}
									} else {
										if (city.getBlacklist().contains(block.getType())) {
											player.sendMessage(ChatColor.RED + block.getType().toString() + " is on the Blacklist for " + city.getName() + ". Please try a different material.");
											event.setCancelled(true);
										}
									}
								}
							}
							break;
						case PROTECTED:
							if (!citizen.isCityOfficial()) {
								player.sendMessage(ChatColor.RED + "You can't build here. Only City officials of " + city.getName() + " can build between the Plots of the City");
								event.setCancelled(true);
							} else {
								if (city.isBlockExclusion()) {
									if (city.isWhitelisted()) {
										if (!city.getBlacklist().contains(block.getType())) {
											player.sendMessage(ChatColor.RED + block.getType().toString() + " is not on the Whitelist for " + city.getName() + ". Please try a different material.");
											event.setCancelled(true);
										}
									} else {
										if (city.getBlacklist().contains(block.getType())) {
											player.sendMessage(ChatColor.RED + block.getType().toString() + " is on the Blacklist for " + city.getName() + ". Please try a different material.");
											event.setCancelled(true);
										}
									}
								}
							}
							break;
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "You cannot build here. " + Messaging.missingCitizenRecord());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onhangingBreak(HangingBreakByEntityEvent event) {
		City city = City.getCity(event.getEntity().getLocation());
		if (city != null) {
			if (event.getRemover().getType() == EntityType.PLAYER) {
				Player player = (Player)event.getRemover();
				Citizen citizen = Citizen.getCitizen(player);
				if (citizen != null) {
					Plot plot = city.getPlot(event.getEntity().getLocation());
					if (plot != null) {
						switch(city.getProtectionLevel()) {
							case PUBLIC:
								break;
							case COMMUNAL:
								if (!city.getCitizens().contains(citizen)){
									player.sendMessage(ChatColor.RED + "You can't break entities here. Only Citizens of " + city.getName() + " can build in this Plot.");
									event.setCancelled(true);
								}
								break;
							case PROTECTED:
								if (!plot.getOwners().contains(citizen) && !citizen.isCityOfficial()) {
									player.sendMessage(ChatColor.RED + "You can't break entities here. Only owners of this Plot and City officials of " + city.getName() 
										+ " can build in this Plot");
									event.setCancelled(true);
								}
								break;
						}
					} else {
						switch (city.getProtectionLevel()) {
							case PUBLIC:
								break;
							case COMMUNAL:
								if (!city.getCitizens().contains(citizen)){
									player.sendMessage(ChatColor.RED + "You can't break entities. Only Citizens of " + city.getName() + " can build between the Plots of the City.");
									event.setCancelled(true);
								}
								break;
							case PROTECTED:
								if (!citizen.isCityOfficial()) {
									player.sendMessage(ChatColor.RED + "You can't break entities. Only City officials of " + city.getName() + " can build between the Plots of the City");
									event.setCancelled(true);
								}
								break;
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "You cannot break entities. " + Messaging.missingCitizenRecord());
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onDoorBreak(EntityBreakDoorEvent event) {
		if (!event.getEntityType().equals(EntityType.PLAYER)) {
			City city = City.getCity(event.getBlock().getLocation());
			if (city != null) {
				event.setCancelled(true);
			}
		}
	}
	
	public void onMobSpawn(CreatureSpawnEvent event) {
		Location location = event.getLocation();
		City city = City.getCity(location);
		if (city != null) {
			EntityType[] aggressives = {
					EntityType.BLAZE,
					EntityType.CAVE_SPIDER,
					EntityType.CREEPER,
					EntityType.ENDER_DRAGON,
					EntityType.ENDERMAN,
					EntityType.ENDERMITE,
					EntityType.GHAST,
					EntityType.GIANT,
					EntityType.GUARDIAN,
					EntityType.MAGMA_CUBE,
					EntityType.PIG_ZOMBIE,
					EntityType.SILVERFISH,
					EntityType.SKELETON,
					EntityType.SLIME,
					EntityType.SPIDER,
					EntityType.WITCH,
					EntityType.WITHER,
					EntityType.ZOMBIE
			};
			for (EntityType type : aggressives) {
				if (type.equals(event.getEntityType())) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}
}
