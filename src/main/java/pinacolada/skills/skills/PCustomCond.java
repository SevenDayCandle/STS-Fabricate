package pinacolada.skills.skills;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardData;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_CardCategory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PCustomCond extends PCond<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PCustomCond.class, PField_CardCategory.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX);
    protected final PCLCardData cardData;
    protected int descIndex;

    public PCustomCond(PCLCardData cardData) {
        this(cardData, 0, 1, 0);
    }

    public PCustomCond(PCLCardData cardData, int index, int amount, int extra) {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = PGR.core.createID(this.getClass().getSimpleName());
        this.cardData = cardData;
        this.descIndex = index;
    }

    public PCustomCond(PCLCardData cardData, int index) {
        this(cardData, index, 1, 0);
    }

    public PCustomCond(PCLCardData cardData, int index, int amount) {
        this(cardData, index, amount, 0);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return false;
    }

    @Override
    public void use(PCLUseInfo info) {
        useImpl(info);
        super.use(info);
    }

    @Override
    public void use(PCLUseInfo info, int index) {
        useImpl(info);
        super.use(info, index);
    }

    @Override
    public void use(PCLUseInfo info, boolean isUsing) {
        useImpl(info);
        super.use(info, isUsing);
    }

    @Override
    public String getSubText() {
        return EUIUtils.format(cardData.strings.EXTENDED_DESCRIPTION[descIndex], amount, extra);
    }

    public PCustomCond makeCopy() {
        PCustomCond copy = null;
        try {
            Constructor<? extends PCustomCond> c = EUIUtils.tryGetConstructor(this.getClass(), PCLCardData.class);
            if (c != null) {
                copy = c.newInstance(cardData);
                makeCopyProperties(copy);
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return copy;
    }

    protected void useImpl(PCLUseInfo info) {
    }
}
