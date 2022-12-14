package pinacolada.skills.skills;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class PCustomCond extends PCond
{
    public static final PSkillData DATA = register(PCustomCond.class, PCLEffectType.General)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX);
    private static final HashMap<String, PSkillData> CUSTOM_MAP = new HashMap<>();
    protected final PCLCardData cardData;
    protected int descIndex;

    public PCustomCond(PCLCardData cardData)
    {
        this(cardData, 0, 1, 0);
    }

    public PCustomCond(PCLCardData cardData, int index)
    {
        this(cardData, index, 1, 0);
    }

    public PCustomCond(PCLCardData cardData, int index, int amount)
    {
        this(cardData, index, amount, 0);
    }

    public PCustomCond(PCLCardData cardData, int index, int amount, int extra)
    {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = PGR.core.createID(this.getClass().getSimpleName());
        this.cardData = cardData;
        this.descIndex = index;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return false;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        useImpl(info);
        super.use(info);
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
        useImpl(info);
        super.use(info, index);
    }

    @Override
    public void use(PCLUseInfo info, boolean isUsing)
    {
        useImpl(info);
        super.use(info, isUsing);
    }

    @Override
    public String getSubText()
    {
        return EUIUtils.format(cardData.strings.EXTENDED_DESCRIPTION[descIndex], amount, extra);
    }

    protected void useImpl(PCLUseInfo info)
    {
    }

    public PCustomCond makeCopy()
    {
        PCustomCond copy = null;
        try
        {
            Constructor<? extends PCustomCond> c = EUIUtils.tryGetConstructor(this.getClass(), PCLCardData.class);
            if (c != null)
            {
                copy = c.newInstance(cardData);
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

        return (PCustomCond) makeCopyProperties(copy);
    }
}
