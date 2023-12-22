package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.powers.PTriggerPower;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Numeric;
import pinacolada.skills.skills.PTrigger;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMove_StackCustomPower extends PMove<PField_Numeric> implements SummonOnlyMove {

    public static final PSkillData<PField_Numeric> DATA = register(PMove_StackCustomPower.class, PField_Numeric.class, -1, DEFAULT_MAX);

    public PMove_StackCustomPower() {
        this(PCLCardTarget.Self, 0);
    }

    public PMove_StackCustomPower(PCLCardTarget target, int amount, Integer... indexes) {
        super(DATA, target, amount);
        fields.setIndexes(Arrays.asList(indexes));
    }

    public PMove_StackCustomPower(PSkillSaveData content) {
        super(DATA, content);
    }

    // Whether this skill is under a PTrigger and this skill references that same PTrigger
    protected boolean doesPowerReferenceSelf(PSkill<?> poEff) {
        return getHighestParent().hasSameUUID(poEff);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_applyAmountX(TEXT.subjects_x, TEXT.cedit_customPower);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        // If this skill is under a PTrigger and this references that same PTrigger, this will cause an infinite loop
        // In this instance, we should describe the power itself instead
        ArrayList<PSkill<?>> effectsForPower = new ArrayList<>();
        PSkill<?> highestParent = getHighestParent();
        boolean referencesSelf = false;
        List<PSkill<?>> powerEffects = source != null ? source.getPowerEffects() : requestor instanceof EditorMaker ? ((EditorMaker<?, ?>) requestor).getPowers() : null;
        if (powerEffects != null) {
            for (Integer i : fields.indexes) {
                if (i >= 0 && powerEffects.size() > i) {
                    PSkill<?> poEff = powerEffects.get(i);
                    referencesSelf = doesPowerReferenceSelf(poEff);
                    if (referencesSelf) {
                        break;
                    }
                    else {
                        effectsForPower.add(poEff);
                    }
                }
            }
        }

        if (referencesSelf) {
            return baseAmount > 0 ? TEXT.act_increaseBy(PGR.core.strings.combat_uses, getAmountRawString()) : TEXT.act_remove(TEXT.subjects_this);
        }

        // Perpsective of self depends on the target this is applied to
        String base = joinEffectDisplayTexts(effectsForPower, baseAmount > 0 ? " " : EUIUtils.DOUBLE_SPLIT_LINE, target, requestor, true);
        if (baseAmount > 0) {
            return (TEXT.cond_forTurns(getAmountRawString()) + ", " + StringUtils.uncapitalize(base));
        }

        return base;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String subtext = getCapitalSubText(perspective, requestor, addPeriod);
        // Prevent the final period from showing when this is under another effect, since subtext takes the exact text from another effect
        return (!addPeriod && subtext.endsWith(LocalizedStrings.PERIOD) ? subtext.substring(0, subtext.length() - 1) : subtext) + (childEffect != null ? PCLCoreStrings.period(true) + " " + childEffect.getText(perspective, requestor, addPeriod) : "");
    }

    // Indexes should correspond to the indexes of powers in the card being built
    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        List<Integer> range = Arrays.asList(EUIUtils.range(0, editor.editor.screen.getPowerCount() - 1));
        editor.registerDropdown(range, fields.indexes, item -> String.valueOf(item + 1), PGR.core.strings.cedit_powers, false, false, false);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (source == null) {
            super.use(info, order);
            return;
        }

        List<PTrigger> triggers = EUIUtils.mapAsNonnull(fields.indexes, i -> EUIUtils.safeCast(source.getPowerEffect(i), PTrigger.class));
        if (triggers.isEmpty()) {
            super.use(info, order);
            return;
        }

        PSkill<?> highestParent = getHighestParent();
        boolean referencesSelf = false;
        List<PSkill<?>> powerEffects = source.getPowerEffects();
        for (Integer i : fields.indexes) {
            if (i >= 0 && powerEffects.size() > i) {
                PSkill<?> poEff = powerEffects.get(i);
                referencesSelf = doesPowerReferenceSelf(poEff);
                if (referencesSelf) {
                    break;
                }
            }
        }

        // If this skill is actually part of the power you are applying, we should be able to remove the power if it is an infinite power
        if (referencesSelf && baseAmount <= 0) {
            String id = PTriggerPower.createPowerID(triggers.get(triggers.size() - 1));
            for (AbstractCreature c : getTargetList(info)) {
                order.removePower(c, c, id);
            }
        }
        else {
            // Deliberately allowing applyPower to work with negative values because infinite turn powers need to be negative, unless it references itself
            for (AbstractCreature c : getTargetList(info)) {
                order.applyPower(new PTriggerPower(c, amount, triggers)).skipIfZero(referencesSelf).allowNegative(!referencesSelf).setInfo(info);
            }
        }

        super.use(info, order);
    }
}
