package pinacolada.skills.skills.base.modifiers;

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
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(TEXT.cedit_repeat, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.act_doThis(getAmountRawString());
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return (childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) + EFFECT_SEPARATOR + capital(getSubText(perspective, requestor), true) : capital(getSubText(perspective, requestor), addPeriod)) + PCLCoreStrings.period(addPeriod);
    }

    @Override
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
        for (int i = 0; i < refreshAmount(info); i++) {
            childEffect.use(info, order);
        }
    }

}
