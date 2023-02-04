package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.SummonOnlyMove;
import pinacolada.misc.PCLUseInfo;
import pinacolada.powers.PSkillPower;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CustomPower;
import pinacolada.skills.skills.PTrigger;

import java.util.Arrays;
import java.util.List;

@VisibleSkill
public class PMove_StackCustomPower extends PMove<PField_CustomPower> implements SummonOnlyMove
{

    public static final PSkillData<PField_CustomPower> DATA = register(PMove_StackCustomPower.class, PField_CustomPower.class, -1, DEFAULT_MAX);

    public PMove_StackCustomPower()
    {
        this(PCLCardTarget.Self, 0);
    }

    public PMove_StackCustomPower(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_StackCustomPower(PCLCardTarget target, int amount, Integer... indexes)
    {
        super(DATA, target, amount);
        fields.setIndexes(Arrays.asList(indexes));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return capital(getSubText(), addPeriod) + (childEffect != null ? PCLCoreStrings.period(true) + " " + childEffect.getText(addPeriod) : "");
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_applyAmount(TEXT.subjects_x, TEXT.cedit_custom);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (!(sourceCard instanceof EditorCard))
        {
            return;
        }

        List<PTrigger> triggers = EUIUtils.mapAsNonnull(fields.indexes, i -> ((EditorCard) sourceCard).getPowerEffect(i));
        for (AbstractCreature c : target.getTargets(info.source, info.target))
        {
            getActions().applyPower(new PSkillPower(c, amount, triggers)).allowDuplicates(true);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String base = joinEffectTexts(EUIUtils.mapAsNonnull(fields.indexes, i -> {
            if (sourceCard instanceof EditorCard && ((EditorCard) sourceCard).getPowerEffects().size() > i)
            {
                return ((EditorCard) sourceCard).getPowerEffects().get(i);
            }
            return null;
        }), amount > 0 ? " " : EUIUtils.DOUBLE_SPLIT_LINE, true);
        return amount > 0 ? (TEXT.cond_forTurns(amount) + ", " + base) : base;
    }
}
