package pinacolada.cards.base;

import extendedui.EUIRM;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.pcl.tokens.AffinityToken;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_Affinity;
import pinacolada.skills.fields.PField_CardModifyAffinity;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class ChoiceData<T> extends PCLDynamicData
{
    public final boolean fromCustom;
    public final T object;

    public static ChoiceData<PCLAffinity> affinity(PCLAffinity affinity)
    {
        ChoiceData<PCLAffinity> builder = new ChoiceData<PCLAffinity>(AffinityToken.getCardData(affinity), affinity);
        String img = AffinityToken.getCardData(affinity).getImagePath(GameUtilities.getActingColor());
        if (img != null)
        {
            builder.portraitForeground = new ColoredTexture(EUIRM.getTexture(img, true));
        }
        builder.portraitImage = new ColoredTexture(EUIRM.getTexture(PGR.getCardImage(builder.ID), true), affinity.getAlternateColor(0.55f));
        builder.imagePath = AffinityToken.backgroundPath();
        return builder;
    }

    public static ChoiceData<PCLAffinity> skillAffinity(PSkill<?> skill, PCLAffinity affinity)
    {
        return (ChoiceData<PCLAffinity>) ChoiceData.affinity(affinity)
                .addPSkill(skill)
                .setTarget(skill.target);
    }


    public static ChoiceData<PCLAffinity> skillAffinity(PSkill<?> skill)
    {
        PCLAffinity affinity = PCLAffinity.Star;
        PField fields = skill.fields;
        if (fields instanceof PField_Affinity)
        {
            List<PCLAffinity> affinities = ((PField_Affinity) fields).affinities;
            if (affinities.size() > 0)
            {
                affinity = affinities.get(0);
            }
        }
        else if (fields instanceof PField_CardModifyAffinity)
        {
            List<PCLAffinity> affinities = ((PField_CardModifyAffinity) fields).addAffinities;
            if (affinities.size() > 0)
            {
                affinity = affinities.get(0);
            }
        }
        return skillAffinity(skill, affinity);
    }

    public static ChoiceData<PSkill<?>> skill(PCLCardData card, PSkill<?> skill)
    {
        return (ChoiceData<PSkill<?>>) ChoiceData.create(card, skill)
                .addPSkill(skill)
                .setTarget(skill.target);
    }

    public static <T> ChoiceData<T> create(PCLCardData card, T object)
    {
        return new ChoiceData<T>(card, object);
    }

    public static <T> ChoiceCard<T> generate(PCLCardData card, T object)
    {
        return new ChoiceData<T>(card, object).build();
    }

    public static ChoiceCard<PCLAffinity> generateAffinity(PCLAffinity affinity)
    {
        return affinity(affinity).build();
    }

    public ChoiceData(PCLCard card, T object)
    {
        super(card, false);
        this.object = object;
        this.showTypeText = false;
        this.fromCustom = card instanceof PCLDynamicCard;
    }

    public ChoiceData(PCLCardData card, T object)
    {
        super(card, false);
        this.object = object;
        this.showTypeText = false;
        this.fromCustom = card instanceof PCLDynamicData;
    }

    public ChoiceData<T> addPSkill(PSkill<?> effect)
    {
        super.addPSkill(effect, false);
        return this;
    }

    public ChoiceCard<T> build()
    {
        if (strings == null)
        {
            setText("", "", "");
        }

        return new ChoiceCard<T>(this);
    }
}