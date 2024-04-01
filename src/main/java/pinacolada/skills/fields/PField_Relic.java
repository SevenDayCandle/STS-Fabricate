package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUIRelicPreview;
import extendedui.utilities.RotatingList;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PField_Relic extends PField_Random {
    public ArrayList<AbstractCard.CardColor> colors = new ArrayList<>();
    public ArrayList<AbstractRelic.RelicTier> rarities = new ArrayList<>();
    public ArrayList<String> relicIDs = new ArrayList<>();

    public static AbstractRelic getRelic(String id) {
        if (id != null) {
            return RelicLibrary.getRelic(id);
        }
        return null;
    }

    public void addRelicTips(KeywordProvider card) {
        List<EUIKeywordTooltip> tips = card.getTips();
        if (tips != null) {
            for (String r : relicIDs) {
                AbstractRelic relic = RelicLibrary.getRelic(r);
                if (relic != null) {
                    tips.add(new EUIKeywordTooltip(relic.name, relic.description));
                }
            }
        }
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Relic
                && relicIDs.equals(((PField_Relic) other).relicIDs)
                && colors.equals(((PField_Relic) other).colors)
                && rarities.equals(((PField_Relic) other).rarities)
                && ((PField_Relic) other).random == random;
    }

    public FuncT1<Boolean, AbstractRelic> getFullRelicFilter() {
        return !relicIDs.isEmpty() ? c -> EUIUtils.any(relicIDs, id -> id.equals(c.relicId)) :
                (c -> (colors.isEmpty() || colors.contains(EUIGameUtils.getRelicColor(c.relicId)))
                        && (rarities.isEmpty() || rarities.contains(c.tier)));
    }

    public String getFullRelicString(Object requestor) {
        return getFullRelicStringForAmount(skill.getAmountRawString(requestor));
    }

    public String getFullRelicStringForAmount(Object value) {
        return !relicIDs.isEmpty() ? getRelicIDOrString() : random ? PSkill.TEXT.subjects_randomX(getRelicOrString(value)) : getRelicOrString(value);
    }

    public String getFullRelicStringSingular() {
        return !relicIDs.isEmpty() ? getRelicIDOrString() : getRelicXString(PCLCoreStrings::joinWithOr, PCLCoreStrings::singularForce);
    }

    public String getRelicAndString(Object value) {
        return getRelicXString(PCLCoreStrings::joinWithAnd, (s) -> EUIUtils.format(s, value));
    }

    public String getRelicIDAndString() {
        return getRelicIDAndString(relicIDs);
    }

    public String getRelicIDOrString() {
        return getRelicIDOrString(relicIDs);
    }

    public String getRelicOrString(Object value) {
        return getRelicXString(PCLCoreStrings::joinWithOr, (s) -> EUIUtils.format(s, value));
    }

    public final String getRelicXString(FuncT1<String, ArrayList<String>> joinFunc, FuncT1<String, String> pluralFunc) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (!colors.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(colors, EUIGameUtils::getColorName)));
        }
        if (!rarities.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(rarities, EUIGameUtils::textForRelicTier)));
        }
        stringsToJoin.add(pluralFunc.invoke(PSkill.TEXT.subjects_relicN));

        return EUIUtils.joinStrings(" ", stringsToJoin);
    }

    public boolean isFilterEmpty() {
        return relicIDs.isEmpty() && colors.isEmpty() && rarities.isEmpty();
    }

    @Override
    public PField_Relic makeCopy() {
        return (PField_Relic) new PField_Relic()
                .setRelicID(relicIDs)
                .setColor(colors)
                .setRarity(rarities)
                .setRandom(random)
                .setNot(not);
    }

    public void makePreviews(RotatingList<EUIPreview> previews) {
        for (String cd : relicIDs) {
            AbstractRelic c = getRelic(cd);
            if (c != null && !EUIUtils.any(previews, p -> p.matches(c.relicId))) {
                previews.add(new EUIRelicPreview(c.makeCopy()));
            }
        }
    }

    public PField_Relic setColor(Collection<AbstractCard.CardColor> types) {
        this.colors.clear();
        this.colors.addAll(types);
        return this;
    }

    public PField_Relic setColor(AbstractCard.CardColor... types) {
        return setColor(Arrays.asList(types));
    }

    public PField_Relic setRarity(Collection<AbstractRelic.RelicTier> types) {
        this.rarities.clear();
        this.rarities.addAll(types);
        return this;
    }

    public PField_Relic setRarity(AbstractRelic.RelicTier... types) {
        return setRarity(Arrays.asList(types));
    }

    public PField_Relic setRelicID(Collection<String> orbs) {
        this.relicIDs.clear();
        this.relicIDs.addAll(orbs);
        return this;
    }

    public PField_Relic setRelicID(String... orbs) {
        return setRelicID(Arrays.asList(orbs));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerColor(colors);
        editor.registerRelicRarity(rarities);
        editor.registerRelic(relicIDs);
    }
}
