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
import extendedui.utilities.RotatingList;
import extendedui.utilities.panels.card.CostFilter;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.fields.CardFlag;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
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
    public ArrayList<String> loadouts = new ArrayList<>();
    public ArrayList<String> flags = new ArrayList<>();
    public boolean invert;
    public boolean partial;

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
        setLoadout(other.loadouts);
        setFlag(other.flags);
        setInvert(other.invert);
        setPartial(other.partial);
    }

    public static boolean checkForFlag(String loadout, AbstractCard card) {
        CardFlag l = CardFlag.get(loadout);
        return l != null && l.has(card);
    }

    public static boolean checkForLoadout(String loadout, AbstractCard card) {
        PCLLoadout l = PCLLoadout.get(loadout);
        return l != null && l.isCardFromLoadout(card);
    }

    public static AbstractCard getCard(String id) {
        if (id != null) {
            return CardLibrary.getCard(id);
        }
        return null;
    }

    public static String getFlagName(String loadout) {
        CardFlag l = CardFlag.get(loadout);
        return l != null ? l.getName() : loadout;
    }

    public static String getLoadoutName(String loadout) {
        PCLLoadout l = PCLLoadout.get(loadout);
        return l != null ? l.getName() : loadout;
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
                && costs.equals(((PField_CardCategory) other).costs)
                && loadouts.equals(((PField_CardCategory) other).loadouts)
                && flags.equals(((PField_CardCategory) other).flags)
                && invert == ((PField_CardCategory) other).invert
                && partial == ((PField_CardCategory) other).partial;
    }

    /* Filter cards without upgrade filtering; use if you need to use the upgrade filter for something else (e.g. generating cards with upgrades) */
    public FuncT1<Boolean, AbstractCard> getBaseCardFilter() {
        return (c -> invert ^ (
                        (cardIDs.isEmpty() || EUIUtils.any(cardIDs, id -> id.equals(c.cardID)))
                        && (affinities.isEmpty() || GameUtilities.hasAnyAffinity(c, affinities))
                        && (colors.isEmpty() || colors.contains(c.color))
                        && (costs.isEmpty() || EUIUtils.any(costs, cost -> cost.check(c)))
                        && (loadouts.isEmpty() || EUIUtils.any(loadouts, loadout -> checkForLoadout(loadout, c)))
                        && (flags.isEmpty() || EUIUtils.any(flags, loadout -> checkForFlag(loadout, c)))
                        && (rarities.isEmpty() || rarities.contains(c.rarity))
                        && (tags.isEmpty() || EUIUtils.any(tags, t -> t.has(c)))
                        && (types.isEmpty() || types.contains(c.type))));
    }

    public FuncT1<Boolean, AbstractCard> getBasePartialCardFilter() {
        return (c -> invert ^ (
                EUIUtils.any(cardIDs, id -> id.equals(c.cardID))
                || GameUtilities.hasAnyAffinity(c, affinities)
                || colors.contains(c.color)
                || EUIUtils.any(costs, cost -> cost.check(c))
                || EUIUtils.any(loadouts, loadout -> checkForLoadout(loadout, c))
                || EUIUtils.any(flags, loadout -> checkForFlag(loadout, c))
                || rarities.contains(c.rarity)
                || EUIUtils.any(tags, t -> t.has(c))
                || types.contains(c.type))
        );
    }

    public String getCardAndString(Object requestor) {
        return getCardAndStringForValue(skill.getAmountRawString(requestor));
    }

    public String getCardAndStringForValue(Object value) {
        return getCardXString(PField::getAffinityAndString, PCLCoreStrings::joinWithAnd, (s) -> EUIUtils.format(s, value));
    }

    public String getCardIDAndString() {
        return getCardIDAndString(cardIDs, skill.extra2, 0);
    }

    public String getCardIDOrString() {
        return getCardIDOrString(cardIDs, skill.extra2, 0);
    }

    public String getCardOrString(Object requestor) {
        return getCardOrStringForValue(skill.getAmountRawString(requestor));
    }

    public String getCardOrStringForValue(Object value) {
        return getCardXString(PField::getAffinityOrString, PCLCoreStrings::joinWithOr, (s) -> EUIUtils.format(s, value));
    }

    public final String getCardXPrefixString(FuncT1<String, ArrayList<PCLAffinity>> affinityFunc, FuncT1<String, ArrayList<String>> joinFunc) {
        ArrayList<String> stringsToJoin = getCardXPrefixes(affinityFunc, joinFunc);
        if (!types.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(types, EUIGameUtils::textForType)));
            return partial ? PCLCoreStrings.joinWithOr(stringsToJoin) : EUIUtils.joinStrings(" ", stringsToJoin);
        }
        return stringsToJoin.isEmpty() ? PSkill.TEXT.subjects_any :
                partial ? PCLCoreStrings.joinWithOr(stringsToJoin) : EUIUtils.joinStrings(" ", stringsToJoin);
    }

    protected final ArrayList<String> getCardXPrefixes(FuncT1<String, ArrayList<PCLAffinity>> affinityFunc, FuncT1<String, ArrayList<String>> joinFunc) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (skill.extra2 > 0) {
            stringsToJoin.add(PGR.core.tooltips.upgrade.past());
        }
        if (!costs.isEmpty()) {
            stringsToJoin.add(PGR.core.strings.subjects_xCost(joinFunc.invoke(CostFilter.getCostRangeStrings(costs))));
        }
        if (!rarities.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(rarities, EUIGameUtils::textForRarity)));
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
        if (!loadouts.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.mapAsNonnull(loadouts, PField_CardCategory::getLoadoutName)));
        }
        if (!flags.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.mapAsNonnull(flags, PField_CardCategory::getFlagName)));
        }
        // Will only be reached when invert is active, which always warrants or
        if (!cardIDs.isEmpty()) {
            stringsToJoin.add(getCardIDOrString());
        }
        return stringsToJoin;
    }

    public final String getCardXString(FuncT1<String, ArrayList<PCLAffinity>> affinityFunc, FuncT1<String, ArrayList<String>> joinFunc, FuncT1<String, String> pluralFunc) {
        ArrayList<String> stringsToJoin = getCardXPrefixes(affinityFunc, joinFunc);
        if (!types.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(types, type -> pluralFunc.invoke(GameUtilities.tooltipForType(type).plural()))));
            return partial ? PCLCoreStrings.joinWithOr(stringsToJoin) : EUIUtils.joinStrings(" ", stringsToJoin);
        }
        else {
            if (partial) {
                return PCLCoreStrings.joinWithOr(stringsToJoin) + " " + pluralFunc.invoke(PSkill.TEXT.subjects_cardN);
            }
            stringsToJoin.add(pluralFunc.invoke(PSkill.TEXT.subjects_cardN));
            return EUIUtils.joinStrings(" ", stringsToJoin);
        }
    }

    public String getFullCardAndString(Object value) {
        String sub = targetsSpecificCards() ? getCardIDAndString() : isRandom() ? PSkill.TEXT.subjects_randomX(getCardOrStringForValue(value)) : getCardAndStringForValue(value);
        return invert ? PSkill.TEXT.subjects_non(sub) : sub;
    }

    /* Filter cards based on the base field filters and their upgrades. Use this unless you require the upgrades field for a different purpose (e.g. generating cards), in which case you use getBaseCardFilter */
    public FuncT1<Boolean, AbstractCard> getFullCardFilter() {
        return getFullCardFilter(skill.extra2);
    }

    public FuncT1<Boolean, AbstractCard> getFullCardFilter(int upgrades) {
        if (partial) {
            if (upgrades > 0) {
                return c -> getBasePartialCardFilter().invoke(c) || (invert ^ c.timesUpgraded >= upgrades);
            }
            return getBasePartialCardFilter();
        }
        if (upgrades > 0) {
            return c -> getBaseCardFilter().invoke(c) && (invert ^ c.timesUpgraded >= upgrades);
        }
        return getBaseCardFilter();
    }

    public String getFullCardOrString(Object value) {
        String sub = targetsSpecificCards() ? getCardIDOrString() : isRandom() ? PSkill.TEXT.subjects_randomX(getCardOrStringForValue(value)) : getCardOrStringForValue(value);
        return invert ? PSkill.TEXT.subjects_non(sub) : sub;
    }

    public String getFullCardString(Object requestor) {
        return getFullCardOrString(skill.getAmountRawString(requestor));
    }

    public String getFullCardStringForValue(Object parse) {
        return getFullCardOrString(parse);
    }

    public String getFullCardStringSingular() {
        String sub = targetsSpecificCards() ? getCardIDOrString() : getCardXString(PField::getAffinityOrString, PCLCoreStrings::joinWithOr, PCLCoreStrings::singularForce);
        return invert ? PSkill.TEXT.subjects_non(sub) : sub;
    }

    public String getFullSummonStringSingular() {
        String sub = targetsSpecificCards() ? getCardIDOrString() : getCardXString(PField::getAffinityOrString, PCLCoreStrings::joinWithOr, (__) -> PGR.core.tooltips.summon.title);
        return invert ? PSkill.TEXT.subjects_non(sub) : sub;
    }

    public int getQualifierRange() {
        return EUIUtils.max(EUIUtils.array(costs, affinities, tags, colors, rarities, types, cardIDs), List::size);
    }

    public String getQualifierText(int i) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (costs.size() > i) {
            stringsToJoin.add(PGR.core.strings.subjects_xCost(costs.get(i)));
        }
        if (rarities.size() > i) {
            stringsToJoin.add(EUIGameUtils.textForRarity(rarities.get(i)));
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
        if (loadouts.size() > i) {
            stringsToJoin.add(getLoadoutName(loadouts.get(i)));
        }
        if (flags.size() > i) {
            stringsToJoin.add(getFlagName(flags.get(i)));
        }
        if (types.size() > i) {
            stringsToJoin.add(EUIGameUtils.textForType(types.get(i)));
        }
        if (cardIDs.size() > i) {
            stringsToJoin.add(GameUtilities.getCardNameForID(cardIDs.get(i), skill.extra2, 0));
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

    public boolean isFilterEmpty() {
        return cardIDs.isEmpty() && colors.isEmpty() && rarities.isEmpty() && types.isEmpty() && affinities.isEmpty() && tags.isEmpty() && costs.isEmpty() && loadouts.isEmpty() && flags.isEmpty();
    }

    public boolean isFilterSolo() {
        return !isFilterEmpty() && cardIDs.size() <= 1 && colors.size() <= 1 && rarities.size() <= 1 && types.size() <= 1 && affinities.size() <= 1 && tags.size() <= 1 && costs.size() <= 1 && loadouts.size() <= 1 && flags.size() <= 1;
    }

    @Override
    public PField_CardCategory makeCopy() {
        return new PField_CardCategory(this);
    }

    public void makePreviews(RotatingList<EUIPreview> previews) {
        for (String cd : cardIDs) {
            AbstractCard c = getCard(cd);
            if (c != null && !EUIUtils.any(previews, p -> p.matches(c.cardID))) {
                AbstractCard copy = c.makeCopy();
                for (int i = 0; i < skill.extra2; i++) {
                    copy.upgrade();
                }
                previews.add(new EUICardPreview(copy, false));
            }
        }
    }

    public PField_CardCategory setAffinity(Collection<PCLAffinity> nt) {
        this.affinities.clear();
        for (PCLAffinity t : nt) {
            if (t != null) {
                this.affinities.add(t);
            }
        }
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

    public PField_CardCategory setColor(Collection<AbstractCard.CardColor> nt) {
        this.colors.clear();
        for (AbstractCard.CardColor t : nt) {
            if (t != null) {
                this.colors.add(t);
            }
        }
        return this;
    }

    public PField_CardCategory setColor(AbstractCard.CardColor... types) {
        return setColor(Arrays.asList(types));
    }

    public PField_CardCategory setCost(Collection<CostFilter> nt) {
        this.costs.clear();
        for (CostFilter t : nt) {
            if (t != null) {
                this.costs.add(t);
            }
        }
        return this;
    }

    public PField_CardCategory setCost(CostFilter... types) {
        return setCost(Arrays.asList(types));
    }

    public PField_CardCategory setFlag(Collection<String> nt) {
        this.flags.clear();
        this.flags.addAll(nt);
        return this;
    }

    public PField_CardCategory setFlag(String... nt) {
        return setLoadout(Arrays.asList(nt));
    }

    public PField_CardCategory setFlag(CardFlag... nt) {
        return setLoadout(EUIUtils.map(nt, l -> l.ID));
    }

    public PField_CardCategory setInvert(boolean val) {
        this.invert = val;
        return this;
    }

    public PField_CardCategory setLoadout(Collection<String> nt) {
        this.loadouts.clear();
        this.loadouts.addAll(nt);
        return this;
    }

    public PField_CardCategory setLoadout(String... nt) {
        return setLoadout(Arrays.asList(nt));
    }

    public PField_CardCategory setLoadout(PCLLoadout... nt) {
        return setLoadout(EUIUtils.map(nt, l -> l.ID));
    }

    public PField_CardCategory setPartial(boolean val) {
        this.partial = val;
        return this;
    }

    public PField_CardCategory setRarity(Collection<AbstractCard.CardRarity> nt) {
        this.rarities.clear();
        for (AbstractCard.CardRarity t : nt) {
            if (t != null) {
                this.rarities.add(t);
            }
        }
        return this;
    }

    public PField_CardCategory setRarity(AbstractCard.CardRarity... types) {
        return setRarity(Arrays.asList(types));
    }

    public PField_CardCategory setTag(Collection<PCLCardTag> nt) {
        this.tags.clear();
        for (PCLCardTag t : nt) {
            if (t != null) {
                this.tags.add(t);
            }
        }
        return this;
    }

    public PField_CardCategory setTag(PCLCardTag... nt) {
        return setTag(Arrays.asList(nt));
    }

    public PField_CardCategory setType(Collection<AbstractCard.CardType> nt) {
        this.types.clear();
        for (AbstractCard.CardType t : nt) {
            if (t != null) {
                this.types.add(t);
            }
        }
        return this;
    }

    public PField_CardCategory setType(AbstractCard.CardType... types) {
        return setType(Arrays.asList(types));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        setupEditorFilters(editor);
    }

    protected void setupEditorBase(PCLCustomEffectEditingPane editor) {
        editor.registerOrigin(origin, origins -> setOrigin(!origins.isEmpty() ? origins.get(0) : PCLCardSelection.Manual));
        editor.registerDestination(destination, destinations -> setDestination(!destinations.isEmpty() ? destinations.get(0) : PCLCardSelection.Manual));
        editor.registerPile(groupTypes);
    }

    protected void setupEditorFilters(PCLCustomEffectEditingPane editor) {
        editor.registerRarity(rarities);
        editor.registerType(types);
        editor.registerCost(costs);
        editor.registerColor(colors);
        editor.registerAffinity(affinities);
        editor.registerTag(tags);
        editor.registerLoadout(loadouts);
        editor.registerFlag(flags);
        editor.registerCard(cardIDs);
        editor.registerBoolean(PSkill.TEXT.cedit_invert, v -> invert = v, invert);
        editor.registerBoolean(PSkill.TEXT.cedit_partial, v -> partial = v, partial);
    }

    public boolean targetsSpecificCards() {
        return !cardIDs.isEmpty() && !invert;
    }
}
