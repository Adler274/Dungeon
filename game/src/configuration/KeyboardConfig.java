package configuration;

import com.badlogic.gdx.Input;
import configuration.values.ConfigIntValue;

@ConfigMap(path = {"keyboard"})
public class KeyboardConfig {

    public static final ConfigKey<Integer> MOVEMENT_UP =
            new ConfigKey<>(new String[] {"movement", "up"}, new ConfigIntValue(Input.Keys.W));
    public static final ConfigKey<Integer> MOVEMENT_DOWN =
            new ConfigKey<>(new String[] {"movement", "down"}, new ConfigIntValue(Input.Keys.S));
    public static final ConfigKey<Integer> MOVEMENT_LEFT =
            new ConfigKey<>(new String[] {"movement", "left"}, new ConfigIntValue(Input.Keys.A));
    public static final ConfigKey<Integer> MOVEMENT_RIGHT =
            new ConfigKey<>(new String[] {"movement", "right"}, new ConfigIntValue(Input.Keys.D));
    public static final ConfigKey<Integer> INTERACT_WORLD =
            new ConfigKey<>(new String[] {"interact", "world"}, new ConfigIntValue(Input.Keys.E));
    public static final ConfigKey<Integer> SHOW_INVENTORY =
            new ConfigKey<>(new String[] {"inventory", "show"}, new ConfigIntValue(Input.Keys.I));
    public static final ConfigKey<Integer> CLOSE_BAG_INVENTORY =
            new ConfigKey<>(
                    new String[] {"inventory", "close_bag"},
                    new ConfigIntValue(Input.Keys.NUMPAD_0));
    public static final ConfigKey<Integer> USE_ITEM_ONE =
            new ConfigKey<>(
                    new String[] {"use_item", "one"}, new ConfigIntValue(Input.Keys.NUMPAD_1));
    public static final ConfigKey<Integer> USE_ITEM_TWO =
            new ConfigKey<>(
                    new String[] {"use_item", "two"}, new ConfigIntValue(Input.Keys.NUMPAD_2));
    public static final ConfigKey<Integer> USE_ITEM_THREE =
            new ConfigKey<>(
                    new String[] {"use_item", "three"}, new ConfigIntValue(Input.Keys.NUMPAD_3));
    public static final ConfigKey<Integer> MELEE_SKILL =
            new ConfigKey<>(new String[] {"skill", "melee"}, new ConfigIntValue(Input.Keys.Q));
    public static final ConfigKey<Integer> FIRST_SKILL =
            new ConfigKey<>(new String[] {"skill", "first"}, new ConfigIntValue(Input.Keys.NUM_1));
    public static final ConfigKey<Integer> SECOND_SKILL =
            new ConfigKey<>(new String[] {"skill", "second"}, new ConfigIntValue(Input.Keys.NUM_2));
    public static final ConfigKey<Integer> THIRD_SKILL =
            new ConfigKey<>(new String[] {"skill", "third"}, new ConfigIntValue(Input.Keys.NUM_3));
    public static final ConfigKey<Integer> FOURTH_SKILL =
            new ConfigKey<>(new String[] {"skill", "fourth"}, new ConfigIntValue(Input.Keys.NUM_4));
}
