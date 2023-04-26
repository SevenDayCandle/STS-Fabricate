package pinacolada.resources.loadout;

import extendedui.EUIUtils;

// Copied and modified from STS-AnimatorMod
public class PCLTrophies {
    public static final int MAXIMUM_TROPHY = 25;
    public int ID;
    public int highScore = 0;
    public int trophy1 = -1;
    public int trophy2 = -1;
    public int glyph0 = 0;
    public int glyph1 = 0;
    public int glyph2 = 0;

    public PCLTrophies() {
        this(-1);
    }

    public PCLTrophies(int id) {
        this.ID = id;
    }

    public PCLTrophies(String data) {
        this(-1);
        deserialize(data);
    }

    public void deserialize(String data) {
        String[] values = data.split(",");

        int id = EUIUtils.parseInt(values[0], -1);
        if (id >= 0) {
            this.ID = id;
            this.highScore = EUIUtils.parseInt(values[1], 0);
            this.trophy1 = EUIUtils.parseInt(values[2], -1);
            this.trophy2 = EUIUtils.parseInt(values[3], -1);
            this.glyph0 = EUIUtils.parseInt(values[5], 0);
            this.glyph1 = EUIUtils.parseInt(values[6], 0);
            this.glyph2 = EUIUtils.parseInt(values[7], 0);
        }
    }

    public String serialize() {
        return EUIUtils.joinStrings(",", ID, highScore, trophy1, trophy2, glyph0, glyph1, glyph2);
    }
}