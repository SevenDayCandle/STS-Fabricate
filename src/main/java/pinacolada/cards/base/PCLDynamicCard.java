package pinacolada.cards.base;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.utilities.ColoredString;
import extendedui.utilities.ColoredTexture;
import pinacolada.interfaces.markers.DynamicCard;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrigger;
import pinacolada.skills.skills.special.moves.PMove_DealCardDamage;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;

public class PCLDynamicCard extends PCLCard implements DynamicCard
{
    private final TextureAtlas.AtlasRegion vanillaEnergyOrb;
    private final TextureAtlas.AtlasRegion vanillaBg;
    protected ArrayList<PCLCardBuilder> forms;
    protected PCLCardBuilder builder;
    private Texture customBg;
    private Texture customEnergyOrb;

    public PCLDynamicCard(PCLCardBuilder builder)
    {
        this(builder, false);
    }

    public PCLDynamicCard(PCLCardBuilder builder, boolean shouldFindForms)
    {
        super(builder, builder.ID, builder.imagePath,
                builder.getCost(0), builder.cardType, builder.cardColor, builder.cardRarity, builder.cardTarget.cardTarget, 0, 0, new BuilderInfo(builder, shouldFindForms));
        this.vanillaEnergyOrb = getVanillaEnergyOrb(builder.cardColor);
        if (vanillaEnergyOrb == null)
        {
            this.customEnergyOrb = getEnergyOrb();
        }
        this.vanillaBg = getBaseGameCardBackground();
        if (vanillaBg == null)
        {
            this.customBg = getCardBackground();
        }
    }

    public PCLDynamicCard findForms()
    {
        PCLCustomCardSlot cSlot = PCLCustomCardSlot.get(cardID);
        if (cSlot != null)
        {
            this.forms = cSlot.builders;
        }
        return this;
    }

    protected TextureAtlas.AtlasRegion getBaseGameCardBackground()
    {
        switch (type)
        {
            case POWER:
                switch (color)
                {
                    case RED:
                        return isPopup ? ImageMaster.CARD_POWER_BG_RED_L : ImageMaster.CARD_POWER_BG_RED;
                    case GREEN:
                        return isPopup ? ImageMaster.CARD_POWER_BG_GREEN_L : ImageMaster.CARD_POWER_BG_GREEN;
                    case BLUE:
                        return isPopup ? ImageMaster.CARD_POWER_BG_BLUE_L : ImageMaster.CARD_POWER_BG_BLUE;
                    case PURPLE:
                        return isPopup ? ImageMaster.CARD_POWER_BG_PURPLE_L : ImageMaster.CARD_POWER_BG_PURPLE;
                    default:
                        return null;
                }
            case ATTACK:
                switch (color)
                {
                    case RED:
                        return isPopup ? ImageMaster.CARD_ATTACK_BG_RED_L : ImageMaster.CARD_ATTACK_BG_RED;
                    case GREEN:
                        return isPopup ? ImageMaster.CARD_ATTACK_BG_GREEN_L : ImageMaster.CARD_ATTACK_BG_GREEN;
                    case BLUE:
                        return isPopup ? ImageMaster.CARD_ATTACK_BG_BLUE_L : ImageMaster.CARD_ATTACK_BG_BLUE;
                    case PURPLE:
                        return isPopup ? ImageMaster.CARD_ATTACK_BG_PURPLE_L : ImageMaster.CARD_ATTACK_BG_PURPLE;
                    default:
                        return null;
                }
            default:
                switch (color)
                {
                    case RED:
                        return isPopup ? ImageMaster.CARD_SKILL_BG_RED_L : ImageMaster.CARD_SKILL_BG_RED;
                    case GREEN:
                        return isPopup ? ImageMaster.CARD_SKILL_BG_GREEN_L : ImageMaster.CARD_SKILL_BG_GREEN;
                    case BLUE:
                        return isPopup ? ImageMaster.CARD_SKILL_BG_BLUE_L : ImageMaster.CARD_SKILL_BG_BLUE;
                    case PURPLE:
                        return isPopup ? ImageMaster.CARD_SKILL_BG_PURPLE_L : ImageMaster.CARD_SKILL_BG_PURPLE;
                    default:
                        return null;
                }
        }
    }

