package pinacolada.cards.base.fields;

import pinacolada.augments.PCLAugment;

import java.util.ArrayList;

public class PCLCardSaveData {
    public int form;
    public ArrayList<PCLAugment.SaveData> augments;
    public ArrayList<String> additionalData;

    public PCLCardSaveData() {
        this(0);
    }

    public PCLCardSaveData(int form) {
        this.form = form;
    }

    public PCLCardSaveData(int form, ArrayList<String> additionalData) {
        this.form = form;
        if (additionalData != null) {
            this.additionalData = new ArrayList<>();
            this.additionalData.addAll(additionalData);
        }
    }

    public PCLCardSaveData(PCLCardSaveData original) {
        this.form = original.form;
        if (original.augments != null) {
            this.augments = new ArrayList<>();
            this.augments.addAll(original.augments);
        }
        if (original.additionalData != null) {
            this.additionalData = new ArrayList<>();
            this.additionalData.addAll(original.additionalData);
        }
    }

    public void addAugment(PCLAugment.SaveData id) {
        if (augments == null) {
            augments = new ArrayList<>();
        }
        augments.add(id);
    }

    public void removeAugmentAt(int index) {
        if (augments != null) {
            augments.set(index, null);
        }
    }
}
