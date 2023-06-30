package pinacolada.augments;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

public abstract class PCLAugment implements KeywordProvider {
    public static final int WEIGHT_MODIFIER = 3;
    public final PCLAugmentData data;
    public final String ID;
    public PSkill<?> skill;
    public PCLCard card;

    public PCLAugment(PCLAugmentData data) {
        this(data, data.skill);
    }

    public PCLAugment(PCLAugmentData data, PSkill<?> skill) {
        this.data = data;
        this.ID = data.ID;
        this.skill = skill.makeCopy();
    }

    protected static PCLAugmentData register(Class<? extends PCLAugment> type, PCLAugmentCategorySub category, int tier) {
        return register(type, category.resources, category, tier);
    }

    protected static PCLAugmentData register(Class<? extends PCLAugment> type, PCLResources<?, ?, ?, ?> resources, PCLAugmentCategorySub category, int tier) {
        return PCLAugmentData.registerData(new PCLAugmentData(type, resources, category, tier));
    }

    public static PCLAugmentReqs setAffinities(PCLAffinity... values) {
        return new PCLAugmentReqs().setAffinities(values);
    }

    public static PCLAugmentReqs setAffinitiesNot(PCLAffinity... values) {
        return new PCLAugmentReqs().setAffinitiesNot(values);
    }

    public static PCLAugmentReqs setRarities(AbstractCard.CardRarity... values) {
        return new PCLAugmentReqs().setRarities(values);
    }

    public static PCLAugmentReqs setTags(PCLCardTag... values) {
        return new PCLAugmentReqs().setTags(values);
    }

    public static PCLAugmentReqs setTagsNot(PCLCardTag... values) {
        return new PCLAugmentReqs().setTagsNot(values);
    }

    public static PCLAugmentReqs setTargets(PCLCardTarget... values) {
        return new PCLAugmentReqs().setTargets(values);
    }

    public static PCLAugmentReqs setTypes(AbstractCard.CardType... values) {
        return new PCLAugmentReqs().setTypes(values);
    }

    public void addToCard(PCLCard c) {
        if (canApplyImpl(c)) {
            c.addAugment(this);
        }
    }

    // TODO allow applying to non-PCLCard
    public boolean canApply(AbstractCard c) {
        return data.canApply(c);
    }

    /* An augment can only be applied if
     *   1. Card has free augment slots
     *   2. Augment requirements are passed
     *   3. Card doesn't already have an augment of its lineage
     */
    protected boolean canApplyImpl(PCLCard c) {
        return data.canApplyImpl(c);
    }

    public boolean canRemove() {
        return !data.isSpecial;
    }

    public String getFullText() {
        String reqs = getReqsString();
        return EUIUtils.joinTrueStrings(EUIUtils.SPLIT_LINE,
                PCLCoreStrings.colorString("p", EUIRM.strings.numAdjNoun(EUIRM.strings.numNoun(PGR.core.strings.misc_tier, data.tier), data.category.getName(), PGR.core.tooltips.augment.title)), // TODO show unremovable string if data is special
                reqs != null ? PCLCoreStrings.headerString(PGR.core.strings.misc_requirement, getReqsString()) : reqs,
                getPowerText());
    }

    public String getName() {
        return data.strings.NAME;
    }

    public String getPowerText() {
        return skill.getPowerText();
    }

    public String getReqsString() {
        return data.reqs == null ? null : data.reqs.getString();
    }

    public String getText() {
        return skill.getText();
    }

    public Texture getTexture() {
        return data.categorySub.getTexture();
    }

    public Texture getTextureBase() {
        return data.category.getIcon();
    }

    public EUIKeywordTooltip getTip() {
        return new EUIKeywordTooltip(getName(), getFullText());
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(getTip());
    }

    public PCLAugment makeCopy() {
        PCLAugment copy = null;
        try {
            Constructor<? extends PCLAugment> c = EUIUtils.tryGetConstructor(this.getClass(), PSkill.class);
            if (c != null) {
                copy = c.newInstance(skill);
                copy.card = card;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(this, "Failed to copy");
        }
        return copy;
    }

    public void onAddToCard(PCLCard c) {
        this.skill.setSource(c).onAddToCard(c);
        card = c;
    }

    public void onRemoveFromCard(PCLCard c) {
        this.skill.onRemoveFromCard(c);
        card = null;
    }
}
