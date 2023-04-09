package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_Affinity extends PField_Random
{
    public ArrayList<PCLAffinity> affinities = new ArrayList<>();

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_Affinity
                && affinities.equals(((PField_Affinity) other).affinities)
                && ((PField_Affinity) other).random == random
                && ((PField_Affinity) other).not == not;
    }

    @Override
    public PField_Affinity makeCopy()
    {
        return (PField_Affinity) new PField_Affinity().setAffinity(affinities).setRandom(random).setNot(not);
    }

    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {
        editor.registerAffinity(affinities);
        super.setupEditor(editor);
    }

    public PField_Affinity addAffinity(PCLAffinity... affinities)
    {
        this.affinities.addAll(Arrays.asList(affinities));
        return this;
    }

    public PField_Affinity setAffinity(PCLAffinity... affinities)
    {
        return setAffinity(Arrays.asList(affinities));
    }

    public PField_Affinity setAffinity(List<PCLAffinity> affinities)
    {
        this.affinities.clear();
        this.affinities.addAll(affinities);
        return this;
    }

    public String getAffinityChoiceString()
    {
        return affinities.isEmpty() ? TEXT.subjects_anyX(PGR.core.tooltips.affinityGeneral) : getAffinityLevelOrString(getColor(), affinities);
    }

    public String getAffinityAndString()
    {
        return getAffinityAndString(affinities);
    }

    public String getAffinityAndOrString()
    {
        return getAffinityAndOrString(affinities, random);
    }

    public String getAffinityLevelAndString()
    {
        return getAffinityLevelAndString(getColor(), affinities);
    }

    public String getAffinityLevelOrString()
    {
        return getAffinityLevelOrString(getColor(), affinities);
    }

    public String getAffinityLevelAndOrString()
    {
        return getAffinityLevelAndOrString(getColor(), affinities, random);
    }

    public String getAffinityOrString()
    {
        return getAffinityOrString(affinities);
    }

    public String getAffinityPowerAndString()
    {
        return getAffinityPowerAndString(affinities);
    }

    public String getAffinityPowerOrString()
    {
        return getAffinityPowerOrString(affinities);
    }

    public String getAffinityPowerString()
    {
        return getAffinityPowerString(affinities);
    }

    public String getAffinityString()
    {
        return getAffinityString(affinities);
    }

    public AbstractCard.CardColor getColor()
    {
        return skill != null && skill.sourceCard != null ? skill.sourceCard.color : GameUtilities.getActingColor();
    }
}
