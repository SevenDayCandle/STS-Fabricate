package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.CostFilter;
import extendedui.utilities.RotatingList;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PField_CardCategory extends PField_CardGeneric {
    public ArrayList<AbstractCard.CardColor> colors = new ArrayList<>();
    public ArrayList<AbstractCard.CardRarity> rarities = new ArrayList<>();
    public ArrayList<AbstractCard.CardType> types = new ArrayList<>();
    public ArrayList<PCLAffinity> affinities = new ArrayList<>();
    public ArrayList<PCLCardTag> tags = new ArrayList<>();
    public ArrayList<CostFilter> costs = new ArrayList<>();
    public ArrayList<String> cardIDs = new ArrayList<>();

    public PField_CardCategory() {
        super();
    }

    public PField_CardCategory(PField_CardCategory other) {
        super(other);
        setAffinity(other.affinities);
        setColor(other.colors);
        setRarity(other.rarities);
        setType(other.types);
        setTag(other.tags);
        setCost(other.costs);
        setCardIDs(other.cardIDs);
    }

    public static AbstractCard getCard(String id) {
        if (id != null) {
            return CardLibrary.getCard(id);
        }
        return null;
    }

    public PField_CardCategory addAffinity(PCLAffinity... affinities) {
        this.affinities.addAll(Arrays.asList(affinities));
        return this;
    }

    public PField_CardCategory addTag(PCLCardTag... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    public SelectFromPile createFilteredAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> action, PCLUseInfo info, int subchoices) {
        return super.createFilteredAction(action, info, subchoices).setFilter(getFullCardFilter());
    }

    public SelectFromPile createFilteredAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> action, PCLUseInfo info, int subchoices, boolean allowSelf) {
        return super.createFilteredAction(action, info, subchoices, allowSelf).setFilter(getFullCardFilter());
    }

    @Override
    public boolean equals(PField other) {
        return super.equals(other)
                && cardIDs.equals(((PField_CardCategory) other).cardIDs)
                && affinities.equals(((PField_CardCategory) other).affinities)
                && colors.equals(((PField_CardCategory) other).colors)
                && rarities.equals(((PField_CardCategory) other).rarities)
                && types.equals(((PField_CardCategory) other).types)
                && tags.equals(((PField_CardCategory) other).tags)
                && costs.equals(((PField_CardCategory) other).costs);
    }

    @Override
    public PField_CardCategory makeCopy() {
        return new PField_CardCategory(this);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerOrigin(origin, origins -> setOrigin(origins.size() > 0 ? origins.get(0) : PCLCardSelection.Manual));
        editor.registerDestination(destination, destinations -> setDestination(destinations.size() > 0 ? destinations.get(0) : PCLCardSelection.Manual));
        editor.registerPile(groupTypes);
        editor.registerRarity(rarities);
        editor.registerType(types);
        editor.registerCost(costs);
        editor.registerColor(colors);
        editor.registerAffinity(affinities);
        editor.registerTag(tags);
        editor.registerCard(cardIDs);
    }

    public String getFullCardString() {
        return getFullCardOrString(skill.getAmountRawString());
    }

    public String getFullCardString(Object parse) {
        return getFullCardOrString(parse);
    }

    public String getFullCardStringSingular() {
        return !cardIDs.isEmpty() ? getCardIDOrString() : getCardXString(PField::getAffinityOrString, PCLCoreStrings::joinWithOr, PCLCoreStrings::singularForce);
    }

    public String getCardAndString() {
        return getCardAndString(skill.getAmountRawString());
    }

    public String getCardAndString(Object value) {
        return getCardXString(PField::getAffinityAndString, PCLCoreStrings::joinWithAnd, (s) -> EUIUtils.format(s, value));
    }

    public String getCardIDAndString() {
        return getCardIDAndString(cardIDs);
    }

    public String getCardIDOrString() {
        return getCardIDOrString(cardIDs);
    }

    public String getCardOrString() {
        return getCardOrString(skill.getAmountRawString());
    }

    public String getCardOrString(Object value) {
        return getCardXString(PField::getAffinityOrString, PCLCoreStrings::joinWithOr, (s) -> EUIUtils.format(s, value));
    }

    public final String getCardXString(FuncT1<String, ArrayList<PCLAffinity>> affinityFunc, FuncT1<String, ArrayList<String>> joinFunc, FuncT1<String, String> pluralFunc) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (!costs.isEmpty()) {
            stringsToJoin.add(PGR.core.strings.subjects_xCost(joinFunc.invoke(EUIUtils.map(costs, c -> c.name))));
        }
        if (!affinities.isEmpty()) {
            stringsToJoin.add(affinityFunc.invoke(affinities));
        }
        if (!tags.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(tags, tag -> tag.getTooltip().getTitleOrIcon())));
        }
        if (!colors.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(colors, EUIGameUtils::getColorName)));
        }
        if (!rarities.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(rarities, EUIGameUtils::textForRarity)));
        }
        if (!types.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(types, type -> pluralFunc.invoke(GameUtilities.tooltipForType(type).plural()))));
        }
        else {
            stringsToJoin.add(pluralFunc.invoke(PSkill.TEXT.subjects_cardN));
        }

        return EUIUtils.joinStrings(" ", stringsToJoin);
    }

    public String getFullCardAndString(Object value) {
        return !cardIDs.isEmpty() ? getCardIDAndString() : isRandom() ? PSkill.TEXT.subjects_randomX(getCardOrString(value)) : getCardAndString(value);
    }

    public FuncT1<Boolean, AbstractCard> getFullCardFilter() {
        return !cardIDs.isEmpty() ? c -> EUIUtils.any(cardIDs, id -> id.equals(c.cardID)) :
                (c -> (affinities.isEmpty() || GameUtilities.hasAnyAffinity(c, affinities))
                        && (colors.isEmpty() || colors.contains(c.color))
                        && (costs.isEmpty() || EUIUtils.any(costs, cost -> cost.check(c)))
                        && (rarities.isEmpty() || rarities.contains(c.rarity))
                        && (tags.isEmpty() || EUIUtils.any(tags, t -> t.has(c)))
                        && (types.isEmpty() || types.contains(c.type)));
    }

    public String getFullCardOrString(Object value) {
        return !cardIDs.isEmpty() ? getCardIDOrString() : isRandom() ? PSkill.TEXT.subjects_randomX(getCardOrString(value)) : getCardOrString(value);
    }

    public String getFullSummonStringSingular() {
        return !cardIDs.isEmpty() ? getCardIDOrString() : getCardXString(PField::getAffinityOrString, PCLCoreStrings::joinWithOr, (__) -> PGR.core.tooltips.summon.title);
    }

    public int getQualifierRange() {
        return EUIUtils.max(EUIUtils.array(costs, affinities, tags, colors, rarities, types, cardIDs), List::size);
    }

    public String getQualifierText(int i) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (costs.size() > i) {
            stringsToJoin.add(PGR.core.strings.subjects_xCost(costs.get(i).name));
        }
        if (affinities.size() > i) {
            stringsToJoin.add(affinities.get(i).getTooltip().toString());
        }
        if (tags.size() > i) {
            stringsToJoin.add(tags.get(i).getTooltip().getTitleOrIcon());
        }
        if (colors.size() > i) {
            stringsToJoin.add(EUIGameUtils.getColorName(colors.get(i)));
        }
        if (rarities.size() > i) {
            stringsToJoin.add(EUIGameUtils.textForRarity(rarities.get(i)));
        }
        if (types.size() > i) {
            stringsToJoin.add(EUIGameUtils.textForType(types.get(i)));
        }

        return stringsToJoin.isEmpty() ? TEXT.subjects_other : EUIUtils.joinStrings(" ", stringsToJoin);
    }

    public ArrayList<Integer> getQualifiers(PCLUseInfo info) {
        List<? extends AbstractCard> cards = info.getDataAsList(AbstractCard.class);
        ArrayList<Integer> indexes = new ArrayList<>();
        if (cards != null) {
            for (AbstractCard c : cards) {
                for (int i = 0; i < affinities.size(); i++) {
                    if (GameUtilities.hasAffinity(c, affinities.get(i))) {
                        indexes.add(i);
                    }
                }
                for (int i = 0; i < cardIDs.size(); i++) {
                    if (c.cardID.equals(cardIDs.get(i))) {
                        indexes.add(i);
                        break;
                    }
                }
                for (int i = 0; i < costs.size(); i++) {
                    if (costs.get(i).check(c)) {
                        indexes.add(i);
                        break;
                    }
                }
                for (int i = 0; i < colors.size(); i++) {
                    if (c.color == colors.get(i)) {
                        indexes.add(i);
                        break;
                    }
                }
                for (int i = 0; i < rarities.size(); i++) {
                    if (c.rarity == rarities.get(i)) {
                        indexes.add(i);
                        break;
                    }
                }
                for (int i = 0; i < tags.size(); i++) {
                    if (tags.get(i).has(c)) {
                        indexes.add(i);
                    }
                }
                for (int i = 0; i < types.size(); i++) {
                    if (c.type == types.get(i)) {
                        indexes.add(i);
                        break;
                    }
                }
            }
        }

        return indexes;
    }

    public void makePreviews(RotatingList<EUIPreview> previews) {
        for (String cd : cardIDs) {
            AbstractCard c = getCard(cd);
            if (c != null && !EUIUtils.any(previews, p -> p.matches(c.cardID))) {
                previews.add(new EUICardPreview(c.makeCopy()));
            }
        }
    }

    public String makeFullString(EUITooltip tooltip) {
        String tooltipTitle = tooltip.title;
        return skill.useParent ? EUIRM.strings.verbNoun(tooltipTitle, skill.getInheritedThemString()) :
                !groupTypes.isEmpty() ? TEXT.act_zXFromY(tooltipTitle, skill.getAmountRawOrAllString(), !cardIDs.isEmpty() ? getCardIDOrString(cardIDs) : getFullCardString(), getGroupString())
                        : EUIRM.strings.verbNoun(tooltipTitle, TEXT.subjects_thisCard);
    }

    public PField_CardCategory setAffinity(Collection<PCLAffinity> affinities) {
        this.affinities.clear();
        this.affinities.addAll(affinities);
        return this;
    }

    public PField_CardCategory setAffinity(PCLAffinity... affinities) {
        return setAffinity(Arrays.asList(affinities));
    }

    public PField_CardCategory setCardIDs(Collection<String> cards) {
        this.cardIDs.clear();
        this.cardIDs.addAll(cards);
        return this;
    }

    public PField_CardCategory setCardIDs(String... cards) {
        return setCardIDs(Arrays.asList(cards));
    }

    public PField_CardCategory setColor(Collection<AbstractCard.CardColor> types) {
        this.colors.clear();
        this.colors.addAll(types);
        return this;
    }

    public PField_CardCategory setColor(AbstractCard.CardColor... types) {
        return setColor(Arrays.asList(types));
    }

    public PField_CardCategory setCost(Collection<CostFilter> types) {
        this.costs.clear();
        this.costs.addAll(types);
        return this;
    }

    public PField_CardCategory setCost(CostFilter... types) {
        return setCost(Arrays.asList(types));
    }

    public PField_CardCategory setRarity(Collection<AbstractCard.CardRarity> types) {
        this.rarities.clear();
        this.rarities.addAll(types);
        return this;
    }

    public PField_CardCategory setRarity(AbstractCard.CardRarity... types) {
        return setRarity(Arrays.asList(types));
    }

    public PField_CardCategory setTag(Collection<PCLCardTag> nt) {
        this.tags.clear();
        this.tags.addAll(nt);
        return this;
    }

    public PField_CardCategory setTag(PCLCardTag... nt) {
        return setTag(Arrays.asList(nt));
    }

    public PField_CardCategory setType(Collection<AbstractCard.CardType> types) {
        this.types.clear();
        this.types.addAll(types);
        return this;
    }

    public PField_CardCategory setType(AbstractCard.CardType... types) {
        return setType(Arrays.asList(types));
    }
}
