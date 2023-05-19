package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Relic extends PField_Random {
    public ArrayList<AbstractCard.CardColor> colors = new ArrayList<>();
    public ArrayList<AbstractRelic.RelicTier> rarities = new ArrayList<>();
    public ArrayList<String> relicIDs = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Relic
                && relicIDs.equals(((PField_Relic) other).relicIDs)
                && colors.equals(((PField_Relic) other).colors)
                && rarities.equals(((PField_Relic) other).rarities)
                && ((PField_Relic) other).random == random;
    }

    @Override
    public PField_Relic makeCopy() {
        return (PField_Relic) new PField_Relic().setRelicID(relicIDs).setRandom(random);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerColor(colors);
        editor.registerRelicRarity(rarities);
        editor.registerRelic(relicIDs);
    }

    public FuncT1<Boolean, AbstractRelic> getFullRelicFilter() {
        return !relicIDs.isEmpty() ? c -> EUIUtils.any(relicIDs, id -> id.equals(c.relicId)) :
                (c -> (colors.isEmpty() || EUIUtils.any(colors, color -> GameUtilities.getRelics(color).containsKey(c.relicId)))
                        && (rarities.isEmpty() || rarities.contains(c.tier)));
    }

    public String getFullRelicString() {
        return getFullRelicString(skill.getAmountRawString());
    }

    public String getFullRelicString(Object value) {
        return !relicIDs.isEmpty() ? getRelicIDOrString() : random ? PSkill.TEXT.subjects_randomX(getRelicOrString(value)) : getRelicOrString(value);
    }

    public String getFullRelicStringSingular() {
        return !relicIDs.isEmpty() ? getRelicIDOrString() : getRelicXString(PCLCoreStrings::joinWithOr, PCLCoreStrings::singularForce);
    }

    public String getRelicAndString() {
        return getRelicAndString(skill.getAmountRawString());
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

    public String getRelicOrString() {
        return getRelicOrString(skill.getAmountRawString());
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
}
