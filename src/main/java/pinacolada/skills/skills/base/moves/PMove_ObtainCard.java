package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.RotatingList;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@VisibleSkill
public class PMove_ObtainCard extends PMove<PField_CardID>
{
    public static final PSkillData<PField_CardID> DATA = register(PMove_ObtainCard.class, PField_CardID.class)
            .setGroups(PCLCardGroupHelper.MasterDeck)
            .selfTarget();

    public PMove_ObtainCard()
    {
        this(1);
    }

    public PMove_ObtainCard(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_ObtainCard(int copies, Collection<String> cards)
    {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardIDs(cards);
    }

    public PMove_ObtainCard(int copies, String... cards)
    {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardIDs(cards);
    }

    @Override
    public PMove_ObtainCard onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
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
    public PMove_ObtainCard makePreviews(RotatingList<EUICardPreview> previews)
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
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_obtain(TEXT.subjects_card);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        ArrayList<AbstractCard> created = new ArrayList<AbstractCard>();
        if (useParent)
        {
            List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
            if (cards != null)
            {
                for (AbstractCard card : cards)
                {
                    for (int i = 0; i < amount; i++)
                    {
                        AbstractCard c = card.makeStatEquivalentCopy();
                        created.add(c);
                        getActions().showAndObtain(c);
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
                getActions().showAndObtain(c);
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
                        getActions().showAndObtain(c);
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
        String joinedString = useParent ? TEXT.subjects_copiesOf(getInheritedString()) : fields.cardIDs.isEmpty() ? TEXT.subjects_copiesOf(TEXT.subjects_thisCard) : fields.getCardIDAndString();
        return TEXT.act_obtain(joinedString);
    }
}
