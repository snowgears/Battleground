package com.snowgears.battleground;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.snowgears.battleground.domination.Base;
import com.snowgears.battleground.domination.BaseHandler;
import com.snowgears.battleground.utilities.SerializableLocation;
import com.snowgears.battleground.utilities.UnzipUtility;

public class FileManager {

	public Battleground plugin = Battleground.plugin;
	private HashMap<String, File> worldDataFiles = new HashMap<String, File>(); //worldName, data directory
	
	public FileManager(Battleground instance){
		plugin = instance;
		generateAndLoad();
	}
	
	public void saveAllFiles(){
		//go through worldData hashmap and save each worldFile to its own file.

		for(Entry<String, File> entry : worldDataFiles.entrySet()){
			String worldName = entry.getKey();
			File dataDirectory = entry.getValue();
			
			File basesFile = new File(dataDirectory + "/bases.yml");
			if(basesFile.exists() && basesFile.length() > 0){
				HashMap<String, ArrayList<Base>> tempBases = new HashMap<String, ArrayList<Base>>();
				tempBases.put(worldName, plugin.baseHandler.getAllBasesInWorld(worldName));
				Battleground.tools.saveHashMapTo(tempBases, basesFile);
			}
			
			File worldLocsFile = new File(dataDirectory + "/worldLocations.yml");
			if(worldLocsFile.exists() && worldLocsFile.length() > 0){
				HashMap<String, WorldLocations> tempWorldLocs = new HashMap<String, WorldLocations>();
				tempWorldLocs.put(worldName, plugin.worldHandler.getWorldLocations(worldName));
				Battleground.tools.saveHashMapTo(tempWorldLocs, worldLocsFile);
			}
		}
		plugin.playerStatsHandler.saveAllPlayerStats();
	}
	
