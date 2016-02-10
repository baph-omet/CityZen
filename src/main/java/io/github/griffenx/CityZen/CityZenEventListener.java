package io.github.griffenx.CityZen;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import io.github.griffenx.CityZen.Tasks.AlertNotifyTask;
import io.github.griffenx.CityZen.Tasks.RewardDisbursementTask;

public class CityZenEventListener implements Listener {
	
	private final CityLog log = CityZen.cityLog;
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		log.write(player.getName() + " logged in.");
		Citizen citizen = Citizen.getCitizen(player);
		if (citizen == null) {
			citizen = Citizen.createCitizen(player);
			log.write("Created Citizen record for " + citizen.getName());
		} else {
			log.write("Fetched Citizen record for " + citizen.getName());
			if (!player.getName().equals(citizen.getName())) citizen.setName(player.getName());
			if (citizen.getAlerts().size() > 0) new AlertNotifyTask(citizen).runTaskLater(CityZen.getPlugin(), 20 * 3);
			if (citizen.getQueuedRewards().size() > 0) {
				List<Reward> rewards = citizen.getQueuedRewards();
				citizen.clearQueuedRewards();
				for (Reward reward : rewards) {
					new RewardDisbursementTask(citizen, reward).runTaskLater(CityZen.getPlugin(), 20 * 3);
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
							if (!plot.getOwners().contains(citizen) && !citizen.isCityOfficial(city)) {
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
							if (!citizen.isCityOfficial(city)) {
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
							if (!plot.getOwners().contains(citizen) && !citizen.isCityOfficial(city)) {
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
							if (!citizen.isCityOfficial(city)) {
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
								if (!plot.getOwners().contains(citizen) && !citizen.isCityOfficial(city)) {
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
								if (!citizen.isCityOfficial(city)) {
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
	
	@EventHandler (priority=EventPriority.NORMAL)
	public void onBucketUse(PlayerBucketEvent event) {
		if (!Util.canBuild(event.getPlayer(), event.getBlockClicked().getLocation())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot use buckets in cities in which you cannot build.");
		}
	}
	
	@EventHandler (priority=EventPriority.NORMAL)
	public void onContainerOpen(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			Material[] types = {
					Material.CHEST,
					Material.TRAPPED_CHEST,
					Material.BEACON,
					Material.FURNACE,
					Material.BURNING_FURNACE,
					Material.DISPENSER,
					Material.DROPPER,
					Material.HOPPER
			};
			for (Material material : types) {
				if (event.getClickedBlock().getType().equals(material)) {
					if (!Util.canBuild(event.getPlayer(), event.getClickedBlock().getLocation())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "You cannot interact with this block in a city in which you cannot build.");
						break;
					}
				}
			}
		}
	}
	
	@EventHandler (priority=EventPriority.LOW)
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
	
	@EventHandler (priority=EventPriority.NORMAL)
	public void onExplosion(EntityExplodeEvent event) {
		for (Block b : event.blockList()) {
			if (City.getCity(b.getLocation()) != null) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler (priority=EventPriority.NORMAL)
	public void onBurn(BlockBurnEvent event) {
		if (City.getCity(event.getBlock().getLocation()) != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority=EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager().getType().equals(EntityType.PLAYER)) {
			Player player = (Player)event.getDamager();
			EntityType[] passives = {
					EntityType.PIG,
					EntityType.COW,
					EntityType.CHICKEN,
					EntityType.SHEEP,
					EntityType.RABBIT,
					EntityType.SQUID,
					EntityType.HORSE,
					EntityType.IRON_GOLEM,
					EntityType.WOLF,
					EntityType.OCELOT,
					EntityType.ITEM_FRAME,
					EntityType.VILLAGER,
					EntityType.SNOWMAN,
					EntityType.PAINTING,
					EntityType.MUSHROOM_COW,
					EntityType.ARMOR_STAND
			};
			
			for (EntityType type : passives) {
				if (type.equals(event.getEntityType())) {
					if (!Util.canBuild(player, event.getEntity().getLocation())) {
						player.sendMessage(ChatColor.RED + "You cannot damage entities in cities in which you cannot build.");
						event.setCancelled(true);
						break;
					}
				}
			}
		}
	}
	
	@EventHandler (priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		City city = City.getCity(player.getLocation());
		if (city != null) {
			if (!player.getMetadata("inCity").get(0).asString().equals(city.getName())) {
				player.setMetadata("inCity", new FixedMetadataValue(CityZen.getPlugin(), city.getName()));
				player.sendMessage(ChatColor.BLUE + "Welcome to " + city.getChatName() + ChatColor.BLUE + "!");
				Citizen citizen = Citizen.getCitizen(player);
				if (citizen != null && city.getCitizens().contains(citizen)) {
					player.sendMessage(ChatColor.BLUE + "Welcome home, " + player.getDisplayName() + ChatColor.BLUE + "!");
				}
			}
		} else {
			if (player.hasMetadata("inCity")) {
				city = City.getCity(player.getMetadata("inCity").get(0).asString());
				player.sendMessage(ChatColor.BLUE + "Now leaving " + city.getChatName() + ChatColor.BLUE + ". Come again soon!");
				player.removeMetadata("inCity", CityZen.getPlugin());
			}
		}
	}
	
}
