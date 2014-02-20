package com.snowgears.battleground.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class ToolMethods {

	public void copyFileIfNotPresent(String jarNestedFile, File dest) {
		InputStream in = getClass().getResourceAsStream(jarNestedFile);
		
		OutputStream out = null;
		try {
			out = new FileOutputStream(dest,true);
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		} 
		byte[] buf = new byte[1024];
        int len;
        try {
			while ((len = in.read(buf)) > 0) {
			    try {
					out.write(buf, 0, len);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public <K, V> void saveHashMapTo(HashMap<K, V> hashmap, File file) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(hashmap);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
   
    /**
    * Loads a HashMap<K, V> from a file.
    * @param file : The file from which the HashMap will be loaded.
    * @return Returns a HashMap that was saved in the file.
    */
    @SuppressWarnings("unchecked")
    public <K, V> HashMap<K, V> loadHashMapFrom(File file) {
        HashMap<K, V> result = null;
        ObjectInputStream ois = null;
       
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            result = (HashMap<K, V>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       
        return result;
    }
    
    public boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	public boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
    
    public void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
    
    public File getFileInDirectory(File directory, final String fileName, final String fileType){
    	if(directory.isDirectory()==false)
			return null;
    	File[] matchingFiles = directory.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith(fileName) && name.endsWith(fileType);
		    }
		});
	    return null;
	}
    
    public LinkedHashMap sortHashMapByValues(HashMap passedMap) {
    	   List mapKeys = new ArrayList(passedMap.keySet());
    	   List mapValues = new ArrayList(passedMap.values());
    	   Collections.sort(mapValues);
    	   Collections.sort(mapKeys);

    	   LinkedHashMap sortedMap = 
    	       new LinkedHashMap();

    	   Iterator valueIt = mapValues.iterator();
    	   while (valueIt.hasNext()) {
    	       Object val = valueIt.next();
    	    Iterator keyIt = mapKeys.iterator();

    	    while (keyIt.hasNext()) {
    	        Object key = keyIt.next();
    	        String comp1 = passedMap.get(key).toString();
    	        String comp2 = val.toString();

    	        if (comp1.equals(comp2)){
    	            passedMap.remove(key);
    	            mapKeys.remove(key);
    	            sortedMap.put((String)key, (Double)val);
    	            break;
    	        }

    	    }

    	}
    	return sortedMap;
    }
    
    public boolean isInRectangle(Player player, Location loc1, Location loc2){
		
		double[] dim = new double[2];
	 
		dim[0] = loc1.getX();
		dim[1] = loc2.getX();
		Arrays.sort(dim);
		if(player.getLocation().getX() > dim[1] || player.getLocation().getX() < dim[0])
			return false;
	 
		dim[0] = loc1.getZ();
		dim[1] = loc2.getZ();
		Arrays.sort(dim);
		if(player.getLocation().getZ() > dim[1] || player.getLocation().getZ() < dim[0])
			return false;
	 
		/*same thing with y if required*/
	 
		return true;
	}
    
    public float faceToYaw(BlockFace bf){
		if(bf.equals(BlockFace.NORTH))
			return 0F;
		else if(bf.equals(BlockFace.EAST))
			return 90F;
		else if(bf.equals(BlockFace.SOUTH))
			return 180F;
		else if(bf.equals(BlockFace.WEST))
			return 270F;
		return 0F;
	}
    
	public DyeColor getDyeColorFromString(String color){
		if(color.equalsIgnoreCase("red"))
			return DyeColor.RED;
		else if(color.equalsIgnoreCase("pink"))
			return DyeColor.PINK;
		else if(color.equalsIgnoreCase("blue"))
			return DyeColor.BLUE;
		else if(color.equalsIgnoreCase("lightBlue"))
			return DyeColor.LIGHT_BLUE;
		else
			return DyeColor.WHITE;
	}
	
	public String getPrimaryColor(String color){
		if(color.equals("pink"))
			return "red";
		else if(color.equals("lightblue"))
			return "blue";
		return color;
	}
	
	public String getSecondaryColor(String color){
		if(color.equals("red"))
			return "pink";
		else if(color.equals("blue"))
			return "lightblue";
		return color;
	}
}
