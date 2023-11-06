package pinacolada.augments;


import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillContainer;
import pinacolada.utilities.GameUtilities;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public abstract class PCLAugment implements KeywordProvider, PointerProvider {
    public static final int WEIGHT_MODIFIER = 3;
    public final PCLAugmentData data;
    public SaveData save;
    public PSkillContainer skills;
    public PCLCard card;

    public PCLAugment(PCLAugmentData data) {
        this(data, 0, 0);
    }

    public PCLAugment(PCLAugmentData data, int timesUpgraded, int form) {
        this(data, data.ID, timesUpgraded, form);
    }

    public PCLAugment(PCLAugmentData data, String id, int timesUpgraded, int form) {
        this.data = data;
        this.save = new SaveData(id, timesUpgraded, form);
        skills = new PSkillContainer();
        setup();
    }

    protected static PCLAugmentData register(Class<? extends PCLAugment> type, PCLAugmentCategory category) {
        return register(type, PGR.core, category);
    }

    protected static PCLAugmentData register(Class<? extends PCLAugment> type, PCLResources<?, ?, ?, ?> resources, PCLAugmentCategory category) {
        return PCLAugmentData.registerData(new PCLAugmentData(type, resources, category));
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
        return !data.permanent;
    }

    public String getFullText() {
        String reqs = getReqsString();
        return EUIUtils.joinTrueStrings(EUIUtils.SPLIT_LINE,
                PCLCoreStrings.colorString("i", EUIRM.strings.numAdjNoun(EUIRM.strings.numNoun(PGR.core.strings.misc_tier, data.tier), data.category.getName(), PGR.core.tooltips.augment.title)), // TODO show unremovable string if data is special
                reqs != null ? PCLCoreStrings.headerString(PGR.core.strings.misc_requirement, getReqsString()) : reqs,
                getEffectPowerTextStrings());
    }

    public String getID() {
        return save.ID;
    }

    public String getName() {
        return GameUtilities.getMultiformName(data.getName(), save.form, save.timesUpgraded, data.maxForms, data.maxUpgradeLevel, data.branchFactor, false);
    }

    public String getReqsString() {
        return data.reqs == null ? null : data.reqs.getString();
    }

    public PSkillContainer getSkills() {
        return skills;
    }

    public Texture getTexture() {
        return data.getTexture();
    }

    public Texture getTextureBase() {
        return data.category.getIcon();
    }

    public int getTier() {
        return data.getTier(save.form) + data.getTierUpgrade(save.form);
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return new EUIKeywordTooltip(getName(), getFullText());
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(getTooltip());
    }

    public PCLAugment makeCopy() {
        return data.create(save.timesUpgraded, save.form);
    }

    public void onAddToCard(PCLCard c) {
        for (PSkill<?> skill : skills.onUseEffects) {
            skill.setSource(c).onAddToCard(c);
        }
        card = c;
    }

    public void onRemoveFromCard(PCLCard c) {
        for (PSkill<?> skill : skills.onUseEffects) {
            skill.onRemoveFromCard(c);
        }
        card = null;
    }

    public void reset() {
        skills.clear();
        setup();
    }

    protected void setup() {

    }

    public static class SaveData implements Serializable {
        public String ID;
        public int form;
        public int timesUpgraded;

        public SaveData(String id, int timesUpgraded, int form) {
            this.ID = id;
            this.timesUpgraded = timesUpgraded;
            this.form = form;
        }

        public PCLAugment create() {
            PCLAugmentData data = getData();
            return data != null ? data.create(timesUpgraded, form) : null;
        }

        public PCLAugmentData getData() {
            return PCLAugmentData.get(ID);
        }
    }
}
