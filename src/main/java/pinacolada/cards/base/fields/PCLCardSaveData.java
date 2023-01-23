package pinacolada.cards.base.fields;

import pinacolada.cards.base.tags.PCLCardTag;

import java.util.ArrayList;
import java.util.HashSet;

public class PCLCardSaveData
{
    public int form;
    public int modifiedDamage;
    public int modifiedBlock;
    public int modifiedMagicNumber;
    public int modifiedHeal;
    public int modifiedHitCount;
    public int modifiedRightCount;
    public int modifiedCost;
    public int[] modifiedAffinities;
    public int[] modifiedScaling;
    public HashSet<PCLCardTag> addedTags = new HashSet<>();
    public HashSet<PCLCardTag> removedTags = new HashSet<>();
    public ArrayList<String> augments = new ArrayList<>();
    public ArrayList<String> additionalData = new ArrayList<>();

    public PCLCardSaveData()
    {
        this.form = 0;
    }

    public PCLCardSaveData(int form)
    {
        this.form = form;
    }

    public PCLCardSaveData(int form, ArrayList<String> additionalData)
    {
        this.form = form;
        if (additionalData != null)
        {
            this.additionalData = new ArrayList<>();
            this.additionalData.addAll(additionalData);
        }
    }

    public PCLCardSaveData(PCLCardSaveData original)
    {
        this.form = original.form;
        this.modifiedDamage = original.modifiedDamage;
        this.modifiedBlock = original.modifiedBlock;
        this.modifiedMagicNumber = original.modifiedMagicNumber;
        this.modifiedHeal = original.modifiedHeal;
        this.modifiedHitCount = original.modifiedHitCount;
        this.modifiedRightCount = original.modifiedRightCount;
        this.modifiedCost = original.modifiedCost;
        this.modifiedAffinities = original.modifiedAffinities;
        this.modifiedScaling = original.modifiedScaling;
        if (original.addedTags != null)
        {
            this.addedTags = new HashSet<>();
            this.addedTags.addAll(original.addedTags);
        }
        if (original.removedTags != null)
        {
            this.removedTags = new HashSet<>();
            this.removedTags.addAll(original.removedTags);
        }
        if (original.augments != null)
        {
            this.augments = new ArrayList<>();
            this.augments.addAll(original.augments);
        }
        if (original.additionalData != null)
        {
            this.additionalData = new ArrayList<>();
            this.additionalData.addAll(original.additionalData);
        }
    }
}
