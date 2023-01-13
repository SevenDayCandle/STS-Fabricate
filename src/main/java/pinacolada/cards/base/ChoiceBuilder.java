package pinacolada.cards.base;

import extendedui.EUIRM;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.pcl.tokens.AffinityToken;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_Affinity;
import pinacolada.skills.fields.PField_CardModifyAffinity;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class ChoiceBuilder<T> extends PCLCardBuilder
{
    public final T object;

    public static ChoiceBuilder<PCLAffinity> affinity(PCLAffinity affinity)
    {
        ChoiceBuilder<PCLAffinity> builder = new ChoiceBuilder<PCLAffinity>(AffinityToken.getCardData(affinity), affinity);
        String img = AffinityToken.getCardData(affinity).getImagePath(GameUtilities.getActingColor());
        if (img != null)
        {
            builder.portraitForeground = new ColoredTexture(EUIRM.getTexture(img, true));
        }
        builder.portraitImage = new ColoredTexture(EUIRM.getTexture(PGR.getCardImage(builder.ID), true), affinity.getAlternateColor(0.55f));
        builder.imagePath = AffinityToken.backgroundPath();
        return builder;
    }

    public static ChoiceBuilder<PCLAffinity> skillAffinity(PSkill<?> skill, PCLAffinity affinity)
    {
        return (ChoiceBuilder<PCLAffinity>) ChoiceBuilder.affinity(affinity)
                .addPSkill(skill)
                .setTarget(skill.target);
    }


    public static ChoiceBuilder<PCLAffinity> skillAffinity(PSkill<?> skill)
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

    public static ChoiceBuilder<PSkill<?>> skill(PCLCardData card, PSkill<?> skill)
    {
        return (ChoiceBuilder<PSkill<?>>) ChoiceBuilder.create(card, skill)
                .addPSkill(skill)
                .setTarget(skill.target);
    }

    public static <T> ChoiceBuilder<T> create(PCLCardData card, T object)
    {
        return new ChoiceBuilder<T>(card, object);
    }

    public static <T> ChoiceCard<T> generate(PCLCardData card, T object)
    {
        return new ChoiceBuilder<T>(card, object).buildPCL();
    }

    public static ChoiceCard<PCLAffinity> generateAffinity(PCLAffinity affinity)
    {
        return affinity(affinity).buildPCL();
    }

    public ChoiceBuilder(PCLCard card, T object)
    {
        super(card, false);
        this.object = object;
        this.showTypeText = false;
    }

    public ChoiceBuilder(PCLCardData card, T object)
    {
        super(card, false);
        this.object = object;
        this.showTypeText = false;
    }

    public ChoiceBuilder<T> addPSkill(PSkill effect)
    {
        super.addPSkill(effect, false);
        return this;
    }

    public ChoiceCard<T> buildPCL()
    {
        if (strings == null)
        {
            setText("", "", "");
        }

        return new ChoiceCard<T>(this);
    }
}