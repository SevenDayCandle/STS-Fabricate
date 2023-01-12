package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

public class PMove_ObtainRandomCard extends PMove
{
    public static final PSkillData DATA = register(PMove_ObtainRandomCard.class, PField_CardCategory.class)
            .setExtra(1, DEFAULT_MAX)
            .selfTarget();

    public PMove_ObtainRandomCard()
    {
        this(1);
    }

    public PMove_ObtainRandomCard(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ObtainRandomCard(int copies, PCLCardGroupHelper... gt)
    {
        super(DATA, PCLCardTarget.None, copies, gt);
    }

    public PMove_ObtainRandomCard(int copies, int extra, PCLCardGroupHelper... gt)
    {
        super(DATA, PCLCardTarget.None, copies, gt);
        setExtra(extra);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.obtain(TEXT.subjects.randomX(TEXT.subjects.card));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        final CardGroup choice = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        final int limit = Math.max(extra, amount);
        while (choice.size() < limit)
        {
            AbstractCard c = GameUtilities.getRandomCard(rarities, types, affinities);
            if (c != null && !EUIUtils.any(choice.group, ca -> ca.cardID.equals(c.cardID)))
            {
                choice.addToBottom(c.makeCopy());
            }
        }
        boolean automatic = extra <= amount;
        getActions().selectFromPile(getName(), amount, choice)
                .setOptions(automatic, automatic)
                .addCallback(cards -> {
                    for (AbstractCard c : cards)
                    {
                        getActions().makeCard(c, groupTypes.size() > 0 ? groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand);
                    }
                    if (this.childEffect != null)
                    {
                        this.childEffect.receivePayload(choice.group);
                        super.use(info);
                    }
                });
    }

    @Override
    public String getSubText()
    {
        String amString = extra > amount ? TEXT.subjects.xOfY(getAmountRawString(), getExtraRawString()) : getAmountRawString();
        String cString = getFullCardOrString(getRawString(EXTRA_CHAR));
        return groupTypes.size() > 0 ? TEXT.actions.addToPile(amString, cString, groupTypes.get(0).name) : TEXT.actions.obtainAmount(amString, cString);
    }
}
