package pinacolada.skills.skills;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_CardCategory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PCustomMod extends PMod<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PCustomMod.class, PField_CardCategory.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .setExtra(-DEFAULT_MAX, DEFAULT_MAX);
    protected final PCLCardData cardData;
    protected int descIndex;

    public PCustomMod(PCLCardData cardData) {
        this(cardData, 0, 1, 0);
    }

    public PCustomMod(PCLCardData cardData, int index, int amount, int extra) {
        super(DATA);
        setAmount(amount);
        setExtra(extra);
        this.effectID = PGR.core.createID(this.getClass().getSimpleName());
        this.cardData = cardData;
        this.descIndex = index;
    }

    public PCustomMod(PCLCardData cardData, int index) {
        this(cardData, index, 1, 0);
    }

    public PCustomMod(PCLCardData cardData, int index, int amount) {
        this(cardData, index, amount, 0);
    }

    public PCustomMod(PCustomMod other) {
        this(other.cardData, other.descIndex, other.amount, other.extra);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return EUIUtils.format(cardData.strings.EXTENDED_DESCRIPTION[descIndex], amount, extra);
    }

    public PCustomMod makeCopy() {
        PCustomMod copy = null;
        try {
            Constructor<? extends PCustomMod> c = EUIUtils.tryGetConstructor(this.getClass(), PCustomMod.class);
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
}
