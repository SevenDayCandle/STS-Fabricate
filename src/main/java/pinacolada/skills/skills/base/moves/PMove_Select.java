package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.SelectFromPileMarker;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.CardSelection;
import pinacolada.utilities.GameActions;

public abstract class PMove_Select extends PMove implements SelectFromPileMarker
{
    public PMove_Select(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Select(PSkillData data, int amount, PCLCardGroupHelper... h)
    {
        super(data, PCLCardTarget.None, amount, h);
    }

    protected SelectFromPile createAction(PCLUseInfo info)
    {
        CardGroup[] g = getCardGroup();
        return getAction().invoke(getName(), useParent && g.length > 0 ? g[0].size() : amount <= 0 ? Integer.MAX_VALUE : amount, g);
    }

    @Override
    public String getSampleText()
    {
        return EUIRM.strings.verbNoun(tooltipTitle(), "X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (!useParent && groupTypes.isEmpty())
        {
            GameActions.last.add(createAction(info))
                    .setOptions(true, true)
                    .addCallback(cards -> {
                        if (this.childEffect != null)
                        {
                            this.childEffect.setCards(cards);
                            this.childEffect.use(info);
                        }
                    });
        }
        else
        {
            getActions().add(createAction(info))
                    .setFilter(cardIDs.isEmpty() ? getFullCardFilter() : c -> EUIUtils.any(cardIDs, id -> id.equals(c.cardID)))
                    .setOptions(alt || amount <= 0 ? CardSelection.Random : origin, !alt2)
                    .addCallback(cards -> {
                        if (this.childEffect != null)
                        {
                            this.childEffect.setCards(cards);
                            this.childEffect.use(info);
                        }
                    });
        }
    }

    @Override
    public String getSubText()
    {
        return useParent ? EUIRM.strings.verbNoun(tooltipTitle(), getInheritedString()) :
                !groupTypes.isEmpty() ? TEXT.actions.genericFrom(tooltipTitle(), amount <= 0 ? TEXT.subjects.all : getAmountRawString(), !cardIDs.isEmpty() ? getCardIDOrString() : getFullCardString(getRawString(EFFECT_CHAR)), getGroupString())
                        : EUIRM.strings.verbNoun(tooltipTitle(), TEXT.subjects.thisObj);
    }
}
