package pinacolada.skills.skills;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
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

    public PCustomCond(PCLCardData cardData, PField_CardCategory fields) {
        this(cardData, fields, 0, 1, 0);
    }

    public PCustomCond(PCLCardData cardData, int index, int amount, int extra) {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = PGR.core.createID(this.getClass().getSimpleName());
        this.cardData = cardData;
        this.descIndex = index;
    }

    public PCustomCond(PCLCardData cardData, PField_CardCategory fields, int index, int amount, int extra) {
        super(DATA, fields);
        setAmount(amount);
        setExtra(extra);
        this.effectID = PGR.core.createID(this.getClass().getSimpleName());
        this.cardData = cardData;
        this.descIndex = index;
    }

    public PCustomCond(PCLCardData cardData, int index) {
        this(cardData, index, 1, 0);
    }

    public PCustomCond(PCLCardData cardData, PField_CardCategory fields, int index) {
        this(cardData, fields, index, 1, 0);
    }

    public PCustomCond(PCLCardData cardData, int index, int amount) {
        this(cardData, index, amount, 0);
    }

    public PCustomCond(PCustomCond other) {
        this(other.cardData, other.descIndex, other.amount, other.extra);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return false;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return EUIUtils.format(cardData.strings.EXTENDED_DESCRIPTION[descIndex], amount, extra);
    }

    // Assume that special card skill powers are always beneficial to the player (i.e. they are debuffs on enemies and buffs on you/allies)
    @Override
    public boolean isDetrimental() {
        return getOwnerCreature() instanceof AbstractMonster && !(getOwnerCreature() instanceof PCLCardAlly);
    }

    @Override
    public PCustomCond makeCopy() {
        PCustomCond copy = null;
        try {
            Constructor<? extends PCustomCond> c = EUIUtils.tryGetConstructor(this.getClass(), PCustomCond.class);
            if (c != null) {
                copy = c.newInstance(this);
                makeCopyProperties(copy);
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return copy;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        useImpl(info, order);
        super.use(info, order, shouldPay);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        useImpl(info, order);
        super.use(info, order);
    }

    protected void useImpl(PCLUseInfo info, PCLActions order) {
    }
}
