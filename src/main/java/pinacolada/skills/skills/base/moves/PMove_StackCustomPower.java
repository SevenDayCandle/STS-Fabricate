package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.powers.PSkillPower;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CustomPower;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMove_StackCustomPower extends PMove<PField_CustomPower> implements SummonOnlyMove {

    public static final PSkillData<PField_CustomPower> DATA = register(PMove_StackCustomPower.class, PField_CustomPower.class, -1, DEFAULT_MAX);

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
        return TEXT.act_applyAmountX(TEXT.subjects_x, TEXT.cedit_custom);
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        String subtext = getCapitalSubText(perspective, addPeriod);
        // Prevent the final period from showing when this is under another effect, since subtext takes the exact text from another effect
        return (!addPeriod && subtext.endsWith(LocalizedStrings.PERIOD) ? subtext.substring(0, subtext.length() - 1) : subtext) + (childEffect != null ? PCLCoreStrings.period(true) + " " + childEffect.getText(perspective, addPeriod) : "");
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        // If this skill is under a PTrigger and this references that same PTrigger, the game will crash from a stack overflow
        // In this instance, we should describe the power itself instead
        ArrayList<PSkill<?>> effectsForPower = new ArrayList<>();
        PSkill<?> highestParent = getHighestParent();
        boolean referencesSelf = false;
        if (source != null) {
            ArrayList<PTrigger> powerEffects = source.getPowerEffects();
            for (Integer i : fields.indexes) {
                if (i >= 0 && powerEffects.size() > i) {
                    PTrigger poEff = powerEffects.get(i);
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
        String base = joinEffectTexts(effectsForPower, baseAmount > 0 ? " " : EUIUtils.DOUBLE_SPLIT_LINE, target, true);
        if (baseAmount > 0) {
            return (TEXT.cond_forTurns(getAmountRawString()) + ", " + StringUtils.uncapitalize(base));
        }

        return base;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (source == null) {
            super.use(info, order);
            return;
        }

        List<PTrigger> triggers = EUIUtils.mapAsNonnull(fields.indexes, i -> source.getPowerEffect(i));
        if (triggers.isEmpty()) {
            super.use(info, order);
            return;
        }

        PSkill<?> highestParent = getHighestParent();
        boolean referencesSelf = false;
        ArrayList<PTrigger> powerEffects = source.getPowerEffects();
        for (Integer i : fields.indexes) {
            if (i >= 0 && powerEffects.size() > i) {
                PTrigger poEff = powerEffects.get(i);
                referencesSelf = doesPowerReferenceSelf(poEff);
                if (referencesSelf) {
                    break;
                }
            }
        }

        // If this skill is actually part of the power you are applying, we should be able to remove the power if it is an infinite power
        if (referencesSelf && baseAmount <= 0) {
            String id = PSkillPower.createPowerID(triggers.get(triggers.size() - 1));
            for (AbstractCreature c : getTargetList(info)) {
                order.removePower(c, c, id);
            }
        }
        else {
            // Deliberately allowing applyPower to work with negative values because infinite turn powers need to be negative, unless it references itself
            for (AbstractCreature c : getTargetList(info)) {
                order.applyPower(new PSkillPower(c, amount, triggers)).skipIfZero(referencesSelf).allowNegative(!referencesSelf);
            }
        }

        super.use(info, order);
    }
}
