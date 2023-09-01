package pinacolada.skills.skills.special.primary;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDelegateCond;

import static pinacolada.utilities.GameUtilities.EMPTY_STRING;

// Placeholder class used to ensure that the root of the effect editor is always a primary
@VisibleSkill
public class PRoot extends PPrimary<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PRoot.class, PField_Empty.class, 1, 1)
            .noTarget();

    public PRoot(PSkillSaveData content) {
        super(DATA, content);
    }

    public PRoot() {
        super(DATA);
    }

    @Override
    public String getSampleText(PSkill<?> caller, PSkill<?> parentSkill) {
        return EUIRM.strings.ui_na;
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return source instanceof AbstractRelic ? TEXT.cond_atStartOfCombat() : EMPTY_STRING;
    }

    // This is a no-op on cards
    // For relics, this activates the effect at the start of battle
    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        if (source instanceof AbstractRelic) {
            return getCapitalSubText(perspective, addPeriod) + (childEffect != null ? COLON_SEPARATOR + StringUtils.capitalize(childEffect.getText(perspective, addPeriod)) : PCLCoreStrings.period(addPeriod));
        }
        return childEffect != null ? childEffect.getText(perspective, addPeriod) : "";
    }

    @Override
    public boolean isBlank() {
        return this.childEffect == null;
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return !(source instanceof AbstractRelic) || !(skill instanceof PDelegateCond);
    }

    @Override
    public void triggerOnStartOfBattleForRelic() {
        if (childEffect != null) {
            childEffect.use(generateInfo(null), PCLActions.bottom);
        }
    }
}
