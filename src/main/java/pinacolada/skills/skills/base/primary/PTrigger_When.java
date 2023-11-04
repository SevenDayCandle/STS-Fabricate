package pinacolada.skills.skills.base.primary;

import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PTrigger;

import java.util.Collections;

@VisibleSkill
public class PTrigger_When extends PTrigger {
    public static final PSkillData<PField_CardGeneric> DATA = register(PTrigger_When.class, PField_CardGeneric.class, -1, DEFAULT_MAX)
            .noTarget();

    public PTrigger_When() {
        super(DATA);
    }

    public PTrigger_When(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return StringUtils.capitalize(TEXT.cond_when(TEXT.subjects_x));
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String subText = getCapitalSubText(perspective, requestor, addPeriod);
        String childText;

        // Every x times
        if (fields.forced && childEffect != null) {
            childText = (fields.not ? TEXT.cond_everyXTimesY(getAmountRawString(), childEffect.getText(perspective, requestor, false)) : TEXT.cond_everyXTimesYThisTurn(getAmountRawString(), childEffect.getText(perspective, requestor, false)))
                    + PCLCoreStrings.period(addPeriod);
        }
        else {
            childText = childEffect != null ?
                    (childEffect.shouldUseWhenText() ? TEXT.cond_when(childEffect.getText(perspective, requestor, false)) : childEffect.getText(perspective, requestor, false)) + PCLCoreStrings.period(addPeriod)
                    : PCLCoreStrings.period(addPeriod);
        }

        return subText.isEmpty() ? childText : subText + COLON_SEPARATOR + StringUtils.capitalize(childText);
    }

    // Restrict conditions that do not subscribe to anything and are not under a condition that does so
    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return !(skill instanceof PCond)
                || skill instanceof PMultiBase
                || skill instanceof PCLCombatSubscriber
                || (skill.parent != null && skill.parent.hasParentType(PCond.class));
    }

    @Override
    public PTrigger_When scanForTips(String source) {
        if (tips == null) {
            tips = Collections.singletonList(new EUIKeywordTooltip(StringUtils.capitalize(TEXT.cond_when(TEXT.subjects_x)), TEXT.cetut_when));
        }
        return this;
    }
}
