package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.RotatingList;
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.effects.PCLEffects;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_Transform extends PMove_Select
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Transform.class, PField_CardCategory.class)
            .selfTarget();

    public PMove_Transform()
    {
        this(1);
    }

    public PMove_Transform(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Transform(int amount, PCLCardGroupHelper... groupHelpers)
    {
        super(DATA, amount, groupHelpers);
    }

    public PMove_Transform(String... cards)
    {
        super(DATA, 1);
        fields.setCardIDs(cards);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.transform;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return SelectFromPile::new;
    }

    private AbstractCard getCard(String id)
    {
        AbstractCard c = CardLibrary.getCopy(id);
        if (c == null)
        {
            c = GameUtilities.getRandomCard();
        }
        // TODO custom variable
        if (fields.forced && c != null)
        {
            c.upgrade();
        }
        if (extra > 0 && c instanceof PCLCard)
        {
            ((PCLCard) c).changeForm(extra, c.timesUpgraded);
        }
        return c;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_transform(TEXT.subjects_x, TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (!fields.hasGroups() && !useParent && sourceCard != null)
        {
            transformImpl(sourceCard);
        }
        else
        {
            fields.getGenericPileAction(getAction(), info)
                    .addCallback(cards -> {
                        for (AbstractCard c : cards)
                        {
                            transformImpl(c);
                        }
                    });
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_transform(
                useParent ? getInheritedString() : fields.groupTypes.size() > 0 ? EUIRM.strings.numNounPlace(getAmountRawString(), fields.getFullCardString(), TEXT.subjects_from(fields.getGroupString())) : TEXT.subjects_thisObj, fields.getCardIDOrString()
        );
    }

    @Override
    public PMove_Transform makePreviews(RotatingList<EUICardPreview> previews)
    {
        for (String cd : fields.cardIDs)
        {
            previews.add(EUICardPreview.generatePreviewCard(getCard(cd)));
        }
        super.makePreviews(previews);
        return this;
    }

    private void transformImpl(AbstractCard c)
    {
        AbstractCard c2 = getCard(fields.cardIDs.isEmpty() ? null : fields.cardIDs.get(0));
        if (c2 != null)
        {
            PCLActions.last.replaceCard(c.uuid, c2);
            PCLEffects.Queue.showCardBriefly(c2.makeStatEquivalentCopy());
        }
    }
}
