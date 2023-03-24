package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT5;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_CardGeneric extends PField
{
    public ArrayList<PCLCardGroupHelper> groupTypes = new ArrayList<>();
    public ArrayList<PCLCardGroupHelper> baseGroupTypes = groupTypes;
    public PCLCardSelection origin = PCLCardSelection.Manual;
    public boolean not;
    public boolean forced;

    public PField_CardGeneric()
    {
        super();
    }

    public PField_CardGeneric(PField_CardGeneric other)
    {
        super();
        setCardGroup(other.groupTypes);
        setOrigin(other.origin);
        setNot(other.not);
        setForced(other.forced);
    }

    @Override
    public boolean equals(PField other)
    {
        return super.equals(other)
                && groupTypes.equals(((PField_CardGeneric) other).groupTypes)
                && origin.equals(((PField_CardGeneric) other).origin)
                && not == ((PField_CardGeneric) other).not
                && forced == ((PField_CardGeneric) other).forced;
    }

    @Override
    public PField_CardGeneric makeCopy()
    {
        return new PField_CardGeneric(this);
    }

    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {
        editor.registerPile(groupTypes);
        editor.registerOrigin(origin, origins -> setOrigin(origins.size() > 0 ? origins.get(0) : PCLCardSelection.Manual));
        super.setupEditor(editor);
    }

    public PField_CardGeneric setOrigin(PCLCardSelection origin)
    {
        this.origin = origin;
        return this;
    }

    public PField_CardGeneric setCardGroup(PCLCardGroupHelper... gt)
    {
        return setCardGroup(Arrays.asList(gt));
    }

    public PField_CardGeneric setCardGroup(List<PCLCardGroupHelper> gt)
    {
        this.groupTypes.clear();
        this.groupTypes.addAll(gt);
        this.baseGroupTypes = this.groupTypes;
        return this;
    }

    public PField_CardGeneric setForced(boolean value)
    {
        this.forced = value;
        return this;
    }

    public PField_CardGeneric setNot(boolean value)
    {
        this.not = value;
        return this;
    }

    public PField_CardGeneric setRandom()
    {
        this.origin = PCLCardSelection.Random;
        return this;
    }

    public PField_CardGeneric setTemporaryGroups(ArrayList<PCLCardGroupHelper> cardGroups)
    {
        this.groupTypes = cardGroups;
        return this;
    }

    public PField_CardGeneric resetTemporaryGroups()
    {
        this.groupTypes = baseGroupTypes;
        return this;
    }

    public String getGroupString()
    {
        return getGroupString(groupTypes, origin);
    }

    public String getShortCardString()
    {
        return isRandom() ? PSkill.TEXT.subjects_randomX(skill.pluralCard()) : skill.pluralCard();
    }

    public String getFullCardString()
    {
        return getShortCardString();
    }

    public String getFullCardStringSingular()
    {
        return getFullCardString();
    }

    public final CardGroup[] getCardGroup(PCLUseInfo info)
    {
        if (skill.useParent)
        {
            ArrayList<AbstractCard> cards = info.getData();
            CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            if (cards != null)
            {
                for (AbstractCard c : cards)
                {
                    g.addToBottom(c);
                }
            }
            return new CardGroup[]{g};
        }
        else if (groupTypes.isEmpty() && skill.sourceCard != null)
        {
            CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            g.addToBottom(skill.sourceCard);
            return new CardGroup[]{g};
        }
        else
        {
            return EUIUtils.map(groupTypes, PCLCardGroupHelper::getCardGroup).toArray(new CardGroup[]{});
        }
    }

    protected SelectFromPile createAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> action, PCLUseInfo info)
    {
        CardGroup[] g = getCardGroup(info);
        return action.invoke(skill.getName(), skill.target.getTarget(info.source, info.target), skill.useParent && g.length > 0 ? g[0].size() : skill.amount <= 0 ? Integer.MAX_VALUE : skill.amount, origin.toSelection(), g);
    }

    public PCLAction<ArrayList<AbstractCard>> getGenericPileAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> action, PCLUseInfo info)
    {
        if (!skill.useParent && groupTypes.isEmpty())
        {
            return PCLActions.last.add(createAction(action, info))
                    .setAnyNumber(!forced);
        }
        else
        {
            SelectFromPile pileAction = initializeBasicSelect(action, info);
            if (forced)
            {
                pileAction.setOrigin(PCLCardSelection.Random);
            }
            return pileAction;
        }
    }

    protected SelectFromPile initializeBasicSelect(FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> action, PCLUseInfo info)
    {
        return skill.getActions().add(createAction(action, info))
            .setAnyNumber(!forced);
    }

    public boolean hasGroups()
    {
        return !EUIUtils.isNullOrEmpty(groupTypes);
    }

    public boolean isRandom()
    {
        return origin == PCLCardSelection.Random;
    }
}
