package pinacolada.cards.base.fields;

import java.util.ArrayList;

public class PCLCardSaveData {
    public int form;
    public int modifiedDamage;
    public int modifiedBlock;
    public int modifiedMagicNumber;
    public int modifiedHeal;
    public int modifiedHitCount;
    public int modifiedRightCount;
    public int modifiedCost;
    public int modifiedUpgradeLevel;
    public int[] modifiedAffinities;
    public ArrayList<String> augments;
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
        this.modifiedDamage = original.modifiedDamage;
        this.modifiedBlock = original.modifiedBlock;
        this.modifiedMagicNumber = original.modifiedMagicNumber;
        this.modifiedHeal = original.modifiedHeal;
        this.modifiedHitCount = original.modifiedHitCount;
        this.modifiedRightCount = original.modifiedRightCount;
        this.modifiedCost = original.modifiedCost;
        this.modifiedUpgradeLevel = original.modifiedUpgradeLevel;
        this.modifiedAffinities = original.modifiedAffinities;
        if (original.augments != null) {
            this.augments = new ArrayList<>();
            this.augments.addAll(original.augments);
        }
        if (original.additionalData != null) {
            this.additionalData = new ArrayList<>();
            this.additionalData.addAll(original.additionalData);
        }
    }

    public void addAugment(String id) {
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
