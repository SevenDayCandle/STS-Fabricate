package pinacolada.cards.base;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLCardTargetingManager;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.interfaces.markers.FabricateItem;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PCardPrimary;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;

public class PCLDynamicCard extends PCLCard implements FabricateItem {
    private TextureAtlas.AtlasRegion vanillaEnergyOrb;
    private TextureAtlas.AtlasRegion vanillaEnergyOrbLarge;
    private TextureAtlas.AtlasRegion vanillaBg;
    private TextureAtlas.AtlasRegion vanillaBgLarge;
    private Texture customBg;
    private Texture customBgLarge;
    private Texture customEnergyOrb;
    private Texture customEnergyOrbLarge;
    protected ArrayList<PCLDynamicCardData> forms;
    protected PCLDynamicCardData builder;
    protected boolean shouldFindForms;

    public PCLDynamicCard(PCLDynamicCardData builder) {
        this(builder, 0, 0, true);
    }

    public PCLDynamicCard(PCLDynamicCardData builder, int form, int timesUpgraded, boolean shouldSetTextures) {
        super(builder, builder.ID, builder.imagePath,
                builder.getCost(form), builder.cardType, builder.cardColor, builder.cardRarity, PCLCardTargetingManager.PCL, form, timesUpgraded, builder);
        assignActualColor();
        if (shouldSetTextures) {
            initializeTextures();
        }
    }

    // Several in-game checks assume color = CURSE, so we need to have consistency
    protected void assignActualColor() {
        if (builder.cardType == CardType.CURSE) {
            this.color = CardColor.CURSE;
        }
    }

    protected void findForms() {
        PCLCustomCardSlot cSlot = PCLCustomCardSlot.get(cardID);
        if (cSlot != null) {
            this.forms = cSlot.builders;
        }
    }

    public void fullReset() {
        findForms();
        this.clearSkills();
        for (PCLCardTag tag : PCLCardTag.values()) {
            tag.set(this, 0);
        }
        this.onAttackEffect = null;
        this.onBlockEffect = null;

        // Use the builder as the source of truth for numbers since this may be inconsistent with the current cardData
        onFormChange(this.auxiliaryData.form, timesUpgraded);
        if (cardData != builder) {
            cardData.setNumbers(builder);
            cardData.setAttackType(builder.attackType);
            cardData.setTarget(builder.cardTarget);
            cardData.setTiming(builder.timing);
            cardData.setTags(builder.tags);
        }

        setupProperties(builder, this.auxiliaryData.form, timesUpgraded);
        setForm(this.auxiliaryData.form, timesUpgraded);
        refresh(null);
    }

    protected TextureAtlas.AtlasRegion getBaseGameCardBackground() {
        switch (type) {
            case POWER:
                switch (color) {
                    case RED:
                        return ImageMaster.CARD_POWER_BG_RED;
                    case GREEN:
                        return ImageMaster.CARD_POWER_BG_GREEN;
                    case BLUE:
                        return ImageMaster.CARD_POWER_BG_BLUE;
                    case PURPLE:
                        return ImageMaster.CARD_POWER_BG_PURPLE;
                    case COLORLESS:
                    case CURSE:
                        return ImageMaster.CARD_POWER_BG_GRAY;
                    default:
                        return null;
                }
            case ATTACK:
                switch (color) {
                    case RED:
                        return ImageMaster.CARD_ATTACK_BG_RED;
                    case GREEN:
                        return ImageMaster.CARD_ATTACK_BG_GREEN;
                    case BLUE:
                        return ImageMaster.CARD_ATTACK_BG_BLUE;
                    case PURPLE:
                        return ImageMaster.CARD_ATTACK_BG_PURPLE;
                    case COLORLESS:
                    case CURSE:
                        return ImageMaster.CARD_ATTACK_BG_GRAY;
                    default:
                        return null;
                }
            default:
                switch (color) {
                    case RED:
                        return ImageMaster.CARD_SKILL_BG_RED;
                    case GREEN:
                        return ImageMaster.CARD_SKILL_BG_GREEN;
                    case BLUE:
                        return ImageMaster.CARD_SKILL_BG_BLUE;
                    case PURPLE:
                        return ImageMaster.CARD_SKILL_BG_PURPLE;
                    case COLORLESS:
                        return ImageMaster.CARD_SKILL_BG_GRAY;
                    case CURSE:
                        return ImageMaster.CARD_SKILL_BG_BLACK;
                    default:
                        return null;
                }
        }
    }

