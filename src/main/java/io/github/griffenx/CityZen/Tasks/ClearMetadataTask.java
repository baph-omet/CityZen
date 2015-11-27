package io.github.griffenx.CityZen.Tasks;

import org.bukkit.metadata.Metadatable;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.griffenx.CityZen.CityZen;

public class ClearMetadataTask extends BukkitRunnable {
	private Metadatable target;
	private String metadataKey;
	
	public ClearMetadataTask(Metadatable target, String metadataKey) {
		this.target = target;
		this.metadataKey = metadataKey;
	}

	@Override
	public void run() {
		if (target.hasMetadata(metadataKey)) target.removeMetadata(metadataKey, CityZen.getPlugin());
	}
}
