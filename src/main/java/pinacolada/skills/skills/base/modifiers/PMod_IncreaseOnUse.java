package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.utilities.ColoredString;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveMod;

@VisibleSkill
public class PMod_IncreaseOnUse extends PPassiveMod<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PMod_IncreaseOnUse.class, PField_Empty.class).selfTarget();

    public PMod_IncreaseOnUse(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_IncreaseOnUse()
    {
        this(0);
    }

    public PMod_IncreaseOnUse(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_increaseBy(TEXT.subjects_x, TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        return amount < 0 ? TEXT.act_reduceBy(TEXT.subjects_thisCard, getAmountRawString()) : TEXT.act_increaseBy(TEXT.subjects_thisCard, getAmountRawString());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return TEXT.cond_doThen(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", getSubText()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            this.childEffect.use(info);
            getActions().callback(() -> {
                this.childEffect.addAmountForCombat(amount);
            });
        }
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
        if (this.childEffect != null)
        {
            this.childEffect.use(info, index);
            getActions().callback(() -> {
                this.childEffect.addAmountForCombat(amount);
            });
        }
    }

    @Override
    public void onDrag(AbstractMonster m)
    {
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet)
    {
    }

    @Override
    public final ColoredString getColoredValueString()
    {
        if (baseAmount != amount)
        {
            return new ColoredString(amount > 0 ? "+" + amount : amount, amount >= baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR);
        }

        return new ColoredString(amount > 0 ? "+" + amount : amount, Settings.CREAM_COLOR);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info)
    {
        return amount;
    }
}
