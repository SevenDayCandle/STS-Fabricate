package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.utilities.RotatingList;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardID;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

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
            previews.add(EUICardPreview.generatePreviewCard(CardLibrary.getCopy(cd)));
        }
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_play(TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (fields.cardIDs.isEmpty())
        {
            ArrayList<AbstractCard> cards = info.getData(new ArrayList<>());
            if (!cards.isEmpty())
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
        return fields.cardIDs.isEmpty() ? EUIRM.strings.verbNounAdv(PGR.core.tooltips.play.title, hasParentType(PTrigger.class) ? getInheritedString() : TEXT.subjects_thisX, TEXT.subjects_times(amount))
                : TEXT.act_play(PCLCoreStrings.joinWithAnd(EUIUtils.map(fields.cardIDs, g -> "{" + PGR.getCardStrings(g).NAME + "}")));
    }
}
