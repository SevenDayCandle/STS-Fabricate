package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLUseInfo;
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
    public ListSelection<AbstractCard> origin = null; // TODO use this instead of random to denote random
    public boolean random; // TODO change to not
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
        setRandom(other.random);
        setForced(other.forced);
    }

    @Override
    public boolean equals(PField other)
    {
        return super.equals(other)
                && groupTypes.equals(((PField_CardGeneric) other).groupTypes)
                && origin.equals(((PField_CardGeneric) other).origin)
                && random == ((PField_CardGeneric) other).random
                && forced == ((PField_CardGeneric) other).forced;
    }

    @Override
    public PField_CardGeneric makeCopy()
    {
        return new PField_CardGeneric(this);
    }

    public void setupEditor(PCLCustomCardEffectEditor editor)
    {
        editor.registerPile(groupTypes);
        super.setupEditor(editor);
    }

    public PField_CardGeneric setOrigin(ListSelection<AbstractCard> origin)
    {
        this.origin = origin;
        return this;
    };

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

    public PField_CardGeneric setRandom(boolean value)
    {
        this.random = value;
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
        return random ? PSkill.TEXT.subjects.randomX(skill.pluralCard()) : skill.pluralCard();
    }

    public final CardGroup[] getCardGroup(PCLUseInfo info)
    {
        ArrayList<AbstractCard> cards = info.getData(null);
        if (skill.useParent && cards != null)
        {
            CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : cards)
            {
                g.addToBottom(c);
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

    public boolean hasGroups()
    {
        return !EUIUtils.isNullOrEmpty(groupTypes);
    }
}
