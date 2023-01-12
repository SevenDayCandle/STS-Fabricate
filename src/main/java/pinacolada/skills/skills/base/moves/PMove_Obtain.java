package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.ui.tooltips.EUICardPreview;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.RotatingList;

import java.util.ArrayList;
import java.util.Collection;

public class PMove_Obtain extends PMove
{
    public static final PSkillData DATA = register(PMove_Obtain.class, PCLEffectType.Card)
            .selfTarget();

    public PMove_Obtain()
    {
        this(1);
    }

    public PMove_Obtain(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Obtain(int copies, Collection<String> cards)
    {
        super(DATA, copies, cards);
    }

    public PMove_Obtain(int copies, String... cards)
    {
        super(DATA, copies, cards);
    }

    private AbstractCard getCard(String id)
    {
        AbstractCard c = CardLibrary.getCopy(id);
        if (extra > 0 && c instanceof PCLCard)
        {
            ((PCLCard) c).changeForm(extra, c.timesUpgraded);
        }
        return c;
    }

    @Override
    public PSkill makePreviews(RotatingList<EUICardPreview> previews)
    {
        for (String cd : cardIDs)
        {
            previews.add(EUICardPreview.generatePreviewCard(getCard(cd)));
        }
        return super.makePreviews(previews);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.obtain("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        ArrayList<AbstractCard> created = new ArrayList<>();
        if (useParent && cards != null)
        {
            for (AbstractCard card : cards)
            {
                for (int i = 0; i < amount; i++)
                {
                    AbstractCard c = card.makeStatEquivalentCopy();
                    created.add(c);
                    getActions().makeCard(c, groupTypes.size() > 0 ? groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand);
                }
            }
        }
        else if (cardIDs.isEmpty())
        {
            for (int i = 0; i < amount; i++)
            {
                AbstractCard c = sourceCard.makeStatEquivalentCopy();
                created.add(c);
                getActions().makeCard(c, groupTypes.size() > 0 ? groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand);
            }
        }
        else
        {
            for (String cd : cardIDs)
            {
                for (int i = 0; i < amount; i++)
                {
                    AbstractCard c = getCard(cd);
                    created.add(c);
                    getActions().makeCard(c, groupTypes.size() > 0 ? groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand).setUpgrade(alt, alt2);
                }
            }
        }
        if (this.childEffect != null)
        {
            this.childEffect.receivePayload(created);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String joinedString = useParent ? TEXT.subjects.copiesOf(getInheritedString()) : cardIDs.isEmpty() ? TEXT.subjects.copiesOf(TEXT.subjects.thisObj) : getCardIDAndString();
        return groupTypes.size() > 0 ? TEXT.actions.addToPile(getAmountRawString(), joinedString, groupTypes.get(0).name) : TEXT.actions.obtainAmount(getAmountRawString(), joinedString);
    }
}
