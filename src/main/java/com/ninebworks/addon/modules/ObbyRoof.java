package com.ninebworks.addon.modules;

import com.ninebworks.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class ObbyRoof extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public ObbyRoof() {
        super(AddonTemplate.CATEGORY, "obby-roof", "A module that builds the obsidian roof.");
    }

    /**
     * Example event handling method.
     * Requires {@link AddonTemplate#getPackage()} to be setup correctly, otherwise the game will crash whenever the module is enabled.
     */
    @EventHandler
    public void onTickEvent(TickEvent.Pre event) {

    }
}
