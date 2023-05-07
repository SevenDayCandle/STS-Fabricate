package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
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
            .selfTarget();

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
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.cond_ifX(PGR.core.tooltips.counter.title);
    }

    @Override
    public String getSubText() {
        if (isWhenClause() || isPassiveClause()) {
            return EUIRM.strings.adjNoun(fields.getThresholdString(plural(PGR.core.tooltips.counter)), TEXT.subjects_card);
        }
        return TEXT.cond_ifTargetHas(TEXT.subjects_thisCard, 1, fields.getThresholdString(plural(PGR.core.tooltips.counter)));
    }

    @Override
    public int getXValue(AbstractCard card) {
        return GameUtilities.getCounter(sourceCard);
    }

    @Override
    public String getText(boolean addPeriod) {
        String base = super.getText(addPeriod);
        if (!isWhenClause() && !isPassiveClause()) {
            return base + getXRawString();
        }
        return base;
    }
}
