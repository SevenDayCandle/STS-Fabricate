package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PPassiveMod;

public abstract class PMod_BonusOn<T extends PField> extends PPassiveMod<T> {

    public PMod_BonusOn(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_BonusOn(PSkillData<T> data) {
        super(data);
    }

    public PMod_BonusOn(PSkillData<T> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }

    public PMod_BonusOn(PSkillData<T> data, int amount, int extra) {
        super(data, PCLCardTarget.None, amount, extra);
    }

    public String getConditionText(PCLCardTarget perspective, Object requestor) {
        return TEXT.cond_bonusIf(getAmountRawString(), getSubText(perspective, requestor));
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        return baseAmount + (meetsCondition(info, isUsing) ? amount : 0);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_bonusIf(TEXT.subjects_x, getSubText(PCLCardTarget.Self, null));
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return TEXT.cond_xConditional(childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) : "", getConditionText(perspective, requestor)) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return input >= 0 ? "+" + input : String.valueOf(input);
    }

    public abstract boolean meetsCondition(PCLUseInfo info, boolean isUsing);
}
