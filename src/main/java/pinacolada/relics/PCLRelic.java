package pinacolada.relics;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActions;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class PCLRelic extends CustomRelic implements TooltipProvider
{
    public static AbstractPlayer player;
    public static Random rng;
    public ArrayList<EUITooltip> tips;
    public EUITooltip mainTooltip;
    public AbstractPlayer.PlayerClass playerClass;

    public PCLRelic(String id, Texture texture, RelicTier tier, LandingSound sfx)
    {
        this(id, texture, tier, sfx, null);
    }

    public PCLRelic(String id, RelicTier tier, LandingSound sfx)
    {
        this(id, EUIRM.getTexture(PGR.getRelicImage(id)), tier, sfx);
    }

    public PCLRelic(String id, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass playerClass)
    {
        this(id, EUIRM.getTexture(PGR.getRelicImage(id)), tier, sfx, playerClass);
    }

    public PCLRelic(String id, Texture texture, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass playerClass)
    {
        super(id, texture, tier, sfx);
        this.playerClass = playerClass;
    }

    public PCLRelic(String id, Texture texture, Texture outline, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass playerClass)
    {
        super(id, texture, outline, tier, sfx);
        this.playerClass = playerClass;
    }

    public PCLRelic(String id, String imgName, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass playerClass)
    {
        super(id, imgName, tier, sfx);
        this.playerClass = playerClass;
    }

    public static String createFullID(Class<? extends PCLRelic> type)
    {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?,?,?> resources, Class<? extends PCLRelic> type)
    {
        return resources.createID(type.getSimpleName());
    }

    protected void activateBattleEffect()
    {

    }

    public int addCounter(int amount)
    {
        setCounter(counter + amount);

        return counter;
    }

    protected void deactivateBattleEffect()
    {

    }

    protected void displayAboveCreature(AbstractCreature creature)
    {
        PCLActions.top.add(new RelicAboveCreatureAction(creature, this));
    }

    protected String formatDescription(int index, Object... args)
    {
        return EUIUtils.format(DESCRIPTIONS[index], args);
    }

    protected String getCounterString()
    {
        return String.valueOf(counter);
    }

    public TextureAtlas.AtlasRegion getPowerIcon()
    {
        final Texture texture = img;
        final int h = texture.getHeight();
        final int w = texture.getWidth();
        final int section = h / 2;
        return new TextureAtlas.AtlasRegion(texture, (w / 2) - (section / 2), (h / 2) - (section / 2), section, section);
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return tips;
    }

    @Override
    public List<EUITooltip> getTipsForFilters()
    {
        return tips.subList(1, tips.size());
    }

    public boolean isEnabled()
    {
        return !super.grayscale;
    }

    public boolean setEnabled(boolean value)
    {
        super.grayscale = !value;
        return value;
    }

    @Override
    public AbstractRelic makeCopy()
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

    @Override
    public final void updateDescription(AbstractPlayer.PlayerClass c)
    {
        this.description = getUpdatedDescription();
        this.mainTooltip.setDescriptions(description);
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, getValue());
    }

    @Override
    public void onEquip()
    {
        super.onEquip();

        if (GameUtilities.inBattle(true))
        {
            activateBattleEffect();
        }
    }

    @Override
    public void onUnequip()
    {
        super.onUnequip();

        if (GameUtilities.inBattle(true))
        {
            deactivateBattleEffect();
        }
    }

    @Override
    public void atPreBattle()
    {
        super.atPreBattle();

        activateBattleEffect();
    }

    @Override
    public void onVictory()
    {
        super.onVictory();

        deactivateBattleEffect();
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel)
    {
        if (this.counter >= 0)
        {
            final String text = getCounterString();
            if (inTopPanel)
            {
                float offsetX = ReflectionHacks.getPrivateStatic(AbstractRelic.class, "offsetX");
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, text,
                        offsetX + this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.WHITE);
            }
            else
            {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, text,
                        this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.WHITE);
            }
        }
    }

    @Override
    public void renderBossTip(SpriteBatch sb)
    {
        EUITooltip.queueTooltips(tips, Settings.WIDTH * 0.63F, Settings.HEIGHT * 0.63F);
    }

    @Override
    public void renderTip(SpriteBatch sb)
    {
        EUITooltip.queueTooltips(this);
    }

    @Override
    protected void initializeTips()
    {
        if (tips == null)
        {
            tips = new ArrayList<>();
        }
        else
        {
            tips.clear();
        }

        mainTooltip = playerClass != null ? new EUITooltip(name, this.playerClass, description) : new EUITooltip(name, description);
        tips.add(mainTooltip);
        EUIGameUtils.scanForTips(description, tips);
    }

    public int getValue()
    {
        return counter;
    }

    // Prevents duplicate relics from showing up for the Animator
    public boolean canSpawn()
    {
        return GameUtilities.isPCLPlayerClass() || (PGR.core.config.enableRelicsForOtherCharacters.get());
    }
}
