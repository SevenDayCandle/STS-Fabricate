package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.RotatingList;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardID;
import pinacolada.skills.skills.PCallbackMove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@VisibleSkill
public class PMove_Create extends PCallbackMove<PField_CardID>
{
    public static final PSkillData<PField_CardID> DATA = register(PMove_Create.class, PField_CardID.class)
            .selfTarget();

    public PMove_Create()
    {
        this(1);
    }

    public PMove_Create(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Create(int copies, Collection<String> cards)
    {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardIDs(cards);
    }

    public PMove_Create(int copies, String... cards)
    {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardIDs(cards);
    }

    private AbstractCard getCard(String id)
    {
        AbstractCard c = CardLibrary.getCard(id);
        if (c != null)
        {
            return c.makeCopy();
        }
        return null;
    }

    @Override
    public PMove_Create makePreviews(RotatingList<EUICardPreview> previews)
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
        return TEXT.act_addToPile(TEXT.subjects_x, TEXT.subjects_card, TEXT.cedit_pile);
    }

    @Override
    public void use(PCLUseInfo info, ActionT1<PCLUseInfo> callback)
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
        callback.invoke(info);
        if (this.childEffect != null)
        {
            this.childEffect.use(info);
        }
    }

    @Override
    public String getSubText()
    {
        String joinedString = useParent ? TEXT.subjects_copiesOf(getInheritedString()) : fields.cardIDs.isEmpty() ? TEXT.subjects_copiesOf(TEXT.subjects_thisCard) : fields.getCardIDAndString();
        return fields.groupTypes.size() > 0 ? TEXT.act_addToPile(getAmountRawString(), joinedString, fields.groupTypes.get(0).name) : TEXT.act_addToPile(getAmountRawString(), joinedString, PCLCardGroupHelper.Hand.name);
    }
}
