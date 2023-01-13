package pinacolada.potions;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.FlashPotionEffect;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.Skills;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class PCLPotion extends AbstractPotion implements TooltipProvider, PointerProvider
{
    public final ArrayList<EUITooltip> pCLTips = new ArrayList<>();
    public final Skills skills = new Skills();
    public final String[] extraDescriptions;

    // We deliberately avoid using initializeData because we need to load the PotionStrings after the super call
    public PCLPotion(String id, PotionRarity rarity, PotionSize size, PotionEffect effect, Color liquidColor, Color hybridColor, Color spotsColor, AbstractPlayer.PlayerClass playerClass)
    {
        super("", id, rarity, size, effect, liquidColor.cpy(), hybridColor.cpy(), spotsColor.cpy());
        PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(id);
        name = potionStrings.NAME;
        extraDescriptions = potionStrings.DESCRIPTIONS;
        this.potency = this.getPotency();
        setup();
        initializeTips(playerClass);
    }

    public PCLPotion(String id, PotionRarity rarity, PotionSize size, PotionEffect effect, Color liquidColor, Color hybridColor, Color spotsColor)
    {
        this(id, rarity, size, effect, liquidColor, hybridColor, spotsColor, null);
    }

    public static String createFullID(Class<? extends PCLPotion> type)
    {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?,?,?> resources, Class<? extends PCLPotion> type)
    {
        return resources.createID(type.getSimpleName());
    }

    @Override
    public Skills getSkills()
    {
        return skills;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getID()
    {
        return ID;
    }

    @Override
    public int xValue()
    {
        return getPotency();
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return pCLTips;
    }

    @Override
    public List<EUITooltip> getTipsForFilters()
    {
        return pCLTips.subList(1, tips.size());
    }

    public void setup()
    {
    }

    protected void initializeTips(AbstractPlayer.PlayerClass playerClass)
    {
        final ArrayList<String> effectStrings = EUIUtils.map(getEffects(), PSkill::getPowerText);
        this.description = effectStrings.size() > 0 ? effectStrings.get(0) : "";
        pCLTips.clear();
        pCLTips.add(playerClass != null ? new EUITooltip(name, playerClass, effectStrings) : new EUITooltip(name, effectStrings));
        EUIGameUtils.scanForTips(description, pCLTips);
        this.isThrown = EUIUtils.any(getEffects(), e -> e.target == PCLCardTarget.Single);
        this.targetRequired = isThrown;
    }

    public void update()
    {
        super.update();
        if (this.hb.justHovered)
        {
            for (PSkill ef : getEffects())
            {
                ef.setAmountFromCard();
            }
        }
    }

    protected void renderImpl(SpriteBatch sb, boolean useOutlineColor)
    {
        float angle = ReflectionHacks.getPrivate(this, AbstractPotion.class, "angle");
        Texture containerImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "containerImg");
        Texture liquidImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "liquidImg");
        Texture hybridImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "hybridImg");
        Texture spotsImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "spotsImg");
        Texture outlineImg = ReflectionHacks.getPrivate(this, AbstractPotion.class, "outlineImg");
        ArrayList<FlashPotionEffect> effect = ReflectionHacks.getPrivate(this, AbstractPotion.class, "effect");

        updateFlash();
        updateEffect();
        if (this.hb.hovered)
        {
            EUITooltip.queueTooltips(this);
            this.scale = 1.5F * Settings.scale;
        }
        else
        {
            this.scale = MathHelper.scaleLerpSnap(this.scale, 1.2F * Settings.scale);
        }

        this.renderOutline(sb, useOutlineColor ? this.labOutlineColor : Settings.HALF_TRANSPARENT_BLACK_COLOR);
        sb.setColor(this.liquidColor);
        sb.draw(liquidImg, this.posX - 32.0F, this.posY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, angle, 0, 0, 64, 64, false, false);
        if (this.hybridColor != null)
        {
            sb.setColor(this.hybridColor);
            sb.draw(hybridImg, this.posX - 32.0F, this.posY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, angle, 0, 0, 64, 64, false, false);
        }

        if (this.spotsColor != null)
        {
            sb.setColor(this.spotsColor);
            sb.draw(spotsImg, this.posX - 32.0F, this.posY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, angle, 0, 0, 64, 64, false, false);
        }

        sb.setColor(Color.WHITE);
        sb.draw(containerImg, this.posX - 32.0F, this.posY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, angle, 0, 0, 64, 64, false, false);

        for (FlashPotionEffect e : effect)
        {
            e.render(sb, this.posX, this.posY);
        }

        if (this.hb != null)
        {
            this.hb.render(sb);
        }
    }

    @Override
    public void use(AbstractCreature target)
    {
        final PCLUseInfo info = new PCLUseInfo(null, AbstractDungeon.player, target);
        for (PSkill ef : getEffects())
        {
            ef.use(info);
        }
    }

    @Override
    public void shopRender(SpriteBatch sb)
    {
        this.generateSparkles(0.0F, 0.0F, false);
        renderImpl(sb, false);
    }

    @Override
    public void labRender(SpriteBatch sb)
    {
        renderImpl(sb, true);
    }

    @Override
    public AbstractPotion makeCopy()
    {
        try
        {
            return getClass().getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            EUIUtils.logError(this, e.getMessage());
            return null;
        }
    }

    @SpireOverride
    protected void updateEffect() {
        SpireSuper.call();
    }

    @SpireOverride
    protected void updateFlash() {
        SpireSuper.call();
    }

}
