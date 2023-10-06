package pinacolada.skills.skills.special.primary;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDelegateCond;

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
        if (source instanceof AbstractRelic || source instanceof AbstractBlight) {
            return TEXT.cond_atStartOfCombat();
        }
        if (source instanceof AbstractPower) {
            return TEXT.cond_when(PGR.core.tooltips.create.past());
        }
        return EUIUtils.EMPTY_STRING;
    }

    // This is a no-op on cards
    // For relics, this activates the effect at the start of battle
    // For powers, this activates when the power is first applied
    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        String sub = getCapitalSubText(perspective, addPeriod);
        if (!sub.isEmpty()) {
            return sub + (childEffect != null ? COLON_SEPARATOR + capital(childEffect.getText(perspective, addPeriod)) : PCLCoreStrings.period(addPeriod));
        }
        return childEffect != null ? childEffect.getText(perspective, addPeriod) : "";
    }

    @Override
    public String getTextForPreview(Object requestor) {
        if (requestor instanceof PCLDynamicPowerData) {
            return capital(TEXT.cond_when(PGR.core.tooltips.create.past()), true)
                    + (childEffect != null ? COLON_SEPARATOR + capital(childEffect.getText(PCLCardTarget.Self, true)) : PCLCoreStrings.period(true));
        }
        return super.getTextForPreview(requestor);
    }

    @Override
    public boolean isBlank() {
        return this.childEffect == null;
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return !(source instanceof AbstractRelic || source instanceof AbstractBlight || source instanceof AbstractPower) || !(skill instanceof PDelegateCond);
    }

    @Override
    public void triggerOnStartOfBattleForRelic() {
        if (childEffect != null) {
            childEffect.use(generateInfo(null), PCLActions.bottom);
        }
    }
}