    protected TextureAtlas.AtlasRegion getBaseGameCardPopupBackground() {
        switch (type) {
            case POWER:
                switch (color) {
                    case RED:
                        return ImageMaster.CARD_POWER_BG_RED_L;
                    case GREEN:
                        return ImageMaster.CARD_POWER_BG_GREEN_L;
                    case BLUE:
                        return ImageMaster.CARD_POWER_BG_BLUE_L;
                    case PURPLE:
                        return ImageMaster.CARD_POWER_BG_PURPLE_L;
                    case COLORLESS:
                    case CURSE:
                        return ImageMaster.CARD_POWER_BG_GRAY_L;
                    default:
                        return null;
                }
            case ATTACK:
                switch (color) {
                    case RED:
                        return ImageMaster.CARD_ATTACK_BG_RED_L;
                    case GREEN:
                        return ImageMaster.CARD_ATTACK_BG_GREEN_L;
                    case BLUE:
                        return ImageMaster.CARD_ATTACK_BG_BLUE_L;
                    case PURPLE:
                        return ImageMaster.CARD_ATTACK_BG_PURPLE_L;
                    case COLORLESS:
                    case CURSE:
                        return ImageMaster.CARD_ATTACK_BG_GRAY_L;
                    default:
                        return null;
                }
            default:
                switch (color) {
                    case RED:
                        return ImageMaster.CARD_SKILL_BG_RED_L;
                    case GREEN:
                        return ImageMaster.CARD_SKILL_BG_GREEN_L;
                    case BLUE:
                        return ImageMaster.CARD_SKILL_BG_BLUE_L;
                    case PURPLE:
                        return ImageMaster.CARD_SKILL_BG_PURPLE_L;
                    case COLORLESS:
                        return ImageMaster.CARD_SKILL_BG_GRAY_L;
                    case CURSE:
                        return ImageMaster.CARD_SKILL_BG_BLACK_L;
                    default:
                        return null;
                }
        }
    }

    @Override
    public TextureAtlas.AtlasRegion getBorderTexture() {
        if (GameUtilities.isPCLOnlyCardColor(builder.cardColor)) {
            return super.getBorderTexture();
        }
        switch (this.type) {
            case ATTACK:
                return ImageMaster.CARD_ATTACK_BG_SILHOUETTE;
            case POWER:
                return ImageMaster.CARD_POWER_BG_SILHOUETTE;
            default:
                return ImageMaster.CARD_SKILL_BG_SILHOUETTE;
        }
    }

    @Override
    protected Texture getCardBackground() {
        return customBg != null ? isPopup ? customBgLarge : customBg : super.getCardBackground();
    }

    protected Texture getCardBackgroundMask() {
        switch (type) {
            case ATTACK:
                return isPopup ? PCLCoreImages.CardUI.cardBackgroundAttackReplL.texture() : PCLCoreImages.CardUI.cardBackgroundAttackRepl.texture();
            case POWER:
                return isPopup ? PCLCoreImages.CardUI.cardBackgroundPowerReplL.texture() : PCLCoreImages.CardUI.cardBackgroundPowerRepl.texture();
            default:
                return isPopup ? PCLCoreImages.CardUI.cardBackgroundSkillReplL.texture() : PCLCoreImages.CardUI.cardBackgroundSkillRepl.texture();
        }
    }

