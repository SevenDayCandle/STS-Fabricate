package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PField_Affinity extends PField_Random {
    public ArrayList<PCLAffinity> affinities = new ArrayList<>();

    public PField_Affinity addAffinity(PCLAffinity... affinities) {
        this.affinities.addAll(Arrays.asList(affinities));
        return this;
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Affinity
                && affinities.equals(((PField_Affinity) other).affinities)
                && ((PField_Affinity) other).random == random
                && ((PField_Affinity) other).not == not;
    }

    public String getAffinityAndOrString() {
        return getAffinityAndOrString(affinities, random);
    }

    public String getAffinityAndString() {
        return getAffinityAndString(affinities);
    }

    public String getAffinityChoiceString() {
        return affinities.isEmpty() ? TEXT.subjects_anyX(getGeneralAffinityString()) : getAffinityOrString(affinities);
    }

    public String getAffinityOrString() {
        return getAffinityOrString(affinities);
    }

    public String getAffinityString() {
        return getAffinityString(affinities);
    }

    public AbstractCard.CardColor getColor() {
        return skill != null && skill.source instanceof AbstractCard ? ((AbstractCard) skill.source).color : GameUtilities.getActingColor();
    }

    public int getQualifierRange() {
        return affinities.size();
    }

    public String getQualifierText(int i) {
        return i < affinities.size() ? affinities.get(i).getTooltip().getTitleOrIcon() : "";
    }

    public ArrayList<Integer> getQualifiers(PCLUseInfo info) {
        List<? extends PCLAffinity> list = info.getDataAsList(PCLAffinity.class);
        ArrayList<Integer> indexes = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < affinities.size(); i++) {
                if (list.contains(affinities.get(i))) {
                    indexes.add(i);
                }
            }
        }
        else {
            PCLAffinity item = info.getData(PCLAffinity.class);
            if (item != null) {
                for (int i = 0; i < affinities.size(); i++) {
                    if (item == (affinities.get(i))) {
                        indexes.add(i);
                        return indexes;
                    }
                }
            }
        }
        return indexes;
    }

    @Override
    public PField_Affinity makeCopy() {
        return (PField_Affinity) new PField_Affinity().setAffinity(affinities).setRandom(random).setNot(not);
    }

    public PField_Affinity setAffinity(Collection<PCLAffinity> nt) {
        this.affinities.clear();
        for (PCLAffinity t : nt) {
            if (t != null) {
                this.affinities.add(t);
            }
        }
        return this;
    }

    public PField_Affinity setAffinity(PCLAffinity... affinities) {
        return setAffinity(Arrays.asList(affinities));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerAffinity(affinities);
        super.setupEditor(editor);
    }
}
