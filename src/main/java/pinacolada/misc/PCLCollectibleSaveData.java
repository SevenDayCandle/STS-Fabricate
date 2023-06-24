package pinacolada.misc;

import java.util.ArrayList;

public class PCLCollectibleSaveData {
    public int form;
    public int timesUpgraded;
    public ArrayList<String> additionalData;

    public PCLCollectibleSaveData() {
        this(0, 0);
    }

    public PCLCollectibleSaveData(int form, int timesUpgraded) {
        this.form = form;
        this.timesUpgraded = timesUpgraded;
    }

    public PCLCollectibleSaveData(int form, int timesUpgraded, ArrayList<String> additionalData) {
        this.form = form;
        this.timesUpgraded = timesUpgraded;
        if (additionalData != null) {
            this.additionalData = new ArrayList<>();
            this.additionalData.addAll(additionalData);
        }
    }

    public PCLCollectibleSaveData(PCLCollectibleSaveData original) {
        this.form = original.form;
        this.timesUpgraded = original.timesUpgraded;
        if (original.additionalData != null) {
            this.additionalData = new ArrayList<>();
            this.additionalData.addAll(original.additionalData);
        }
    }
}
