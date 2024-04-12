package xyz.oribuin.skyblock.gui;

import xyz.oribuin.skyblock.SkyblockPlugin;
import xyz.oribuin.skyblock.gui.impl.BiomeGUI;
import xyz.oribuin.skyblock.gui.impl.BorderGUI;
import xyz.oribuin.skyblock.gui.impl.CreateGUI;
import xyz.oribuin.skyblock.gui.impl.PanelGUI;
import xyz.oribuin.skyblock.gui.impl.SettingsGUI;
import xyz.oribuin.skyblock.gui.impl.warp.WarpCategoryGUI;
import xyz.oribuin.skyblock.gui.impl.warp.WarpSettingsGUI;
import xyz.oribuin.skyblock.gui.impl.warp.WarpsGUI;

import java.util.HashMap;
import java.util.Map;

public final class MenuProvider {

    private final static Map<Class<? extends PluginMenu>, PluginMenu> menuCache = new HashMap<>();

    static {
        menuCache.put(BiomeGUI.class, new BiomeGUI(SkyblockPlugin.get()));
        menuCache.put(BorderGUI.class, new BorderGUI(SkyblockPlugin.get()));
        menuCache.put(CreateGUI.class, new CreateGUI(SkyblockPlugin.get()));
        menuCache.put(PanelGUI.class, new PanelGUI(SkyblockPlugin.get()));
//        menuCache.put(SettingsGUI.class, new SettingsGUI(SkyblockPlugin.get()));

        // Warp GUIs
//        menuCache.put(WarpCategoryGUI.class, new WarpCategoryGUI(SkyblockPlugin.get()));
//        menuCache.put(WarpSettingsGUI.class, new WarpSettingsGUI(SkyblockPlugin.get()));
//        menuCache.put(WarpsGUI.class, new WarpsGUI(SkyblockPlugin.get()));

        menuCache.forEach((aClass, pluginMenu) -> pluginMenu.load());
    }

    public static void reload() {
        menuCache.forEach((aClass, pluginMenu) -> pluginMenu.load());
    }

    /**
     * Get the instance of the menu.
     *
     * @param <T> the type of the menu.
     * @return the instance of the menu.
     */
    @SuppressWarnings("unchecked")
    public static <T extends PluginMenu> T get(Class<T> menuClass) {
        if (menuCache.containsKey(menuClass)) {
            return (T) menuCache.get(menuClass);
        }

        try {
            T menu = menuClass.getDeclaredConstructor().newInstance();
            menu.load();
            menuCache.put(menuClass, menu);
            return menu;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + menuClass.getName(), e);
        }
    }

}