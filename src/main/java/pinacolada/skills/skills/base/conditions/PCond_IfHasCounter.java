package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_IfHasCounter extends PFacetCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_IfHasCounter.class, PField_Not.class)
            .noTarget();

    public PCond_IfHasCounter(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_IfHasCounter() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_IfHasCounter(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return info != null &&
                (fields.doesValueMatchThreshold(GameUtilities.getCounter(info.card)));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(PGR.core.tooltips.counter.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isWhenClause() || isPassiveClause()) {
            return EUIRM.strings.adjNoun(fields.getThresholdRawString(plural(PGR.core.tooltips.counter)), TEXT.subjects_card);
        }
        return TEXT.cond_ifTargetHas(TEXT.subjects_thisCard, 1, fields.getThresholdRawString(plural(PGR.core.tooltips.counter))) + getXRawString();
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        String base = super.getText(perspective, addPeriod);
        if (!isWhenClause() && !isPassiveClause()) {
            return base + getXRawString();
        }
        return base;
    }

    @Override
    public int getXValue() {
        return GameUtilities.getCounter(sourceCard);
    }
}
