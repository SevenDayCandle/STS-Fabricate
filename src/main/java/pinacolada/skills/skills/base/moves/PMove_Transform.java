package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActions;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RotatingList;

public class PMove_Transform extends PMove_Select
{
    public static final PSkillData DATA = register(PMove_Transform.class, PCLEffectType.Card)
            .selfTarget();

    public PMove_Transform()
    {
        this((String) null);
    }

    public PMove_Transform(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Transform(int amount, PCLCardGroupHelper... groupHelpers)
    {
        super(DATA, amount, groupHelpers);
    }

    public PMove_Transform(String... cards)
    {
        super(DATA, 1);
        setCardIDs(cards);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.transform;
    }

    @Override
    public FuncT3<SelectFromPile, String, Integer, CardGroup[]> getAction()
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
        if (alt2 && c != null)
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
        return TEXT.actions.transform("X", "Y");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (groupTypes.isEmpty() && !useParent && sourceCard != null)
        {
            transformImpl(sourceCard);
        }
        else
        {
            getActions().add(createAction(info))
                    .setFilter(getFullCardFilter())
                    .setOptions(alt || useParent, true)
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
        return TEXT.actions.transform(
                useParent ? getInheritedString() : groupTypes.size() > 0 ? EUIRM.strings.numNounPlace(getAmountRawString(), getFullCardString(), TEXT.subjects.from(getGroupString())) : TEXT.subjects.thisObj, getCardIDOrString()
        );
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

    private void transformImpl(AbstractCard c)
    {
        AbstractCard c2 = getCard(cardIDs.isEmpty() ? null : cardIDs.get(0));
        if (c2 != null)
        {
            PCLActions.last.replaceCard(c.uuid, c2);
            PCLEffects.Queue.showCardBriefly(c2.makeStatEquivalentCopy());
        }
    }
}