    @Override
    protected Texture getCardBackground()
    {
        if (GameUtilities.isPCLCardColor(this.color))
        {
            return super.getCardBackground();
        }
        Texture texture = null;
        if (isPopup)
        {
            switch (type)
            {
                case POWER:
                    if (BaseMod.getPowerBgPortraitTexture(color) == null)
                    {
                        BaseMod.savePowerBgPortraitTexture(color, ImageMaster.loadImage(BaseMod.getPowerBgPortrait(color)));
                    }
                    texture = BaseMod.getPowerBgPortraitTexture(color);
                    break;
                case ATTACK:
                    if (BaseMod.getAttackBgPortraitTexture(color) == null)
                    {
                        BaseMod.saveAttackBgPortraitTexture(color, ImageMaster.loadImage(BaseMod.getAttackBgPortrait(color)));
                    }
                    texture = BaseMod.getAttackBgPortraitTexture(color);
                    break;
                default:
                    if (BaseMod.getSkillBgPortraitTexture(color) == null)
                    {
                        BaseMod.saveSkillBgPortraitTexture(color, ImageMaster.loadImage(BaseMod.getSkillBgPortrait(color)));
                    }
                    texture = BaseMod.getSkillBgPortraitTexture(color);
            }
        }
        else
        {
            switch (type)
            {
                case POWER:
                    if (BaseMod.getPowerBgTexture(color) == null)
                    {
                        BaseMod.savePowerBgTexture(color, ImageMaster.loadImage(BaseMod.getPowerBg(color)));
                    }
                    texture = BaseMod.getPowerBgTexture(color);
                    break;
                case ATTACK:
                    if (BaseMod.getAttackBgTexture(color) == null)
                    {
                        BaseMod.saveAttackBgTexture(color, ImageMaster.loadImage(BaseMod.getAttackBg(color)));
                    }
                    texture = BaseMod.getAttackBgTexture(color);
                    break;
                default:
                    if (BaseMod.getSkillBgTexture(color) == null)
                    {
                        BaseMod.saveSkillBgTexture(color, ImageMaster.loadImage(BaseMod.getSkillBg(color)));
                    }
                    texture = BaseMod.getSkillBgTexture(color);
            }
        }
        return texture != null ? texture : super.getCardBackground();
    }

    @Override
    protected ColoredTexture getCardBanner()
    {
        if (GameUtilities.isPCLCardColor(this.color))
        {
            return super.getCardBanner();
        }
        return null;
    }

    @Override
    protected Texture getEnergyOrb()
    {
        if (GameUtilities.isPCLCardColor(this.color))
        {
            return super.getEnergyOrb();
        }
        Texture t = isPopup ? BaseMod.getEnergyOrbPortraitTexture(this.color) : BaseMod.getEnergyOrbTexture(this.color);
        if (t == null)
        {
            t = ImageMaster.loadImage(isPopup ? BaseMod.getEnergyOrbPortrait(color) : BaseMod.getEnergyOrb(color));
            BaseMod.saveEnergyOrbPortraitTexture(color, t);
        }
        return t;
    }

    @Override
    public int getMaxForms()
    {
        return forms != null ? forms.size() : 1;
    }

    @Override
    protected ColoredTexture getPortraitFrame()
    {
        if (GameUtilities.isPCLCardColor(this.color))
        {
            return super.getPortraitFrame();
        }
        return null;
    }

    @Override
    public int setForm(Integer form, int timesUpgraded)
    {
        super.setForm(form, timesUpgraded);
        if (forms != null && forms.size() > form)
        {
            this.builder = forms.get(form);
        }
        if (this.builder != null)
        {
            setProperties(this.builder, form, timesUpgraded);
        }
        return this.auxiliaryData.form;
    }

    @Override
    public void setup(Object input)
    {
        if (input instanceof BuilderInfo)
        {
            this.builder = ((BuilderInfo) input).builder;
            if (((BuilderInfo) input).shouldFindForms)
            {
                findForms();
            }
        }
    }

    @Override
    public PCLDynamicCard makeCopy()
    {
        PCLDynamicCard copy = new PCLDynamicCard(builder);
        if (forms != null && !forms.isEmpty())
        {
            copy.setForms(forms);
        }
        return copy;
    }

    @Override
    protected void renderCardBg(SpriteBatch sb, float x, float y)
    {
        if (GameUtilities.isPCLCardColor(this.color))
        {
            super.renderCardBg(sb, x, y);
        }
        else
        {
            float popUpMultiplier = isPopup ? 0.5f : 1f;
            Texture mask = getCardBackgroundMask();
            float width = mask.getWidth();
            float height = mask.getHeight();
            if (vanillaBg != null)
            {
                PCLRenderHelpers.drawWithMask(sb,
                        s -> PCLRenderHelpers.drawOnCardAuto(s, this, mask, new Vector2(0, 0), width, height, getRenderColor(), transparency, popUpMultiplier),
                        s -> PCLRenderHelpers.drawOnCardAuto(s, this, vanillaBg, new Vector2(0, 0), vanillaBg.packedWidth, vanillaBg.packedHeight, getRenderColor(), transparency, popUpMultiplier)
                );
            }
            else if (customBg != null)
            {
                PCLRenderHelpers.drawWithMask(sb,
                        s -> PCLRenderHelpers.drawOnCardAuto(s, this, mask, new Vector2(0, 0), width, height, getRenderColor(), transparency, popUpMultiplier),
                        s -> PCLRenderHelpers.drawOnCardAuto(s, this, customBg, new Vector2(0, 0), width, height, getRenderColor(), transparency, popUpMultiplier)
                );
            }
        }
    }

