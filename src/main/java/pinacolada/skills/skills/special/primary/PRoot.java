package pinacolada.skills.skills.special.primary;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Empty;

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
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (source instanceof AbstractRelic || source instanceof AbstractBlight) {
            return TEXT.cond_atStartOfCombat();
        }
        if (source instanceof AbstractPower || requestor instanceof PCLDynamicPowerData) {
            return TEXT.cond_when(PGR.core.tooltips.create.past());
        }
        return EUIUtils.EMPTY_STRING;
    }

    // This is a no-op on cards
    // For relics, this activates the effect at the start of battle
    // For powers, this activates when the power is first applied
    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String sub = getCapitalSubText(perspective, requestor, addPeriod);
        if (!sub.isEmpty()) {
            return sub + (childEffect != null ? COLON_SEPARATOR + capital(childEffect.getText(perspective, requestor, addPeriod)) : PCLCoreStrings.period(addPeriod));
        }
        return childEffect != null ? childEffect.getText(perspective, requestor, addPeriod) : "";
    }

    @Override
    public boolean isBlank() {
        return this.childEffect == null;
    }

    @Override
    public boolean isPassiveOnly() {
        return childEffect instanceof PTrait || (childEffect instanceof PMultiBase<?> && EUIUtils.all(((PMultiBase<?>) childEffect).getSubEffects(), ef -> ef instanceof PTrait));
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return skill.data.sourceTypes == null || EUIUtils.any(skill.data.sourceTypes, s -> s.isSourceAllowed(this));
    }

    @Override
    public void triggerOnStartOfBattleForRelic() {
        if (childEffect != null) {
            childEffect.use(generateInfo(null), PCLActions.bottom);
        }
    }
}
