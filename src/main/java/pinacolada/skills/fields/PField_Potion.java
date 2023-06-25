package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Potion extends PField_Random {
    public ArrayList<AbstractCard.CardColor> colors = new ArrayList<>();
    public ArrayList<AbstractPotion.PotionRarity> rarities = new ArrayList<>();
    public ArrayList<AbstractPotion.PotionSize> sizes = new ArrayList<>();
    public ArrayList<String> potionIDs = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Potion
                && potionIDs.equals(((PField_Potion) other).potionIDs)
                && colors.equals(((PField_Potion) other).colors)
                && rarities.equals(((PField_Potion) other).rarities)
                && sizes.equals(((PField_Potion) other).sizes)
                && ((PField_Potion) other).random == random;
    }

    @Override
    public PField_Potion makeCopy() {
        return (PField_Potion) new PField_Potion()
                .setPotionID(potionIDs)
                .setColor(colors)
                .setRarity(rarities)
                .setSize(sizes)
                .setRandom(random);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerColor(colors);
        editor.registerPotionRarity(rarities);
        editor.registerPotionSize(sizes);
        editor.registerPotion(potionIDs);
    }

    public FuncT1<Boolean, AbstractPotion> getFullPotionFilter() {
        return !potionIDs.isEmpty() ? c -> EUIUtils.any(potionIDs, id -> id.equals(c.ID)) :
                (c -> (colors.isEmpty() || colors.contains(EUIGameUtils.getPotionColor(c.ID)))
                        && (rarities.isEmpty() || rarities.contains(c.rarity))
                        && (sizes.isEmpty() || sizes.contains(c.size))
                );
    }

    public String getFullPotionString() {
        return getFullPotionString(skill.getAmountRawString());
    }

    public String getFullPotionString(Object value) {
        return !potionIDs.isEmpty() ? getPotionIDOrString() : random ? PSkill.TEXT.subjects_randomX(getPotionOrString(value)) : getPotionOrString(value);
    }

    public String getFullPotionStringSingular() {
        return !potionIDs.isEmpty() ? getPotionIDOrString() : getPotionXString(PCLCoreStrings::joinWithOr, PCLCoreStrings::singularForce);
    }

    public String getPotionAndString() {
        return getPotionAndString(skill.getAmountRawString());
    }

    public String getPotionAndString(Object value) {
        return getPotionXString(PCLCoreStrings::joinWithAnd, (s) -> EUIUtils.format(s, value));
    }

    public String getPotionIDAndString() {
        return getPotionIDAndString(potionIDs);
    }

    public String getPotionIDOrString() {
        return getPotionIDOrString(potionIDs);
    }

    public String getPotionOrString() {
        return getPotionOrString(skill.getAmountRawString());
    }

    public String getPotionOrString(Object value) {
        return getPotionXString(PCLCoreStrings::joinWithOr, (s) -> EUIUtils.format(s, value));
    }

    public final String getPotionXString(FuncT1<String, ArrayList<String>> joinFunc, FuncT1<String, String> pluralFunc) {
        ArrayList<String> stringsToJoin = new ArrayList<>();
        if (!colors.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(colors, EUIGameUtils::getColorName)));
        }
        if (!rarities.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(rarities, EUIGameUtils::textForPotionRarity)));
        }
        if (!sizes.isEmpty()) {
            stringsToJoin.add(joinFunc.invoke(EUIUtils.map(sizes, EUIGameUtils::textForPotionSize)));
        }
        stringsToJoin.add(pluralFunc.invoke(PSkill.TEXT.subjects_potionN));

        return EUIUtils.joinStrings(" ", stringsToJoin);
    }

    public PField_Potion setColor(Collection<AbstractCard.CardColor> types) {
        this.colors.clear();
        this.colors.addAll(types);
        return this;
    }

    public PField_Potion setColor(AbstractCard.CardColor... types) {
        return setColor(Arrays.asList(types));
    }

    public PField_Potion setPotionID(Collection<String> orbs) {
        this.potionIDs.clear();
        this.potionIDs.addAll(orbs);
        return this;
    }

    public PField_Potion setPotionID(String... orbs) {
        return setPotionID(Arrays.asList(orbs));
    }

    public PField_Potion setRarity(Collection<AbstractPotion.PotionRarity> types) {
        this.rarities.clear();
        this.rarities.addAll(types);
        return this;
    }

    public PField_Potion setRarity(AbstractPotion.PotionRarity... types) {
        return setRarity(Arrays.asList(types));
    }

    public PField_Potion setSize(AbstractPotion.PotionSize... types) {
        return setSize(Arrays.asList(types));
    }

    public PField_Potion setSize(Collection<AbstractPotion.PotionSize> types) {
        this.sizes.clear();
        this.sizes.addAll(types);
        return this;
    }
}
