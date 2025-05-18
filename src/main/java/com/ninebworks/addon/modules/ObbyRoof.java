package com.ninebworks.addon.modules;

import baritone.api.BaritoneAPI;
import baritone.api.schematic.CompositeSchematic;
import baritone.api.schematic.FillSchematic;
import baritone.api.schematic.ISchematic;
import com.ninebworks.addon.NinebworksAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.EChestFarmer;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;

public class ObbyRoof extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final HashSet<ChunkPos> completedChunks = new HashSet<>();

    private final Setting<Integer> minObby = sgGeneral.add(new IntSetting.Builder()
        .name("min obby")
        .description("The minimum amount of obsidian before running echest farmer.")
        .defaultValue(32)
        .range(0, 1024)
        .sliderRange(0, 1024)
        .build()
    );

    private final Setting<Integer> maxObby = sgGeneral.add(new IntSetting.Builder()
        .name("max obby")
        .description("The maximum amount of obsidian before stopping echest farmer.")
        .defaultValue(256)
        .range(0, 1024)
        .sliderRange(0, 1024)
        .build()
    );

    private final Setting<Integer> roofYLevel = sgGeneral.add(new IntSetting.Builder()
        .name("roof y level")
        .description("The y level of the obsidian roof")
        .defaultValue(119)
        .range(0, 128)
        .sliderRange(0, 128)
        .build());

    private final Setting<Integer> roofAirHeight = sgGeneral.add(new IntSetting.Builder()
        .name("roof air height")
        .description("The height of the air pocket above the roof.")
        .defaultValue(3)
        .min(1)
        .sliderRange(1, 8)
        .build());

    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public ObbyRoof() {
        super(NinebworksAddon.CATEGORY, "obby-roof", "A module that builds the obsidian roof.");
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();
        WButton clear = list.add(theme.button("Clear completed chunks")).widget();
        clear.action = completedChunks::clear;
        return list;
    }

    /**
     * Example event handling method.
     * Requires {@link NinebworksAddon#getPackage()} to be setup correctly, otherwise the game will crash whenever the module is enabled.
     */
    @EventHandler
    public void onTickEvent(TickEvent.Pre event) {
        if (isFarmingObby()) {
            if (numObby() >= maxObby.get()) {
                Modules.get().get(EChestFarmer.class).toggle();
                BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().resume();
            } else {
                setPressed(mc.options.forwardKey, true);
            }
        } else {
            if (numObby() <= minObby.get()) {
                Modules.get().get(EChestFarmer.class).toggle();
                BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().pause();
            } else {
                setPressed(mc.options.forwardKey, false);
            }
        }

        if (isFarmingObby()) return;

        if (!isBuilding()) {
            beginBuilding();
        }
    }

    @Override
    public void onDeactivate() {
        setPressed(mc.options.forwardKey, false);
    }

    private void beginBuilding() {
        ChunkPos pos = findNextChunk();
        BlockPos min = pos.getBlockPos(0, roofYLevel.get(), 0);

        CompositeSchematic schematic = new CompositeSchematic(0, 0, 0);

        ISchematic obsidian = new FillSchematic(16, 1, 16, Blocks.OBSIDIAN.getDefaultState());
        ISchematic air = new FillSchematic(16, roofAirHeight.get(), 16, Blocks.AIR.getDefaultState());

        schematic.put(obsidian, 0, 0, 0);
        schematic.put(air, 0, 1, 0);

        BaritoneAPI.getProvider().getPrimaryBaritone().getBuilderProcess().build("Obby roof", schematic, min);
        completedChunks.add(pos);
    }

    private ChunkPos findNextChunk(int searchRadius) {
        assert this.mc.player != null;
        ChunkPos center = this.mc.player.getChunkPos();
        return ChunkPos.stream(center, searchRadius)
            .filter(this::isAvailable)
            .findFirst()
            .orElseGet(() -> findNextChunk(searchRadius + 1));
    }

    private boolean isAvailable(ChunkPos pos) {
        return !completedChunks.contains(pos);
    }

    private ChunkPos findNextChunk() {
        return findNextChunk(0);
    }


    private int numObby() {
        assert this.mc.player != null;
        return this.mc.player.getInventory().count(Items.OBSIDIAN);
    }

    private boolean isBuilding() {
        return BaritoneAPI
            .getProvider()
            .getPrimaryBaritone()
            .getBuilderProcess()
            .isActive();
    }

    private boolean isFarmingObby() {
        return Modules.get().isActive(EChestFarmer.class);
    }

    private void setPressed(KeyBinding key, boolean pressed) {
        key.setPressed(pressed);
        Input.setKeyState(key, pressed);
    }
}
