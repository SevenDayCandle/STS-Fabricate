package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_CardCategory extends PField_CardID
{
    public ArrayList<AbstractCard.CardColor> colors = new ArrayList<>();
    public ArrayList<AbstractCard.CardRarity> rarities = new ArrayList<>();
    public ArrayList<AbstractCard.CardType> types = new ArrayList<>();
    public ArrayList<PCLAffinity> affinities = new ArrayList<>();
    public ArrayList<PCLCardTag> tags = new ArrayList<>();

    public PField_CardCategory()
    {
        super();
    }

    public PField_CardCategory(PField_CardCategory other)
    {
        super(other);
        setAffinity(other.affinities);
        setColor(other.colors);
        setRarity(other.rarities);
        setType(other.types);
        setTag(other.tags);
    }

    @Override
    public boolean equals(PField other)
    {
        return super.equals(other)
                && affinities.equals(((PField_CardCategory) other).affinities)
                && colors.equals(((PField_CardCategory) other).colors)
                && rarities.equals(((PField_CardCategory) other).rarities)
                && types.equals(((PField_CardCategory) other).types)
                && tags.equals(((PField_CardCategory) other).tags);
    }

    @Override
    public PField_CardCategory makeCopy()
    {
        return new PField_CardCategory(this);
    }

    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {
        editor.registerPile(groupTypes);
        editor.registerOrigin(origin, origins -> setOrigin(origins.size() > 0 ? origins.get(0) : PCLCardSelection.Manual));
        editor.registerRarity(rarities);
        editor.registerType(types);
        editor.registerAffinity(affinities);
        editor.registerColor(colors);
        editor.registerTag(tags);
        editor.registerCard(cardIDs);
        editor.registerBoolean(PGR.core.strings.cedit_required, PGR.core.strings.cetut_required1, v -> forced = v, forced);
        registerUseParentBoolean(editor);
    }

    public PField_CardCategory addAffinity(PCLAffinity... affinities)
    {
        this.affinities.addAll(Arrays.asList(affinities));
        return this;
    }

    public PField_CardCategory addTag(PCLCardTag... tags)
    {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    public PField_CardCategory setAffinity(PCLAffinity... affinities)
    {
        return setAffinity(Arrays.asList(affinities));
    }

    public PField_CardCategory setAffinity(List<PCLAffinity> affinities)
    {
        this.affinities.clear();
        this.affinities.addAll(affinities);
        return this;
    }

    public PField_CardCategory setColor(AbstractCard.CardColor... types)
    {
        return setColor(Arrays.asList(types));
    }

    public PField_CardCategory setColor(List<AbstractCard.CardColor> types)
    {
        this.colors.clear();
        this.colors.addAll(types);
        return this;
    }

    public PField_CardCategory setRarity(AbstractCard.CardRarity... types)
    {
        return setRarity(Arrays.asList(types));
    }

    public PField_CardCategory setRarity(List<AbstractCard.CardRarity> types)
    {
        this.rarities.clear();
        this.rarities.addAll(types);
        return this;
    }

    public PField_CardCategory setType(AbstractCard.CardType... types)
    {
        return setType(Arrays.asList(types));
    }

    public PField_CardCategory setType(List<AbstractCard.CardType> types)
    {
        this.types.clear();
        this.types.addAll(types);
        return this;
    }

    public PField_CardCategory setTag(PCLCardTag... nt)
    {
        return setTag(Arrays.asList(nt));
    }

    public PField_CardCategory setTag(List<PCLCardTag> nt)
    {
        this.tags.clear();
        this.tags.addAll(nt);
        return this;
    }

    public FuncT1<Boolean, AbstractCard> getFullCardFilter()
    {
        return !cardIDs.isEmpty() ? c -> EUIUtils.any(cardIDs, id -> id.equals(c.cardID)) :
                (c -> (affinities.isEmpty() || GameUtilities.hasAnyAffinity(c, affinities))
                        && (colors.isEmpty() || colors.contains(c.color))
                        && (rarities.isEmpty() || rarities.contains(c.rarity))
                        && (tags.isEmpty() || EUIUtils.any(tags, t -> t.has(c)))
                        && (types.isEmpty() || types.contains(c.type)));
    }

    public String getFullCardAndString(Object value)
    {
        return getFullCardXString(PField::getAffinityAndString, PCLCoreStrings::joinWithAnd, value);
    }

    public String getFullCardAndString()
    {
        return getFullCardAndString(skill.getAmountRawString());
    }

    public String getFullCardOrString(Object value)
    {
        return getFullCardXString(PField::getAffinityOrString, PCLCoreStrings::joinWithOr, value);
    }

    public String getFullCardOrString()
    {
        return getFullCardOrString(skill.getAmountRawString());
    }

    public String getFullCardString()
    {
        return getFullCardString(skill.getAmountRawString());
    }

    public String getFullCardString(Object value)
    {
        return !cardIDs.isEmpty() ? getCardIDOrString() : isRandom() ? PSkill.TEXT.subjects_randomX(getFullCardOrString(value)) : getFullCardOrString(value);
    }

    public String getFullCardStringSingular()
    {
        return !cardIDs.isEmpty() ? getCardIDOrString() : getFullCardOrString(1);
    }

    public final String getFullCardXString(FuncT1<String, ArrayList<PCLAffinity>> affinityFunc, FuncT1<String, ArrayList<String>> joinFunc, Object value)
    {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (!affinities.isEmpty())
        {
            stringsToJoin.add(affinityFunc.invoke(affinities));
        }
        if (!tags.isEmpty())
        {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(tags, tag -> tag.getTip().getTitleOrIcon())));
        }
        if (!colors.isEmpty())
        {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(colors, EUIGameUtils::getColorName)));
        }
        if (!rarities.isEmpty())
        {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(rarities, EUIGameUtils::textForRarity)));
        }
        if (!types.isEmpty())
        {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(types, type -> PCLCoreStrings.plural(GameUtilities.tooltipForType(type), value))));
        }
        else
        {
            stringsToJoin.add(EUIUtils.format(PSkill.TEXT.subjects_cardN, value));
        }

        return EUIUtils.joinStrings(" ", stringsToJoin);
    }

    public String makeFullString(EUITooltip tooltip)
    {
        String tooltipTitle = tooltip.title;
        return skill.useParent ? EUIRM.strings.verbNoun(tooltipTitle, skill.getInheritedString()) :
                !groupTypes.isEmpty() ? TEXT.act_genericFrom(tooltipTitle, skill.getAmountRawOrAllString(), !cardIDs.isEmpty() ? getCardIDOrString(cardIDs) : getFullCardString(), getGroupString())
                        : EUIRM.strings.verbNoun(tooltipTitle, TEXT.subjects_thisObj);
    }

    protected SelectFromPile initializeBasicSelect(FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> action, PCLUseInfo info)
    {
        return skill.getActions().add(createAction(action, info))
                .setFilter(getFullCardFilter())
                .setAnyNumber(!forced);
    }
}
