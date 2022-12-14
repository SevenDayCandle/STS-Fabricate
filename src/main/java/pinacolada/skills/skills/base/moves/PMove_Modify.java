package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;
import java.util.List;

public abstract class PMove_Modify extends PMove
{

    public PMove_Modify(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Modify(PSkillData data, int amount, ArrayList<AbstractCard> cards)
    {
        super(data, PCLCardTarget.None, amount);
        this.cards = cards;
    }

    public PMove_Modify(PSkillData data, int amount, int extraAmount)
    {
        this(data, amount, extraAmount, new ArrayList<>());
    }

    public PMove_Modify(PSkillData data, int amount, int extraAmount, PCLCardGroupHelper... groups)
    {
        this(data, amount, extraAmount);
        setCardGroup(groups);
    }

    public PMove_Modify(PSkillData data, int amount, int extraAmount, ArrayList<AbstractCard> cards)
    {
        this(data, amount, cards);
        setExtra(extraAmount);
    }

    public PMove_Modify(PSkillData data, int amount, ArrayList<AbstractCard> cards, PCLAffinity... affinities)
    {
        super(data, PCLCardTarget.None, amount, affinities);
        this.cards = cards;
    }

    public PMove_Modify(PSkillData data, int amount, int extraAmount, ArrayList<AbstractCard> cards, PCLAffinity... affinities)
    {
        this(data, amount, cards, affinities);
        setExtra(extraAmount);
    }

    public PMove_Modify(PSkillData data, int amount, ArrayList<AbstractCard> cards, PCLCardTag... tags)
    {
        super(data, amount, tags);
        this.cards = cards;
    }

    public PMove_Modify(PSkillData data, int amount, int extraAmount, ArrayList<AbstractCard> cards, PCLCardTag... tags)
    {
        this(data, amount, cards, tags);
        setExtra(extraAmount);
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType)
    {
        return PSkill.register(type, effectType, getDefaultPriority(type), 0, DEFAULT_MAX)
                .setExtra(-DEFAULT_MAX, DEFAULT_MAX)
                .selfTarget();
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType, AbstractCard.CardColor... cardColors)
    {
        return PSkill.register(type, effectType, getDefaultPriority(type), 0, DEFAULT_MAX, cardColors)
                .setExtra(-DEFAULT_MAX, DEFAULT_MAX)
                .selfTarget();
    }

    public void cardAction(List<AbstractCard> cards)
    {
        for (AbstractCard c : cards)
        {
            getAction().invoke(c);
        }
    }

    public abstract ActionT1<AbstractCard> getAction();

    public String getObjectSampleText()
    {
        return getObjectText();
    }

    public abstract String getObjectText();

    @Override
    public String wrapExtra(int input)
    {
        return input > 0 ? "+" + input : String.valueOf(input);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.giveTarget(TEXT.subjects.card, getObjectSampleText());
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().selectFromPile(getName(), amount <= 0 ? Integer.MAX_VALUE : amount, getCardGroup())
                .setOptions(amount <= 0 || groupTypes.isEmpty(), true)
                .addCallback(this::cardAction);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String giveString = getObjectText();
        return useParent || (cards != null && !cards.isEmpty()) ? TEXT.actions.giveTarget(getInheritedString(), giveString) :
                groupTypes != null && !groupTypes.isEmpty() ?
                        TEXT.actions.giveFrom(EUIRM.strings.numNoun(amount <= 0 ? TEXT.subjects.all : getAmountRawString(), pluralCard()), getGroupString(), giveString) :
                        TEXT.actions.giveTarget(TEXT.subjects.thisX, giveString);
    }
}
