package pinacolada.skills.skills;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_CardCategory;

public class PSpecialSkill extends PSkill<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PSpecialSkill.class, PField_CardCategory.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget();
    private final String description;
    private final ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse;

    public PSpecialSkill(String effectID, String description, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse) {
        this(effectID, description, onUse, 1, 0);
    }

    public PSpecialSkill(String effectID, String description, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = effectID;
        this.description = description;
        this.onUse = onUse;
    }

    public PSpecialSkill(String effectID, String description, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        this(effectID, description, onUse, amount, 0);
    }

    public PSpecialSkill(String effectID, FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse) {
        this(effectID, strFunc, onUse, 1, 0);
    }

    public PSpecialSkill(String effectID, FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount, int extra) {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = effectID;
        this.description = strFunc.invoke(this);
        this.onUse = onUse;
    }

    public PSpecialSkill(String effectID, FuncT1<String, PSpecialSkill> strFunc, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int amount) {
        this(effectID, strFunc, onUse, amount, 0);
    }

    @Override
    public String getSubText() {
        return description;
    }

    @Override
    public String getText(boolean addPeriod) {
        return EUIUtils.format(getSubText(), getAmountRawString(), getExtraRawString()) + PCLCoreStrings.period(addPeriod);
    }

    // Assume that special card skill powers are always beneficial to the player (i.e. they are debuffs on enemies and buffs on you/allies)
    @Override
    public boolean isDetrimental() {
        return getOwnerCreature() instanceof AbstractMonster && !(getOwnerCreature() instanceof PCLCardAlly);
    }

    @Override
    public PSpecialSkill makeCopy() {
        return new PSpecialSkill(effectID, description, onUse, amount, extra);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        onUse.invoke(this, info, order);
    }
}
