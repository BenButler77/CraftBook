package com.sk89q.craftbook.gates.world;

import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.sk89q.craftbook.ic.AbstractIC;
import com.sk89q.craftbook.ic.AbstractICFactory;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;
import com.sk89q.craftbook.ic.ICUtil;
import com.sk89q.craftbook.ic.RestrictedIC;
import com.sk89q.craftbook.util.LocationUtil;
import com.sk89q.craftbook.util.SignUtil;

/**
 * @author Silthus
 */
public class ItemSensor extends AbstractIC {

    private Material item;
    private short data = -1;

    private Block center;
    private Set<Chunk> chunks;
    private int radius;

    public ItemSensor(Server server, Sign block) {
	super(server, block);
	load();
    }

    private void load() {
	Sign sign = getSign();
	Block block = SignUtil.getBackBlock(sign.getBlock());
	String[] split = sign.getLine(3).trim().split(":");
	// lets get the type to detect first
	try {
	    item = Material.getMaterial(Integer.parseInt(split[0]));
	} catch (NumberFormatException e) {
	    // seems to be the name of the item
	    item = Material.getMaterial(split[0]);
	}
	if (item == null) {
	    item = Material.STONE;
	}
	if (split.length > 1) {
	    data = Short.parseShort(split[1]);
	}
	// if the line contains a = the offset is given
	// the given string should look something like that:
	// radius=x:y:z or radius, e.g. 1=-2:5:11
	radius = ICUtil.parseRadius(getSign());
	center = ICUtil.parseBlockLocation(getSign());
	chunks = LocationUtil.getSurroundingChunks(block, radius);
    }

    @Override
    public String getTitle() {
	return "Item Detection";
    }

    @Override
    public String getSignTitle() {
	return "ITEM DETECTION";
    }

    @Override
    public void trigger(ChipState chip) {
	if (chip.getInput(0)) {
	    chip.setOutput(0, isDetected());
	}
    }

    protected boolean isDetected() {
	for (Chunk chunk : chunks) {
	    if (chunk.isLoaded()) {
		// get all entites from the chunks in the defined radius
		for (Entity entity : chunk.getEntities()) {
		    if (entity instanceof Item) {
			ItemStack itemStack = ((Item) entity).getItemStack();
			if (itemStack.getType() == item) {
			    if (data != -1 && !(itemStack.getDurability() == data)) {
				return false;
			    }
			    if (LocationUtil.getGreatestDistance(entity.getLocation(), center.getLocation()) <= radius) {
				return true;
			    }
			}
		    }
		}
	    }
	}
	return false;
    }

    public static class Factory extends AbstractICFactory implements RestrictedIC {

	public Factory(Server server) {
	    super(server);
	}

	@Override
	public IC create(Sign sign) {
	    return new ItemSensor(getServer(), sign);
	}
    }
}
