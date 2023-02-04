package pinacolada.skills.fields;

import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_Affinity extends PField_Random
{
    public ArrayList<PCLAffinity> affinities = new ArrayList<>();

    @Override
    public boolean equals(PField other)
    {
        return super.equals(other) && affinities.equals(((PField_Affinity) other).affinities) && ((PField_Affinity) other).random == random;
    }

    @Override
    public PField_Affinity makeCopy()
    {
        return new PField_Affinity().setAffinity(affinities).setRandom(random);
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

    public PField_Affinity setRandom(boolean random)
    {
        this.random = random;
        return this;
    }

    public String getAffinityChoiceString()
    {
        return affinities.isEmpty() ? TEXT.subjects_anyX(PGR.core.tooltips.affinityGeneral) : getAffinityLevelOrString(affinities);
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
        return getAffinityLevelAndString(affinities);
    }

    public String getAffinityLevelOrString()
    {
        return getAffinityLevelOrString(affinities);
    }

    public String getAffinityLevelAndOrString()
    {
        return getAffinityLevelAndOrString(affinities, random);
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
}
