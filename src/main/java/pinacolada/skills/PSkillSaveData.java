package pinacolada.skills;

import extendedui.EUIUtils;

import java.io.Serializable;

public class PSkillSaveData implements Serializable {
    static final long serialVersionUID = 1L;
    public String effectID;
    public String effectData;
    public String target;
    public String children;
    public String special; // Used for multibase skills
    public int amount;
    public int extra;
    public int extra2;
    public int scope;
    public int[] upgrade;
    public int[] upgradeExtra;
    public int[] upgradeScope;
    public boolean useParent;

    public PSkillSaveData(PSkill<?> effect) {
        this.effectID = effect.effectID;
        this.target = effect.target.name();
        this.amount = effect.amount;
        this.extra = effect.extra;
        this.extra2 = effect.extra2;
        this.scope = effect.scope;
        this.upgrade = effect.upgrade;
        this.upgradeExtra = effect.upgradeExtra;
        this.upgradeScope = effect.upgradeScope;
        this.useParent = effect.useParent;
        this.effectData = EUIUtils.serialize(effect.fields);

        PSkill<?> cEffect = effect.childEffect;
        if (cEffect != null) {
            this.children = cEffect.serialize();
        }

        this.special = effect.getSpecialData();
    }
}
