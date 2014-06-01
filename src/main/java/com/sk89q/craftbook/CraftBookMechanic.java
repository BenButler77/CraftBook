package com.sk89q.craftbook;

import org.bukkit.event.Listener;

import com.sk89q.util.yaml.YAMLProcessor;

/**
 * Represents a CraftBook Mechanic.
 */
public interface CraftBookMechanic extends Listener {

    /**
     * Called when a mechanic should be initialized. This includes creating of any maps, lists or singleton instances.
     * 
     * @return if it enabled properly. Note: returning false will cause the mechanic to be disabled.
     */
    public boolean enable();

    /**
     * Called when the mechanic should be disabled. This should make sure all memory is released.
     */
    public void disable();

    /**
     * Loads the configuration for this mechanic.
     * 
     * @param config The YAMLProcessor for this config.
     * @param path The path of the parent element.
     */
    public void loadConfiguration(YAMLProcessor config, String path);
}