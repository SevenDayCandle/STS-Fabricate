package pinacolada.augments;


import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
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
    public AbstractCard card;

    public PCLAugment(PCLAugmentData data) {
        this(data, 0, 0);
    }

    public PCLAugment(PCLAugmentData data, int form, int timesUpgraded) {
        this(data, new SaveData(data.ID, form, timesUpgraded));
    }

    public PCLAugment(PCLAugmentData data, SaveData save) {
        this.data = data;
        this.save = save;
        skills = new PSkillContainer();
        setup();
        setForm(save.form, save.timesUpgraded);
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

    @Override
    public String getEffectPowerTextStrings() {
        return EUIUtils.joinStringsMapNonnull(PGR.config.removeLineBreaks.get() ? " " : EUIUtils.DOUBLE_SPLIT_LINE,
                ef -> ef != null ? StringUtils.capitalize(ef.getPowerText(null)) : null,
                getFullEffects());
    }

    @Override
    public String getEffectStrings() {
        return EUIUtils.joinStringsMapNonnull(PGR.config.removeLineBreaks.get() ? " " : EUIUtils.DOUBLE_SPLIT_LINE,
                ef -> ef != null ? StringUtils.capitalize(ef.getText(null)) : null,
                getFullEffects());
    }

    public String getFullText() {
        String reqs = getReqsString();
        return EUIUtils.joinTrueStrings(EUIUtils.SPLIT_LINE,
                PCLCoreStrings.colorString("i", EUIRM.strings.numAdjNoun(EUIRM.strings.numNoun(PGR.core.strings.misc_tier, getTier()), data.category.getName(), PGR.core.tooltips.augment.title)),
                !canRemove() ? PGR.core.strings.misc_unremovableDesc : null,
                reqs != null ? PCLCoreStrings.headerString(PGR.core.strings.misc_requirement, getReqsString()) : reqs,
                getEffectPowerTextStrings());
    }

    public String getID() {
        return save.ID;
    }

    public String getName() {
        return GameUtilities.getMultiformName(data.getName(), save.form, save.timesUpgraded + 1, data.maxForms, data.maxUpgradeLevel, data.branchFactor, true);
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
        return data.getTextureBase();
    }

    public int getTier() {
        return data.getTier(save.form) + data.getTierUpgrade(save.form) * save.timesUpgraded;
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
        return data.create(save.form, save.timesUpgraded);
    }

    public void onAddToCard(AbstractCard c) {
        for (PSkill<?> skill : skills.onUseEffects) {
            skill.onAddToCard(c); // Do not set source, values should be sourced from augment instead
        }
        card = c;
    }

    public void onRemoveFromCard(AbstractCard c) {
        for (PSkill<?> skill : skills.onUseEffects) {
            skill.onRemoveFromCard(c);
        }
        card = null;
    }

    public void reset() {
        skills.clear();
        setup();
    }

    public void setForm(int form, int timesUpgraded) {
        this.save.form = form;
        this.save.timesUpgraded = timesUpgraded;

        for (PSkill<?> ef : getEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
        for (PSkill<?> ef : getPowerEffects()) {
            ef.setAmountFromCard().onUpgrade();
        }
    }

    protected void setup() {

    }

    public static class SaveData implements Serializable {
        static final long serialVersionUID = 1L;
        public String ID;
        public int form;
        public int timesUpgraded;

        public SaveData(String id, int form, int timesUpgraded) {
            this.ID = id;
            this.timesUpgraded = timesUpgraded;
            this.form = form;
        }

        public PCLAugment create() {
            PCLAugmentData data = getData();
            return data != null ? data.create(this) : null;
        }

        public PCLAugmentData getData() {
            return PCLAugmentData.getStaticDataOrCustom(ID);
        }
    }
}
