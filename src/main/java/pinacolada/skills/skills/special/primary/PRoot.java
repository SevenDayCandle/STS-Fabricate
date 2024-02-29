package pinacolada.skills.skills.special.primary;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.orbs.PCLOrb;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.powers.PCLPower;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDelegateCond;
import pinacolada.skills.skills.PMultiCond;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.ui.editor.PCLCustomEffectPage;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;

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
        if ((source instanceof AbstractPower && (!(source instanceof PCLPower) || !((PCLPower) source).data.isInstant())) || (requestor instanceof PCLDynamicPowerData && !((PCLDynamicPowerData) requestor).isInstant())) {
            return TEXT.cond_when(PGR.core.tooltips.create.past());
        }
        if (source instanceof AbstractOrb || requestor instanceof PCLDynamicOrbData) {
            if (!(childEffect instanceof PDelegateCond || childEffect instanceof PMultiCond && EUIUtils.all(((PMultiCond) childEffect).getSubEffects(), c -> c instanceof PDelegateCond))) {
                String timingString = requestor instanceof PCLDynamicOrbData ? ((PCLDynamicOrbData)requestor).timing.getTitle() : source instanceof PCLOrb ? ((PCLOrb) source).timing.getTitle() : null;
                return PCLCoreStrings.colorString("y", timingString != null ? PGR.core.tooltips.trigger.title + COMMA_SEPARATOR + timingString : PGR.core.tooltips.trigger.title);
            }
        }
        return EUIUtils.EMPTY_STRING;
    }

    // This is a no-op on cards
    // For relics, this activates the effect at the start of battle
    // For powers, this activates when the power is first applied
    // For orbs, this activates when triggered
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
        return childEffect instanceof PTrait || (childEffect instanceof PMultiSkill && EUIUtils.all(((PMultiSkill) childEffect).getSubEffects(), ef -> ef instanceof PTrait));
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill, PCLCustomEffectPage editor, PCLCustomEffectNode node) {
        return skill.data.sourceTypes == null || EUIUtils.any(skill.data.sourceTypes, s -> s.isSourceAllowed(editor));
    }

    // No-Op, should not subscribe children
    @Override
    public void subscribeChildren() {
    }

    @Override
    public void triggerOnStartOfBattleForRelic() {
        if (childEffect != null) {
            childEffect.use(generateInfo(null), PCLActions.bottom);
        }
    }
}
