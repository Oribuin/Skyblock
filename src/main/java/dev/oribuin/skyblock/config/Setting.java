package dev.oribuin.skyblock.config;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import dev.oribuin.skyblock.SkyblockPlugin;

import java.util.ArrayList;
import java.util.List;

import static dev.rosewood.rosegarden.config.RoseSettingSerializers.INTEGER;

public class Setting {

    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();

    public static RoseSetting<Integer> ISLAND_SIZE = create("island-size", INTEGER, 150, "The default size of the island");
    public static RoseSetting<Integer> MAX_MEMBERS = create("max-members", INTEGER, 5, "The maximum amount of members per island");


    private static <T> RoseSetting<T> create(String key, RoseSettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.backed(SkyblockPlugin.get(), key, serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<CommentedConfigurationSection> create(String key, String... comments) {
        RoseSetting<CommentedConfigurationSection> setting = RoseSetting.backedSection(SkyblockPlugin.get(), key, comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return KEYS;
    }

}
