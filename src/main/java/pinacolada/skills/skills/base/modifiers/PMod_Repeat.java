package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActiveMod;

@VisibleSkill
public class PMod_Repeat extends PActiveMod<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PMod_Repeat.class, PField_Empty.class).noTarget();

    public PMod_Repeat(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_Repeat() {
        super(DATA);
    }

    public PMod_Repeat(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info, boolean isUsing) {
        return amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(TEXT.cedit_repeat, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.act_doThis(getAmountRawString());
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        return (childEffect != null ? capital(childEffect.getText(perspective, false), addPeriod) + EFFECT_SEPARATOR + capital(getSubText(perspective), true) : capital(getSubText(perspective), addPeriod)) + PCLCoreStrings.period(addPeriod);
    }

    // Does not update child amounts
    @Override
    public void onDrag(AbstractMonster m) {
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet, boolean isUsing) {
    }

    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (shouldPay && childEffect != null) {
            useImpl(info, order);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect != null) {
            useImpl(info, order);
        }
    }

    protected void useImpl(PCLUseInfo info, PCLActions order) {
        for (int i = 0; i < getModifiedAmount(this.childEffect, info, true); i++) {
            childEffect.use(info, order);
        }
    }

}
