package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.RotatingList;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardID;

import java.util.ArrayList;
import java.util.Collection;

@VisibleSkill
public class PMove_Obtain extends PMove<PField_CardID>
{
    public static final PSkillData<PField_CardID> DATA = register(PMove_Obtain.class, PField_CardID.class)
            .selfTarget();

    public PMove_Obtain()
    {
        this(1);
    }

    public PMove_Obtain(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Obtain(int copies, Collection<String> cards)
    {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardIDs(cards);
    }

    public PMove_Obtain(int copies, String... cards)
    {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardIDs(cards);
    }

    private AbstractCard getCard(String id)
    {
        AbstractCard c = CardLibrary.getCard(id);
        if (c != null)
        {
            c = c.makeCopy();
            if (extra > 0 && c instanceof PCLCard)
            {
                ((PCLCard) c).changeForm(extra, c.timesUpgraded);
            }
            return c;
        }
        return null;
    }

    @Override
    public PMove_Obtain makePreviews(RotatingList<EUICardPreview> previews)
    {
        for (String cd : fields.cardIDs)
        {
            AbstractCard c = getCard(cd);
            if (c != null)
            {
                previews.add(EUICardPreview.generatePreviewCard(c));
            }
        }
        super.makePreviews(previews);
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_obtain(TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        ArrayList<AbstractCard> created = new ArrayList<AbstractCard>();
        if (useParent)
        {
            ArrayList<AbstractCard> cards = info.getData();
            if (cards != null)
            {
                for (AbstractCard card : cards)
                {
                    for (int i = 0; i < amount; i++)
                    {
                        AbstractCard c = card.makeStatEquivalentCopy();
                        created.add(c);
                        getActions().makeCard(c, fields.groupTypes.size() > 0 ? fields.groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand);
                    }
                }
            }
        }
        else if (fields.cardIDs.isEmpty())
        {
            for (int i = 0; i < amount; i++)
            {
                AbstractCard c = sourceCard.makeStatEquivalentCopy();
                created.add(c);
                getActions().makeCard(c, fields.groupTypes.size() > 0 ? fields.groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand);
            }
        }
        else
        {
            for (String cd : fields.cardIDs)
            {
                for (int i = 0; i < amount; i++)
                {
                    AbstractCard c = getCard(cd);
                    if (c != null)
                    {
                        created.add(c);
                        getActions().makeCard(c, fields.groupTypes.size() > 0 ? fields.groupTypes.get(0).getCardGroup() : AbstractDungeon.player.hand).setUpgrade(fields.forced, false);
                    }
                }
            }
        }
        info.setData(created);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String joinedString = useParent ? TEXT.subjects_copiesOf(getInheritedString()) : fields.cardIDs.isEmpty() ? TEXT.subjects_copiesOf(TEXT.subjects_thisObj) : fields.getCardIDAndString();
        return fields.groupTypes.size() > 0 ? TEXT.act_addToPile(getAmountRawString(), joinedString, fields.groupTypes.get(0).name) : TEXT.act_obtainAmount(getAmountRawString(), joinedString);
    }
}
