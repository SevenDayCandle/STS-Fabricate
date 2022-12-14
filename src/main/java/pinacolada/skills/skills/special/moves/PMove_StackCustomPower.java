package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.powers.PSkillPower;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PMove_StackCustomPower extends PMove
{

    public static final PSkillData DATA = register(PMove_StackCustomPower.class, PCLEffectType.CustomPower, -1, 999);
    protected ArrayList<Integer> indexes = new ArrayList<>();

    public PMove_StackCustomPower()
    {
        this(PCLCardTarget.Self, 0);
    }

    public PMove_StackCustomPower(PSkillSaveData content)
    {
        super(content);
        this.indexes = EUIUtils.mapAsNonnull(split(content.effectData), Integer::parseInt);
    }

    public PMove_StackCustomPower(PCLCardTarget target, int amount, Integer... indexes)
    {
        super(DATA, target, amount);
        this.indexes.addAll(Arrays.asList(indexes));
    }

    public void addAdditionalData(PSkillSaveData data)
    {
        data.effectData = joinData(indexes, String::valueOf);
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return capital(getSubText(), addPeriod) + (childEffect != null ? PCLCoreStrings.period(true) + " " + childEffect.getText(addPeriod) : "");
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.applyAmount("X", TEXT.cardEditor.custom);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (!(sourceCard instanceof EditorCard))
        {
            return;
        }

        List<PTrigger> triggers = EUIUtils.mapAsNonnull(indexes, i -> ((EditorCard) sourceCard).getPowerEffect(i));
        for (AbstractCreature c : target.getTargets(info.source, info.target))
        {
            getActions().applyPower(new PSkillPower(c, amount, triggers)).allowDuplicates(true);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String base = joinEffectTexts(EUIUtils.mapAsNonnull(indexes, i -> {
            if (sourceCard instanceof EditorCard && ((EditorCard) sourceCard).getPowerEffects().size() > i)
            {
                return ((EditorCard) sourceCard).getPowerEffects().get(i);
            }
            return null;
        }), amount > 0 ? " " : EUIUtils.DOUBLE_SPLIT_LINE, true);
        return amount > 0 ? (TEXT.conditions.forTurns(amount) + ", " + base) : base;
    }

    public List<Integer> getIndexes()
    {
        return this.indexes;
    }

    public PMove_StackCustomPower setIndexes(List<Integer> indexes)
    {
        this.indexes.clear();
        this.indexes.addAll(indexes);
        return this;
    }
}
