package com.snowgears.battleground.utilities;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
 
public class InventoryStringDeSerializer {
	
    public static String InventoryToString (ItemStack[] invInventory)
    {
        String serialization = invInventory.length + ";";
        for (int i = 0; i < invInventory.length; i++)
        {
            ItemStack is = invInventory[i];
            if (is != null)
            {
                String serializedItemStack = new String();
               
                String isType = String.valueOf(is.getType().getId());
                serializedItemStack += "t@" + isType;
               
                if (is.getDurability() != 0)
                {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }
               
                if (is.getAmount() != 1)
                {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }
               
                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0)
                {
                    for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
                    {
                        serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                    }
                }
               
                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
    }
   
    public static ItemStack[] StringToInventory (String invString)
    {
        String[] serializedBlocks = invString.split(";");
   //     String invInfo = serializedBlocks[0];
        ItemStack[] deserializedInventory = {};
       
        for (int i = 1; i < serializedBlocks.length; i++)
        {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);
           
            if (stackPosition >= deserializedInventory.length)
            {
                continue;
            }
           
            ItemStack is = null;
            Boolean createdItemStack = false;
           
            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack)
            {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t"))
                {
                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    createdItemStack = true;
                }
                else if (itemAttribute[0].equals("d") && createdItemStack)
                {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("a") && createdItemStack)
                {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("e") && createdItemStack)
                {
                    is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                }
            }
            deserializedInventory[stackPosition] = is;
        }
       
        return deserializedInventory;
    }
}
