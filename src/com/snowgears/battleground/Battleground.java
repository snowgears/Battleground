package com.snowgears.battleground;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.snowgears.battleground.domination.BaseHandler;
import com.snowgears.battleground.domination.DominationListener;
import com.snowgears.battleground.domination.DominationGame;
import com.snowgears.battleground.stats.PlayerStatsHandler;
import com.snowgears.battleground.utilities.Metrics;
import com.snowgears.battleground.utilities.ToolMethods;
import com.snowgears.battleground.voting.VotingManager;


public class Battleground extends JavaPlugin{
	
	public final BattleworldListener alisten = new BattleworldListener(this);
	public final PlayerEditListener playerEditListener = new PlayerEditListener(this);
	public final DominationListener captureBaseListener = new DominationListener(this);
	public final PlayerListener playerListener = new PlayerListener(this);
	
	public DominationGame dominationGame = new DominationGame(this);
	public WorldHandler worldHandler = null;
	public BaseHandler baseHandler = null;
	public FileManager fileManager = null;
	public VotingManager votingManager = null;
	public PlayerDataHandler playerDataHandler = new PlayerDataHandler(this);
	public TeamManager playerManager = new TeamManager(this);
	public PlayerStatsHandler playerStatsHandler = new PlayerStatsHandler();
	public static ToolMethods tools = new ToolMethods();
	
	public static Battleground plugin;
	protected FileConfiguration config; 

	public static ArrayList<String> playersReadyForBG = new ArrayList<String>();
	
	public static int basinPlayersToStart = 0;
	public static int basinMaxPlayers = 0;
	public static boolean usePerms = false;

	public void onEnable(){
		plugin = this;
		getServer().getPluginManager().registerEvents(alisten, this);
		getServer().getPluginManager().registerEvents(playerEditListener, this);
		getServer().getPluginManager().registerEvents(captureBaseListener, this);
		getServer().getPluginManager().registerEvents(playerListener, this);
		getCommand("bg").setExecutor(new BattlegroundCommandExecutor(this));
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats
		}

		basinPlayersToStart = getConfig().getInt("dominationPlayersToStart");
		basinMaxPlayers = getConfig().getInt("dominationMaxPlayers");
		usePerms = getConfig().getBoolean("usePermissions");
		
		//file manager will handle all of the generation of necessary files and worlds
		fileManager = new FileManager(this);
		
		//voting manager will handle all of the voting that takes place for map selection
		votingManager = new VotingManager(this);

		//always daytime in Battleground worlds
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() { 
				public void run() { 
					for(String world : worldHandler.getAllBattleWorlds())
						getServer().getWorld(world).setTime(6000);
					} 
			}, 600L, 600L); 
		
		//instantiate a blank worldLocations for all battle worlds so no null pointers occur
		for(String world : worldHandler.getAllBattleWorlds()){
			if(worldHandler.getWorldLocations(world) == null)
				worldHandler.setWorldLocations(world, new WorldLocations(world));
		}
	}
	
	public void onDisable(){

		if(worldHandler.getCurrentWorld() != null && worldHandler.getCurrentWorld().isEmpty()==false){
			playerDataHandler.returnDataToAllPlayersInWorld(worldHandler.getCurrentWorld());
		}
		
		fileManager.saveAllFiles();

		worldHandler.resetWorld(worldHandler.getCurrentWorld());
	}
	
//	@EventHandler
//	public void onSplash(PotionSplashEvent event)
//	{
//	    ThrownPotion potion = event.getEntity();
//	    for(PotionEffect effect : potion.getEffects())
//	    {
//	        if(effect.getType() == PotionEffectType.INVISIBILITY)
//	        {
//	            Collection<LivingEntity> les = event.getAffectedEntities(); // To prevent the concurrent exception
//	            final ArrayList<String> unaffected = new ArrayList<String>();
//	            for(LivingEntity le : les)
//	            {
//	                if(le instanceof Player)
//	                {
//	                    if(((Player)le).hasPermission("no.invis.perm"))
//	                    {
//	                        unaffected.add(((Player)le).getName());
//	                    }
//	                }
//	            }
//	            if(unaffected.size() > 0)
//	            {
//	                Bukkit.getScheduler().scheduleSyncDelayedTask(ReplaceWithMainClassInstance, new Runnable()
//	                {
//	                    @Override
//	                    public void run()
//	                    {
//	                        for(String name : unaffected)
//	                        {
//	                            Player p = Bukkit.getPlayer(name);
//	                            if(p != null)
//	                            {
//	                                p.removePotionEffect(PotionEffectType.INVISIBILITY);
//	                            }
//	                        }                   
//	                    }
//	                }, 1L);
//	            }
//	        }
//	    }
//	}
}