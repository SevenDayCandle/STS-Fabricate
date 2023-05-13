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

public class ChoiceCardData<T> extends PCLDynamicCardData {
    public final boolean fromCustom;
    public final T object;

    public ChoiceCardData(PCLCard card, T object) {
        super(card, false);
        this.object = object;
        this.showTypeText = false;
        this.fromCustom = card instanceof PCLDynamicCard;
    }

    public ChoiceCardData(PCLCardData card, T object) {
        super(card, false);
        this.object = object;
        this.showTypeText = false;
        this.fromCustom = card instanceof PCLDynamicCardData;
    }

    public static ChoiceCardData<PCLAffinity> affinity(PCLAffinity affinity) {
        ChoiceCardData<PCLAffinity> builder = new ChoiceCardData<PCLAffinity>(AffinityToken.getCardData(affinity), affinity);
        String img = AffinityToken.getCardData(affinity).getImagePath(GameUtilities.getActingColor());
        if (img != null) {
            builder.portraitForeground = new ColoredTexture(EUIRM.getTexture(img, true));
        }
        builder.portraitImage = new ColoredTexture(EUIRM.getTexture(PGR.getCardImage(builder.ID), true), affinity.getAlternateColor(0.55f));
        builder.imagePath = AffinityToken.backgroundPath();
        return builder;
    }

    public static <T> ChoiceCardData<T> create(PCLCardData card, T object) {
        return new ChoiceCardData<T>(card, object);
    }

    public static <T> ChoiceCard<T> generate(PCLCardData card, T object) {
        return new ChoiceCardData<T>(card, object).create();
    }

    public static ChoiceCard<PCLAffinity> generateAffinity(PCLAffinity affinity) {
        return affinity(affinity).create();
    }

    public static ChoiceCardData<PSkill<?>> skill(PCLCardData card, PSkill<?> skill) {
        return (ChoiceCardData<PSkill<?>>) ChoiceCardData.create(card, skill)
                .addPSkill(skill)
                .setTarget(skill.target);
    }

    public static ChoiceCardData<PCLAffinity> skillAffinity(PSkill<?> skill, PCLAffinity affinity) {
        return (ChoiceCardData<PCLAffinity>) ChoiceCardData.affinity(affinity)
                .addPSkill(skill)
                .setTarget(skill.target);
    }

    public static ChoiceCardData<PCLAffinity> skillAffinity(PSkill<?> skill) {
        PCLAffinity affinity = PCLAffinity.Star;
        PField fields = skill.fields;
        if (fields instanceof PField_Affinity) {
            List<PCLAffinity> affinities = ((PField_Affinity) fields).affinities;
            if (affinities.size() > 0) {
                affinity = affinities.get(0);
            }
        }
        else if (fields instanceof PField_CardModifyAffinity) {
            List<PCLAffinity> affinities = ((PField_CardModifyAffinity) fields).addAffinities;
            if (affinities.size() > 0) {
                affinity = affinities.get(0);
            }
        }
        return skillAffinity(skill, affinity);
    }

    public ChoiceCardData<T> addPSkill(PSkill<?> effect) {
        super.addPSkill(effect, false);
        return this;
    }

    public ChoiceCard<T> create() {
        if (strings == null) {
            setText("", "", "");
        }

        return new ChoiceCard<T>(this);
    }
}