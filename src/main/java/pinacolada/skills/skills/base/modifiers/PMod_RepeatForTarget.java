package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.AbstractCreature;
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
import pinacolada.skills.skills.PMathMod;

// TODO split into a new type of node along with PMod_Repeat
@VisibleSkill
public class PMod_RepeatForTarget extends PActiveMod<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PMod_RepeatForTarget.class, PField_Empty.class, 1, 1);

    public PMod_RepeatForTarget(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_RepeatForTarget() {
        super(DATA);
    }

    public PMod_RepeatForTarget(PCLCardTarget target) {
        super(DATA, target, 1);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.verbNoun(TEXT.cedit_repeat, TEXT.subjects_target);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.act_doThisFor(getTargetStringPerspective(perspective));
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
        for (AbstractCreature c : getTargetList(info)) {
            PCLUseInfo cInfo = generateInfo(c);
            childEffect.use(cInfo, order);
        }
    }

}
