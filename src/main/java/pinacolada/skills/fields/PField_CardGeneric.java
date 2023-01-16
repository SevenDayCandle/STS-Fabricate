package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardSelection;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

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
        editor.registerOrigin(origin);
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
        return isRandom() ? PSkill.TEXT.subjects.randomX(skill.pluralCard()) : skill.pluralCard();
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

    public boolean isRandom()
    {
        return origin == PCLCardSelection.Random;
    }
}
