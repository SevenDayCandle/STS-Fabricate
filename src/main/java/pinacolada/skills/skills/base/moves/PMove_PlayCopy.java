package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.RotatingList;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardID;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMove_PlayCopy extends PMove<PField_CardID>
{
    public static final PSkillData<PField_CardID> DATA = register(PMove_PlayCopy.class, PField_CardID.class)
            .selfTarget();

    public PMove_PlayCopy()
    {
        this(1, PCLCardTarget.RandomEnemy);
    }

    public PMove_PlayCopy(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_PlayCopy(int copies, PCLCardTarget target, String... cards)
    {
        super(DATA, target, copies);
        fields.setCardIDs(cards);
    }

    @Override
    public PMove_PlayCopy makePreviews(RotatingList<EUICardPreview> previews)
    {
        for (String cd : fields.cardIDs)
        {
            AbstractCard c = CardLibrary.getCard(cd);
            if (c != null)
            {
                previews.add(EUICardPreview.generatePreviewCard(c.makeCopy()));
            }
        }
        return this;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_play(TEXT.subjects_copiesOf(TEXT.subjects_x));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (fields.cardIDs.isEmpty())
        {
            if (useParent)
            {
                List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
                if (cards != null)
                {
                    for (AbstractCard c : cards)
                    {
                        if (GameUtilities.canPlayTwice(c))
                        {
                            for (int i = 0; i < amount; i++)
                            {
                                getActions().playCopy(c, target.getTarget(info.source, info.target));
                            }
                        }
                    }
                }
            }
            else if (GameUtilities.canPlayTwice(sourceCard))
            {
                for (int i = 0; i < amount; i++)
                {
                    getActions().playCopy(sourceCard, target.getTarget(info.source, info.target));
                }
            }
        }
        else
        {
            for (String cd : fields.cardIDs)
            {
                for (int i = 0; i < amount; i++)
                {
                    getActions().playCopy(CardLibrary.getCopy(cd), target.getTarget(info.source, info.target));
                }
            }
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_playXTimes(hasParentType(PTrigger.class) ? getInheritedString() : fields.cardIDs.isEmpty() ? TEXT.subjects_thisX : fields.getCardIDAndString(), getAmountRawString());
    }
}
