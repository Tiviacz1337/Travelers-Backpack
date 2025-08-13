package com.tiviacz.travelersbackpack.inventory.upgrades.filter;

import com.tiviacz.travelersbackpack.inventory.upgrades.Point;

import java.util.LinkedHashMap;

public class ButtonStates {

    public static final ButtonState ALLOW = new ButtonState(new LinkedHashMap<>() {{
        put("ALLOW", new Point(96, 0));
        put("BLOCK", new Point(114, 0));
        put("MATCH_CONTENTS", new Point(132, 0));
    }});
    public static final ButtonState ALLOW_FEEDING = new ButtonState(new LinkedHashMap<>() {{
        put("ALLOW", new Point(96, 0));
        put("BLOCK", new Point(114, 0));
    }});
    public static final ButtonState OBJECT_TYPE = new ButtonState(new LinkedHashMap<>() {{
        put("ITEM", new Point(96, 18));
        put("MOD", new Point(114, 18));
        put("TAG", new Point(186, 18));
    }});
    public static final ButtonState IGNORE_MODE = new ButtonState(new LinkedHashMap<>() {{
        put("MATCH_COMPONENTS", new Point(96, 36));
        put("IGNORE_COMPONENTS", new Point(114, 36));
    }});
    public static final ButtonState HUNGER_MODE = new ButtonState(new LinkedHashMap<>() {{
        put("ALWAYS_EAT", new Point(150, 0));
        put("HALF_NUTRITION", new Point(150, 18));
        put("FULL_NUTRITION", new Point(150, 36));
    }});
    public static final ButtonState IGNORE_EFFECT_MODE = new ButtonState(new LinkedHashMap<>() {{
        put("IGNORE_BAD_EFFECTS", new Point(168, 18));
        put("ALLOW_BAD_EFFECTS", new Point(168, 0));
    }});

    public static class ButtonState {
        private final LinkedHashMap<String, Point> modes;

        public ButtonState(LinkedHashMap<String, Point> modes) {
            this.modes = modes;
        }

        public String getByIndex(int index) {
            return (String)this.modes.keySet().toArray()[index];
        }

        public int getStatesCount() {
            return this.modes.size();
        }

        public Point getButtonIcon(int index) {
            return this.modes.get(getByIndex(index));
        }
    }
}