    protected Texture getCustomCardBackground() {
        if (GameUtilities.isPCLOnlyCardColor(this.color)) {
            return super.getCardBackground();
        }
        Texture texture = null;
        switch (type) {
            case POWER:
                if (BaseMod.getPowerBgTexture(color) == null) {
                    BaseMod.savePowerBgTexture(color, ImageMaster.loadImage(BaseMod.getPowerBg(color)));
                }
                texture = BaseMod.getPowerBgTexture(color);
                break;
            case ATTACK:
                if (BaseMod.getAttackBgTexture(color) == null) {
                    BaseMod.saveAttackBgTexture(color, ImageMaster.loadImage(BaseMod.getAttackBg(color)));
                }
                texture = BaseMod.getAttackBgTexture(color);
                break;
            default:
                if (BaseMod.getSkillBgTexture(color) == null) {
                    BaseMod.saveSkillBgTexture(color, ImageMaster.loadImage(BaseMod.getSkillBg(color)));
                }
                texture = BaseMod.getSkillBgTexture(color);
        }
        return texture != null ? texture : super.getCardBackground();
    }

    protected Texture getCustomCardPopupBackground() {
        if (GameUtilities.isPCLOnlyCardColor(this.color)) {
            return super.getCardBackground();
        }
        Texture texture = null;
        switch (type) {
            case POWER:
                if (BaseMod.getPowerBgPortraitTexture(color) == null) {
                    BaseMod.savePowerBgPortraitTexture(color, ImageMaster.loadImage(BaseMod.getPowerBgPortrait(color)));
                }
                texture = BaseMod.getPowerBgPortraitTexture(color);
                break;
            case ATTACK:
                if (BaseMod.getAttackBgPortraitTexture(color) == null) {
                    BaseMod.saveAttackBgPortraitTexture(color, ImageMaster.loadImage(BaseMod.getAttackBgPortrait(color)));
                }
                texture = BaseMod.getAttackBgPortraitTexture(color);
                break;
            default:
                if (BaseMod.getSkillBgPortraitTexture(color) == null) {
                    BaseMod.saveSkillBgPortraitTexture(color, ImageMaster.loadImage(BaseMod.getSkillBgPortrait(color)));
                }
                texture = BaseMod.getSkillBgPortraitTexture(color);
        }
        return texture != null ? texture : super.getCardBackground();
    }

    protected Texture getCustomEnergyOrb() {
        if (GameUtilities.isPCLOnlyCardColor(this.color)) {
            return super.getEnergyOrb();
        }
        Texture t = BaseMod.getEnergyOrbTexture(this.color);
        if (t == null) {
            t = ImageMaster.loadImage(BaseMod.getEnergyOrb(color));
            BaseMod.saveEnergyOrbTexture(color, t);
        }
        return t;
    }

    protected Texture getCustomEnergyPopupOrb() {
        if (GameUtilities.isPCLOnlyCardColor(this.color)) {
            return super.getEnergyOrb();
        }
        Texture t = BaseMod.getEnergyOrbPortraitTexture(this.color);
        if (t == null) {
            t = ImageMaster.loadImage(BaseMod.getEnergyOrbPortrait(color));
            BaseMod.saveEnergyOrbPortraitTexture(color, t);
        }
        return t;
    }

    @Override
    public PCLDynamicCardData getDynamicData() {
        return builder;
    }

    @Override
    protected Texture getEnergyOrb() {
        return customEnergyOrb != null ? isPopup ? customEnergyOrbLarge : customEnergyOrb : super.getEnergyOrb();
    }

    protected TextureAtlas.AtlasRegion getVanillaCardBackgroundForRender() {
        return isPopup ? vanillaBgLarge : vanillaBg;
    }

    protected TextureAtlas.AtlasRegion getVanillaEnergyOrb(CardColor color) {
        switch (color) {
            case RED:
                return ImageMaster.CARD_RED_ORB;
            case GREEN:
                return ImageMaster.CARD_GREEN_ORB;
            case BLUE:
                return ImageMaster.CARD_BLUE_ORB;
            case PURPLE:
                return ImageMaster.CARD_PURPLE_ORB;
            case COLORLESS:
            case CURSE:
                return ImageMaster.CARD_COLORLESS_ORB;
        }
        return null;
    }