	private void generateAndLoad(){
		File configFile = new File(plugin.getDataFolder() + "/config.yml");
		if(!configFile.exists())
		{
		  plugin.saveDefaultConfig();
		}
		
		generateDataFolder();
		
		File fileDirectory = new File(plugin.getDataFolder(), "Data");
		File stopGenFile = new File(fileDirectory, "StopGenerate.txt");
		if(!stopGenFile.exists()){
			
			try {
				stopGenFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			File worldDirectory = new File(plugin.getServer().getWorldContainer(), "bg_domination");
			if(!worldDirectory.exists())
			{
				boolean success = false;
				success = (worldDirectory.mkdirs());
				if (!success) {
					plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.RED+" Battleground domination world could not be created.");
				}
				else{ //directory creation successful
					plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.GREEN+" Battleground domination world generated successfully.");
					
					File dest = new File(new File(plugin.getServer().getWorldContainer(), "bg_domination") + "/bg_domination.zip");
					Battleground.tools.copyFileIfNotPresent("/files/bg_domination.zip", dest); //copy zip file into world folder
					
					File zipFile = new File(worldDirectory + "/bg_domination.zip");
					
					UnzipUtility uu = new UnzipUtility();
					//try to unzip the file into the battleground world folder
					try {
						uu.unzip(zipFile.getAbsolutePath(), worldDirectory.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		//this is where the key value of "old world" name is changed to whatever world the file is now nested in.
		generateFilesInWorldFolders();

		//worldHandler will register all battle worlds in the server
		plugin.worldHandler = new WorldHandler(plugin);
		plugin.baseHandler = new BaseHandler(plugin);
		
		
		// this will generate default values into the world on first run if the world exists
		File basesFile = null;
		File worldLocsFile = null;
		if(plugin.getServer().getWorld("bg_domination") != null){
			for(File file : worldDataFiles.get("bg_domination").listFiles()){
				if(file.getName().equals("bases.yml"))
					basesFile = file;
				else if(file.getName().equals("worldLocations.yml"))
					worldLocsFile = file;
			}
		}
		//transfer file from build path to data folder here
		if(basesFile != null)
			Battleground.tools.copyFileIfNotPresent("/files/bases.yml", basesFile); //copy yml file into data folder
		if(worldLocsFile != null)
			Battleground.tools.copyFileIfNotPresent("/files/worldLocations.yml", worldLocsFile); //copy yml file into data folder
		
		
		loadAllDataFromWorldFiles();

		generateDependancyPlugins();
	}
	
	private void generateDependancyPlugins(){
		boolean generated = false;
		
		//Put ProtocolLib plugin in the server files
		if(plugin.getServer().getPluginManager().getPlugin("ProtocolLib") == null){
			File dest = new File(new File("plugins/") + "/ProtocolLib.jar");
			if(!(dest.exists())){
				Battleground.tools.copyFileIfNotPresent("/files/ProtocolLib.jar", dest); //copy jar file into plugins folder
				plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.GREEN+" Successfully installed ProtocolLib.");
				generated = true;
			}
		}
		//Put TabAPI plugin in the server files
		if(plugin.getServer().getPluginManager().getPlugin("TabAPI") == null){
			File dest = new File(new File("plugins/") + "/TabAPI.jar");
			if(!(dest.exists())){
				Battleground.tools.copyFileIfNotPresent("/files/TabAPI.jar", dest); //copy jar file into plugins folder
				plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.GREEN+" Successfully installed TabAPI.");
				generated = true;
			}
		}
		//Put ProtocolLib plugin in the server files
		if(plugin.getServer().getPluginManager().getPlugin("TagAPI") == null){
			File dest = new File(new File("plugins/") + "/TagAPI.jar");
			if(!(dest.exists())){
				Battleground.tools.copyFileIfNotPresent("/files/TagAPI.jar", dest); //copy jar file into plugins folder
				plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.GREEN+" Successfully installed TagAPI.");
				generated = true;
			}
		}
		
		if(generated){
			plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() { 
				public void run() { 
					plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.DARK_RED+" You must STOP AND RESTART the server for Battleground to function correctly.");
					} 
			}, 40L, 200L); //2 seconds, every 10 seconds
		}
	}
   
	
	private void generateFilesInWorldFolders(){
		//go through and generate all files within all battleworld directories
		File worldContainer = plugin.getServer().getWorldContainer();
		
		for(File worldDirectory : worldContainer.listFiles()){
			if(worldDirectory.getName().contains("bg_") && worldDirectory.isDirectory()){
				File dataDirectory = new File(worldDirectory + "/Battleground Data");
				if(!dataDirectory.exists())
					dataDirectory.mkdirs();
				
				if(worldDirectory.getName().contains("domination")){
					File basesFile = new File(dataDirectory + "/bases.yml");
					File worldLocationsFile = new File(dataDirectory + "/worldLocations.yml");
					
					if(!basesFile.exists())
						try {
							basesFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					if(!worldLocationsFile.exists())
						try {
							worldLocationsFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					worldDataFiles.put(worldDirectory.getName(), dataDirectory);
//					System.out.println("Putting this world in worldDataFiles HashMap "+worldDirectory.getName());
				}
				//When you add more gamemodes, add the data files generation here
			}
		}
	}
	
	private void generateDataFolder(){
		File fileDirectory = new File(plugin.getDataFolder(), "Data");
		if(!fileDirectory.exists())
		{
			boolean success = false;
			success = fileDirectory.mkdirs();
			if (!success) {
				plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.RED+" Data folder could not be created.");
			}
		}
		File playerDataDirectory = new File(fileDirectory, "Players");
		if(!playerDataDirectory.exists())
		{
			boolean success = false;
			success = playerDataDirectory.mkdirs();
			if (!success) {
				plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.RED+" Player Data folder could not be created.");
			}
		}
	}
	
	private void loadAllDataFromWorldFiles(){
		HashMap<String, ArrayList<Base>> basesMap = new HashMap<String, ArrayList<Base>>();
		HashMap<String, WorldLocations> worldLocsMap = new HashMap<String, WorldLocations>();
		
		for(Entry<String, File> entry : worldDataFiles.entrySet()){
//			String worldName = entry.getKey();
			File dataDirectory = entry.getValue();
			
			File basesFile = new File(dataDirectory + "/bases.yml");
			if(! basesFile.exists()){ // file doesn't exist
				try {
					basesFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{ //file does exist
				if (basesFile.length() > 0 ) { //file contains something
					HashMap<String, ArrayList<Base>> basesTemp = Battleground.tools.loadHashMapFrom(basesFile);
					basesMap.putAll(basesTemp);
				}
			}
			
			File worldLocsFile = new File(dataDirectory + "/worldLocations.yml");
			if(! worldLocsFile.exists()){ // file doesn't exist
				try {
					worldLocsFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{ //file does exist
				if (worldLocsFile.length() > 0 ) { //file contains something
					HashMap<String, WorldLocations> worldLocsTemp = Battleground.tools.loadHashMapFrom(worldLocsFile);
					worldLocsMap.putAll(worldLocsTemp);
				}
			}
		}
		plugin.worldHandler.setAllWorldLocationsOnLoad(worldLocsMap);
		plugin.baseHandler.setAllBasesOnLoad(basesMap);
	}
	
	private File getWorldDataFile(String world){
		if(worldDataFiles.containsKey(world))
			return worldDataFiles.get(world);
		return null;
	}
}
