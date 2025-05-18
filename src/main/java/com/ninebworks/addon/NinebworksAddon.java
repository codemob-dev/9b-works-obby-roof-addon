package com.ninebworks.addon;

import com.ninebworks.addon.commands.CommandExample;
import com.ninebworks.addon.hud.HudExample;
import com.ninebworks.addon.modules.ObbyRoof;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class NinebworksAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("9bworks");
    public static final HudGroup HUD_GROUP = new HudGroup("9bworks");

    @Override
    public void onInitialize() {
        LOG.info("Initializing 9bworks");

        // Modules
        Modules.get().add(new ObbyRoof());

        // Commands
        Commands.add(new CommandExample());

        // HUD
        Hud.get().register(HudExample.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.ninebworks.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("codemob-dev", "9b-works-obby-roof-addon");
    }
}
