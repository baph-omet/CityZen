package io.github.griffenx.CityZen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CityLog {
	private String filename;
	private String filepath;
	private File file = null;
	private FileWriter writer;
	private boolean debug;
	
	public CityLog() {
		filepath = CityZen.getPlugin().getDataFolder().getPath() + "/Logs";
		filename = CityLog.generateLogName();
		if (CityZen.getPlugin().getConfig().getBoolean("logEnabled")) {
			file = new File(filepath + "/" + filename);
			debug = CityZen.getPlugin().getConfig().getBoolean("logDebug");
			try {
				int id = 0;
				while (file.exists()) {
					id++;
					if (filename.contains("_")) filename = filename.replace(".log", "").split("_")[0] + "_" + id + ".log";
					else filename = filename.replace(".log", "") + "_" + id + ".log";
					file = new File(filepath + "/" + filename);
				}
				if (!file.exists()) {
					new File(filepath).mkdirs();
					file.createNewFile();
				}
				file.setWritable(true);
				writer = new FileWriter(file);
			} catch (IOException e) {
				CityZen.getPlugin().getLogger().severe("Error setting up log file.\n" + e.toString());
			}
		}
	}
	
	public void write(String text) {
		if (file != null) {
			try {
				String timeStamp = "[" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + "] ";
				writer.write(timeStamp + text + "\n");
				writer.flush();
			} catch (IOException e) {
				CityZen.getPlugin().getLogger().severe("Error writing to log file.\n" + e.toString());
			}
		}
	}
	
	public void debug(String text) {
		if (debug) write(text);
	}
	
	public String getFileName() {
		return filename;
	}
	
	public static String generateLogName() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".log";
	}
}