    protected TextureAtlas.AtlasRegion getVanillaEnergyOrbForRender() {
        return isPopup ? vanillaEnergyOrbLarge : vanillaEnergyOrb;
    }

    protected TextureAtlas.AtlasRegion getVanillaEnergyPopupOrb(CardColor color) {
        switch (color) {
            case RED:
                return ImageMaster.CARD_RED_ORB_L;
            case GREEN:
                return ImageMaster.CARD_GREEN_ORB_L;
            case BLUE:
                return ImageMaster.CARD_BLUE_ORB_L;
            case PURPLE:
                return ImageMaster.CARD_PURPLE_ORB_L;
            case COLORLESS:
            case CURSE:
                return ImageMaster.CARD_GRAY_ORB_L;
        }
        return null;
    }

    protected void initializeTextures() {
        if (!GameUtilities.isPCLOnlyCardColor(builder.cardColor) || (GameUtilities.isColorlessCardColor(this.color) && !PGR.getResources(builder.cardColor).usePCLFrame)) {
            this.vanillaEnergyOrb = getVanillaEnergyOrb(this.color);
            this.vanillaEnergyOrbLarge = getVanillaEnergyPopupOrb(this.color);
            if (vanillaEnergyOrb == null) {
                this.customEnergyOrb = getCustomEnergyOrb();
                this.customEnergyOrbLarge = getCustomEnergyPopupOrb();
            }
            this.vanillaBg = getBaseGameCardBackground();
            this.vanillaBgLarge = getBaseGameCardPopupBackground();
            if (vanillaBg == null) {
                this.customBg = getCustomCardBackground();
                this.customBgLarge = getCustomCardPopupBackground();
            }
        }
    }

    @Override
    public PCLDynamicCard makeCopy() {
        PCLDynamicCard copy = new PCLDynamicCard(builder);
        if (forms != null && !forms.isEmpty()) {
            copy.setForms(forms);
            copy.onFormChange(copy.getForm(), copy.timesUpgraded);
        }
        return copy;
    }

    @Override
    public PCLDynamicCard makeStatEquivalentCopy() {
        PCLDynamicCard copy = new PCLDynamicCard(builder);
        makeCopyProperties(copy);
        if (forms != null && !forms.isEmpty()) {
            copy.setForms(forms);
            copy.onFormChange(copy.getForm(), copy.timesUpgraded);
        }
        return copy;
    }

    @Override
    protected void onFormChange(Integer form, int timesUpgraded) {
        PCLDynamicCardData lastBuilder = null;
        if (forms != null && forms.size() > form) {
            lastBuilder = forms.get(form);
        }
        if (lastBuilder != null && lastBuilder != this.builder) {
            this.builder = lastBuilder;
            setupBuilder(this.builder);
        }
    }

    public void putCustomDesc(PSkill<?> skill, int index) {
        if (skill != null) {
            CardStrings s = builder.getStringsForLanguage(Settings.language);
            if (s != null && s.EXTENDED_DESCRIPTION != null && s.EXTENDED_DESCRIPTION.length > index) {
                String res = s.EXTENDED_DESCRIPTION[index];
                if (!StringUtils.isEmpty(res)) {
                    skill.overrideDesc = res;
                }
            }
        }
    }

    @Override
    protected void renderCardBg(SpriteBatch sb, float x, float y) {
        if (vanillaBg == null && customBg == null) {
            super.renderCardBg(sb, x, y);
        }
        else {
            float popUpMultiplier = isPopup ? 0.5f : 1f;
            Texture mask = getCardBackgroundMask();
            float width = mask.getWidth();
            float height = mask.getHeight();
            TextureAtlas.AtlasRegion vanilla = getVanillaCardBackgroundForRender();
            if (vanilla != null) {
                PCLRenderHelpers.drawWithMask(sb,
                        s -> PCLRenderHelpers.drawOnCardAuto(s, this, mask, 0, 0, width, height, getRenderColor(), transparency, popUpMultiplier),
                        s -> PCLRenderHelpers.drawOnCardAuto(s, this, vanilla, 0, 0, vanilla.packedWidth, vanilla.packedHeight, getRenderColor(), transparency, popUpMultiplier)
                );
            }
            else {
                Texture customBack = getCardBackground();
                PCLRenderHelpers.drawWithMask(sb,
                        s -> PCLRenderHelpers.drawOnCardAuto(s, this, mask, 0, 0, width, height, getRenderColor(), transparency, popUpMultiplier),
                        s -> PCLRenderHelpers.drawOnCardAuto(s, this, customBack, 0, 0, width, height, getRenderColor(), transparency, popUpMultiplier)
                );
            }
        }
    }

