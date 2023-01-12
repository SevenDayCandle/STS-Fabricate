package pinacolada.skills;

import java.io.Serializable;

public class PSkillSaveData implements Serializable
{
    public String effectID;
    public String effectData;
    public String target;
    public String extraSource;
    public String valueSource;
    public String children;
    public int amount;
    public int extra;
    public int[] upgrade;
    public int[] upgradeExtra;
    public boolean useParent;

    public PSkillSaveData(PSkill effect)
    {
        this.effectID = effect.effectID;
        this.target = effect.target.name();
        this.valueSource = effect.amountSource.name();
        this.amount = effect.amount;
        this.extra = effect.extra;
        this.upgrade = effect.upgrade;
        this.upgradeExtra = effect.upgradeExtra;
        this.useParent = effect.useParent;
        this.effectData = effect.fields.serialize();

        PSkill cEffect = effect.childEffect;
        if (cEffect != null)
        {
            this.children = cEffect.serialize();
        }
    }
}
