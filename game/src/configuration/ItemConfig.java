package configuration;

import configuration.values.ConfigEnumValue;
import configuration.values.ConfigIntValue;
import configuration.values.ConfigStringValue;
import ecs.items.ItemType;

/** The default ItemData values */
@ConfigMap(path = {"item"})
public class ItemConfig {
    /** The Description of the Default ItemData */
    public static final ConfigKey<String> DESCRIPTION =
            new ConfigKey<>(new String[] {"description"}, new ConfigStringValue("Default Item"));

    /** The Price of the Default ItemData */
    public static final ConfigKey<Integer> PRICE =
            new ConfigKey<>(new String[] {"price"}, new ConfigIntValue(10));

    /** The Name of the Default ItemData */
    public static final ConfigKey<String> NAME =
            new ConfigKey<>(new String[] {"name"}, new ConfigStringValue("Defaultname"));

    /** The Type of the Default ItemData */
    public static final ConfigKey<ItemType> TYPE =
            new ConfigKey<>(new String[] {"type"}, new ConfigEnumValue<>(ItemType.DEBUG));

    /** The texturepath of the Default ItemData will be used for world and Inventory */
    public static final ConfigKey<String> TEXTURE =
            new ConfigKey<>(
                    new String[] {"texture"},
                    new ConfigStringValue("animation/missingTexture.png"));

    public static final ConfigKey<String> PATTERN =
            new ConfigKey<>(new String[] {"pattern"}, new ConfigStringValue("Defaultpattern"));
}
