package com.snowgears.battleground.utilities;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerializableLocation implements Serializable {

	
	private static final long serialVersionUID = -2093336188722015371L;
	private final String world;
	private final double x, y, z;
	private final float yaw, pitch;
	
	public SerializableLocation(Location loc) {
	    world = loc.getWorld().getName();
		//world = "Battleground";
	    x = loc.getX();
	    y = loc.getY();
	    z = loc.getZ();
	    yaw = loc.getYaw();
	    pitch = loc.getPitch();
	}
	
	public Location deserialize() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