    @Override
    protected void renderEnergy(SpriteBatch sb)
    {
        if (GameUtilities.isPCLCardColor(this.color))
        {
            super.renderEnergy(sb);
        }
        else if (this.cost > -2 && !getDarken() && !this.isLocked && this.isSeen)
        {
            if (this.vanillaEnergyOrb != null)
            {
                this.renderAtlas(sb, getRenderColor(), this.vanillaEnergyOrb, this.current_x, this.current_y);

                ColoredString costString = getCostString();
                if (costString != null)
                {
                    BitmapFont font = PCLRenderHelpers.getEnergyFont(this);
                    PCLRenderHelpers.writeOnCard(sb, this, font, costString.text, -132f, 192f, costString.color);
                    PCLRenderHelpers.resetFont(font);
                }
            }
            else if (customEnergyOrb != null)
            {
                float popUpMultiplier = isPopup ? 0.5f : 1f;
                PCLRenderHelpers.drawOnCardAuto(sb, this, customEnergyOrb, new Vector2(0, 0), customEnergyOrb.getWidth(), customEnergyOrb.getHeight(), getRenderColor(), transparency, popUpMultiplier);
                ColoredString costString = getCostString();
                if (costString != null)
                {
                    BitmapFont font = PCLRenderHelpers.getEnergyFont(this);
                    PCLRenderHelpers.writeOnCard(sb, this, font, costString.text, -132f, 192f, costString.color);
                    PCLRenderHelpers.resetFont(font);
                }
            }
        }
    }

    protected Texture getCardBackgroundMask()
    {
        switch (type)
        {
            case ATTACK:
                return isPopup ? PGR.core.images.cardBackgroundAttackReplL.texture() : PGR.core.images.cardBackgroundAttackRepl.texture();
            case POWER:
                return isPopup ? PGR.core.images.cardBackgroundPowerReplL.texture() : PGR.core.images.cardBackgroundPowerRepl.texture();
            default:
                return isPopup ? PGR.core.images.cardBackgroundSkillReplL.texture() : PGR.core.images.cardBackgroundSkillRepl.texture();
        }
    }

    protected TextureAtlas.AtlasRegion getVanillaEnergyOrb(CardColor color)
    {
        switch (color)
        {
            case RED:
                return ImageMaster.CARD_RED_ORB;
            case GREEN:
                return ImageMaster.CARD_GREEN_ORB;
            case BLUE:
                return ImageMaster.CARD_BLUE_ORB;
            case PURPLE:
                return ImageMaster.CARD_PURPLE_ORB;
        }
        return null;
    }

    public PCLDynamicCard setForms(ArrayList<PCLCardBuilder> builders)
    {
        this.forms = builders;
        changeForm(this.auxiliaryData.form, timesUpgraded);
        return this;
    }

    protected void setProperties(PCLCardBuilder builder, Integer form, int timesUpgraded)
    {
        super.setupProperties(builder, form, timesUpgraded);

        this.builder = builder;
        this.cropPortrait = false;
        this.showTypeText = builder.showTypeText;
        this.maxUpgradeLevel = builder.maxUpgradeLevel;

        if (builder.portraitImage != null)
        {
            this.portraitImg = builder.portraitImage;
        }
        if (builder.portraitForeground != null)
        {
            this.portraitForeground = builder.portraitForeground;
        }
        if (builder.fakePortrait != null)
        {
            this.fakePortrait = builder.fakePortrait;
        }

        clearSkills();
        onDamageEffect = null;
        final PCLCard source = builder.source != null ? builder.source : this;
        for (PSkill effect : builder.moves)
        {
            if (effect == null)
            {
                continue;
            }
            if (effect instanceof PMove_DealCardDamage)
            {
                onDamageEffect = (PMove_DealCardDamage) effect.makeCopy().setSource(source).onAddToCard(this);
            }
            else
            {
                addUseMove(effect);
            }
        }

        for (PTrigger pe : builder.pPowers)
        {
            if (pe == null)
            {
                continue;
            }
            addGainPower(pe);
        }

        // Automatically create Attack actions for Attacks that do not already have attack actions
        if ((builder.cardType == CardType.ATTACK || builder.cardType == PCLEnum.CardType.SUMMON) && onDamageEffect == null)
        {
            this.addDamageMove(builder.attackEffect);
        }

        initializeDescription();
    }

    private static class BuilderInfo
    {
        protected final PCLCardBuilder builder;
        protected final boolean shouldFindForms;

        BuilderInfo(PCLCardBuilder builder, boolean shouldFindForms)
        {
            this.builder = builder;
            this.shouldFindForms = shouldFindForms;
        }
    }
}