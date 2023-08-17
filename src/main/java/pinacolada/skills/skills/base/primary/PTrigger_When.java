package pinacolada.skills.skills.base.primary;

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

@VisibleSkill
public class PTrigger_When extends PTrigger {
    public static final PSkillData<PField_CardGeneric> DATA = register(PTrigger_When.class, PField_CardGeneric.class, -1, DEFAULT_MAX)
            .selfTarget();

    public PTrigger_When() {
        super(DATA);
    }

    public PTrigger_When(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return capital(TEXT.cond_when(TEXT.subjects_x), true);
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        String subText = getCapitalSubText(perspective, addPeriod);
        String childText;

        // Every x times
        if (fields.forced && childEffect != null) {
            childText = (fields.not ? TEXT.cond_everyXTimesY(getAmountRawString(), childEffect.getText(perspective, false) ) : TEXT.cond_everyXTimesYThisTurn(getAmountRawString(), childEffect.getText(perspective, false) ))
                    + PCLCoreStrings.period(addPeriod);
        }
        else {
            childText = childEffect != null ?
                    (childEffect.shouldUseWhenText() ? TEXT.cond_when(childEffect.getText(perspective, false)) : childEffect.getText(perspective, false)) + PCLCoreStrings.period(addPeriod)
                    : PCLCoreStrings.period(addPeriod);
        }

        return subText.isEmpty() ? childText : subText + COLON_SEPARATOR + StringUtils.capitalize(childText);
    }

    // Restrict conditions that do not subscribe to anything and are not under a condition that does so
    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return !(skill instanceof PCond) || skill instanceof PMultiBase || skill instanceof PCLCombatSubscriber || skill.parent instanceof PCond;
    }
}
