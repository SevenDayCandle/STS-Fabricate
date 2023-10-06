package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.tooltips.EUIPreview;
import extendedui.ui.tooltips.EUIBlightPreview;
import extendedui.utilities.BlightTier;
import extendedui.utilities.RotatingList;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Blight extends PField_Random {
    public ArrayList<AbstractCard.CardColor> colors = new ArrayList<>();
    public ArrayList<BlightTier> rarities = new ArrayList<>();
    public ArrayList<String> blightIDs = new ArrayList<>();

    public static AbstractBlight getBlight(String id) {
        if (id != null) {
            return BlightHelper.getBlight(id);
        }
        return null;
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Blight
                && blightIDs.equals(((PField_Blight) other).blightIDs)
                && colors.equals(((PField_Blight) other).colors)
                && rarities.equals(((PField_Blight) other).rarities)
                && ((PField_Blight) other).random == random;
    }

    public FuncT1<Boolean, AbstractBlight> getFullBlightFilter() {
        return !blightIDs.isEmpty() ? c -> EUIUtils.any(blightIDs, id -> id.equals(c.blightID)) :
                (c -> (colors.isEmpty() || colors.contains(GameUtilities.getBlightColor(c.blightID)))
                        && (rarities.isEmpty() || rarities.contains(BlightTier.getTier(c))));
    }

    public String getFullBlightString() {
        return getFullBlightString(skill.getAmountRawString());
    }

    public String getFullBlightString(Object value) {
        return !blightIDs.isEmpty() ? getBlightIDOrString() : random ? PSkill.TEXT.subjects_randomX(getBlightOrString(value)) : getBlightOrString(value);
    }

    public String getFullBlightStringSingular() {
        return !blightIDs.isEmpty() ? getBlightIDOrString() : getBlightXString(PCLCoreStrings::joinWithOr, PCLCoreStrings::singularForce);
    }

    public String getBlightAndString() {
        return getBlightAndString(skill.getAmountRawString());
    }

    public String getBlightAndString(Object value) {
        return getBlightXString(PCLCoreStrings::joinWithAnd, (s) -> EUIUtils.format(s, value));
    }

    public String getBlightIDAndString() {
        return getBlightIDAndString(blightIDs);
    }

    public String getBlightIDOrString() {
        return getBlightIDOrString(blightIDs);
    }

    public String getBlightOrString() {
        return getBlightOrString(skill.getAmountRawString());
    }

    public String getBlightOrString(Object value) {
        return getBlightXString(PCLCoreStrings::joinWithOr, (s) -> EUIUtils.format(s, value));
    }

    public final String getBlightXString(FuncT1<String, ArrayList<String>> joinFunc, FuncT1<String, String> pluralFunc) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (!colors.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(colors, EUIGameUtils::getColorName)));
        }
        if (!rarities.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(rarities, BlightTier::getName)));
        }
        stringsToJoin.add(pluralFunc.invoke(PSkill.TEXT.subjects_blightN));

        return EUIUtils.joinStrings(" ", stringsToJoin);
    }

    public boolean isFilterEmpty() {
        return blightIDs.isEmpty() && colors.isEmpty() && rarities.isEmpty();
    }

    @Override
    public PField_Blight makeCopy() {
        return (PField_Blight) new PField_Blight()
                .setBlightID(blightIDs)
                .setColor(colors)
                .setRarity(rarities)
                .setRandom(random)
                .setNot(not);
    }

    public void makePreviews(RotatingList<EUIPreview> previews) {
        for (String cd : blightIDs) {
            AbstractBlight c = getBlight(cd);
            if (c != null && !EUIUtils.any(previews, p -> p.matches(c.blightID))) {
                // getBlight always returns a copy
                previews.add(new EUIBlightPreview(c));
            }
        }
    }

    public PField_Blight setColor(Collection<AbstractCard.CardColor> types) {
        this.colors.clear();
        this.colors.addAll(types);
        return this;
    }

    public PField_Blight setColor(AbstractCard.CardColor... types) {
        return setColor(Arrays.asList(types));
    }

    public PField_Blight setRarity(Collection<BlightTier> types) {
        this.rarities.clear();
        this.rarities.addAll(types);
        return this;
    }

    public PField_Blight setRarity(BlightTier... types) {
        return setRarity(Arrays.asList(types));
    }

    public PField_Blight setBlightID(Collection<String> orbs) {
        this.blightIDs.clear();
        this.blightIDs.addAll(orbs);
        return this;
    }

    public PField_Blight setBlightID(String... orbs) {
        return setBlightID(Arrays.asList(orbs));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerColor(colors);
        editor.registerBlightRarity(rarities);
        editor.registerBlight(blightIDs);
    }
}
