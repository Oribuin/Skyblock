package xyz.oribuin.skyblock.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import xyz.oribuin.skyblock.SkyblockPlugin;

public class ConfigurationManager extends AbstractConfigurationManager {

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[]{};
    }

    public enum Setting implements RoseSetting {
        WORLDNAMES_NORMAL("world-names.NORMAL", "islands_normal", "The name for the main island world"),
        WORLDNAMES_NETHER("world-names.NETHER", "islands_nether", "The name for the nether island world"),
        WORLDNAMES_END("world-names.THE_END", "islands_end", "The name for the end island world"),

        // Island Settings
        ISLAND_SIZE("island-size", 150, "The size of the island"),
        MAX_MEMBERS("max-members", 5, "The maximum amount of members per island"),
        ;

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return SkyblockPlugin.get().getManager(ConfigurationManager.class).getConfig();
        }
    }

}