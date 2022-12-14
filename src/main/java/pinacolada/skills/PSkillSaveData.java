package pinacolada.skills;

public class PSkillSaveData
{
    public String effectID;
    public String effectData;
    public String effectData2;
    public String effectData3;
    public String groupTypes;
    public String target;
    public String valueSource;
    public String children;
    public int amount;
    public int extra;
    public int[] upgrade;
    public int[] upgradeExtra;
    public boolean alt;
    public boolean alt2;
    public boolean useParent;

    public PSkillSaveData(PSkill effect)
    {
        this.effectID = effect.effectID;
        this.groupTypes = PSkill.joinData(effect.groupTypes, c -> c != null ? c.pile.name() : null);
        this.target = effect.target.name();
        this.valueSource = effect.amountSource.name();
        this.amount = effect.amount;
        this.extra = effect.extra;
        this.upgrade = effect.upgrade;
        this.upgradeExtra = effect.upgradeExtra;
        this.alt = effect.alt;
        this.alt2 = effect.alt2;
        this.useParent = effect.useParent;
        makeEffectData(effect);
    }

    protected final void makeEffectData(PSkill effect)
    {

        switch (PSkill.getEffectType(effect))
        {

            case CardGroupFull:
                effectData2 = PSkill.joinData(effect.rarities, Enum::name);
                effectData3 = PSkill.joinData(effect.types, Enum::name);
            case Affinity:
            case CardGroupAffinity:
                effectData = PSkill.joinData(effect.affinities, Enum::name);
                break;
            case Card:
                effectData = PSkill.joinData(effect.cardIDs, card -> card);
                break;
            case Orb:
                effectData = PSkill.joinData(effect.orbs, orb -> orb.ID);
                break;
            case Power:
                effectData = PSkill.joinData(effect.powers, power -> power.ID);
                break;
            case Stance:
                effectData = PSkill.joinData(effect.stances, stance -> stance.ID);
                break;
            case Tag:
                effectData = PSkill.joinData(effect.tags, Enum::name);
                break;
            case CardGroupCardType:
                effectData = PSkill.joinData(effect.types, Enum::name);
        }

        PSkill cEffect = effect.childEffect;
        if (cEffect != null)
        {
            this.children = cEffect.serialize();
        }

        effect.addAdditionalData(this);
    }
}