    @Override
    protected void renderEnergy(SpriteBatch sb) {
        if (vanillaEnergyOrb == null && customEnergyOrb == null) {
            super.renderEnergy(sb);
        }
        else if (this.cost > -2 && !getDarken() && !this.isLocked && this.isSeen) {
            TextureAtlas.AtlasRegion vanilla = getVanillaEnergyOrbForRender();
            if (vanilla != null) {
                // TODO better way of doing this instead of blindly copying vanilla SCV
                if (isPopup) {
                    this.renderAtlas(sb, getRenderColor(), vanilla, (float) Settings.WIDTH / 2.0F - 270.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F + 380.0F * Settings.scale, 0.5f);
                }
                else {
                    this.renderAtlas(sb, getRenderColor(), vanilla, this.current_x, this.current_y);
                }

                renderEnergyText(sb);
            }
            else {
                Texture custom = getEnergyOrb();
                float popUpMultiplier = isPopup ? 0.5f : 1f;
                PCLRenderHelpers.drawOnCardAuto(sb, this, custom, 0, 0, custom.getWidth(), custom.getHeight(), getRenderColor(), transparency, popUpMultiplier);
                renderEnergyText(sb);
            }
        }
    }

    public PCLDynamicCard setForms(ArrayList<PCLDynamicCardData> builders) {
        this.forms = builders;
        changeForm(this.auxiliaryData.form, timesUpgraded);
        return this;
    }

    @Override
    public void setup(Object input) {
        if (input instanceof PCLDynamicCardData) {
            this.builder = (PCLDynamicCardData) input;
            findForms();
            setupBuilder(this.builder);
        }
    }

    protected void setupBuilder(PCLDynamicCardData builder) {
        this.builder = builder;
        this.showTypeText = builder.showTypeText;

        clearSkills();
        onAttackEffect = null;
        onBlockEffect = null;
        final PCLCard source = builder.source != null ? builder.source : this;
        int exDescInd = -1;
        for (PSkill<?> effect : builder.moves) {
            exDescInd += 1;
            if (PSkill.isSkillBlank(effect)) {
                continue;
            }
            // Add damage/block effects and set their source to this card
            PSkill<?> eff = effect.makeCopy();
            putCustomDesc(eff, exDescInd);
            if (eff instanceof PCardPrimary_DealDamage) {
                addDamageMove((PCardPrimary_DealDamage) eff);
            }
            else if (eff instanceof PCardPrimary_GainBlock) {
                addBlockMove((PCardPrimary_GainBlock) eff);
            }
            else {
                addUseMove(eff);
            }
        }

        for (PSkill<?> pe : builder.powers) {
            exDescInd += 1;
            if (PSkill.isSkillBlank(pe)) {
                continue;
            }
            PSkill<?> pec = pe.makeCopy();
            putCustomDesc(pec, exDescInd);
            addPowerMove(pec);
        }

        initializeDescription();
    }

    public void setupImages(String imagePath) {
        portrait = null;
        if (builder.portraitForeground != null) {
            this.portraitForeground = builder.portraitForeground;
        }
        if (builder.portraitImage != null) {
            this.portraitImg = builder.portraitImage;
            assetUrl = imagePath;
        }
        else {
            loadImage(imagePath);
        }
    }

    // These are null when rendering PCL colors
    @Override
    public boolean shouldUsePCLFrame() {
        return vanillaBg == null && customBg == null && super.shouldUsePCLFrame();
    }
}