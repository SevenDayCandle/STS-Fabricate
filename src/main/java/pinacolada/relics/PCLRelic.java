package pinacolada.relics;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.FloatyEffect;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.*;

public abstract class PCLRelic extends AbstractRelic implements KeywordProvider {
    protected static EUITooltip hiddenTooltip;

    public static AbstractPlayer player;
    public static Random rng;
    public final PCLRelicData relicData;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;

    public PCLRelic(PCLRelicData data) {
        super(data.ID, "", data.tier, data.sfx);
        this.relicData = data;
        initializePCLTips();
        setupImages(data.imagePath);
    }

    public static String createFullID(Class<? extends PCLRelic> type) {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLRelic> type) {
        return resources.createID(type.getSimpleName());
    }

    public static EUITooltip getHiddenTooltip() {
        if (hiddenTooltip == null) {
            hiddenTooltip = new EUITooltip(LABEL[1], MSG[1]);
        }
        return hiddenTooltip;
    }

    protected static PCLRelicData register(Class<? extends PCLRelic> type) {
        return register(type, PGR.core);
    }

    protected static PCLRelicData register(Class<? extends PCLRelic> type, PCLResources<?, ?, ?, ?> resources) {
        return registerRelicData(new PCLRelicData(type, resources));
    }

    protected static <T extends PCLRelicData> T registerRelicData(T cardData) {
        return PCLRelicData.registerData(cardData);
    }

    protected static TemplateRelicData registerTemplate(Class<? extends PCLRelic> type) {
        return registerTemplate(type, PGR.core, type.getSimpleName());
    }

    protected static TemplateRelicData registerTemplate(Class<? extends PCLRelic> type, String sourceID) {
        return registerTemplate(type, PGR.core, sourceID);
    }

    protected static TemplateRelicData registerTemplate(Class<? extends PCLRelic> type, PCLResources<?, ?, ?, ?> resources, String sourceID) {
        return PCLRelicData.registerData(new TemplateRelicData(type, resources, sourceID));
    }

    protected void activateBattleEffect() {

    }

    public int addCounter(int amount) {
        setCounter(counter + amount);

        return counter;
    }

    public float atBlockModify(PCLUseInfo info, float block, AbstractCard c) {
        return atBlockModify(block, c);
    }

    public float atBlockModify(float block, AbstractCard c) {
        return block;
    }

    public float atDamageModify(PCLUseInfo info, float block, AbstractCard c) {
        return atDamageModify(block, c);
    }

    public float atHealModify(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float atHitCountModify(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float atMagicNumberModify(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    public float atRightCountModify(PCLUseInfo info, float block, AbstractCard c) {
        return block;
    }

    protected void deactivateBattleEffect() {

    }

    protected void displayAboveCreature(AbstractCreature creature) {
        PCLActions.top.add(new RelicAboveCreatureAction(creature, this));
    }

    protected String formatDescription(int index, Object... args) {
        return EUIUtils.format(getDescriptions()[index], args);
    }

    protected String getCounterString() {
        return String.valueOf(counter);
    }

    public String[] getDescriptions() {
        return DESCRIPTIONS;
    }

    public TextureAtlas.AtlasRegion getPowerIcon() {
        final Texture texture = img;
        final int h = texture.getHeight();
        final int w = texture.getWidth();
        final int section = h / 2;
        return new TextureAtlas.AtlasRegion(texture, (w / 2) - (section / 2), (h / 2) - (section / 2), section, section);
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForFilters() {
        return tips.subList(1, tips.size());
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips;
    }

    public String getName() {
        return relicData.strings.NAME;
    }

    public int getValue() {
        return counter;
    }

    public void loadImage(String path) {
        Texture t = EUIRM.getTexture(path, true, false);
        if (t == null) {
            path = PCLCoreImages.CardAffinity.unknown.path();
            t = EUIRM.getTexture(path, true, true);
        }
        this.img = t;
        this.outlineImg = t;
    }

    // Initialize later to ensure relicData is set
    public void initializePCLTips() {
        if (tips == null) {
            tips = new ArrayList<>();
        }
        else {
            tips.clear();
        }

        ModInfo info = EUIGameUtils.getModInfo(this);
        mainTooltip = info != null ? new EUIKeywordTooltip(getName(), description, info.ID) : new EUIKeywordTooltip(getName(), description);
        tips.add(mainTooltip);
        EUIGameUtils.scanForTips(description, tips);
    }

    public boolean isEnabled() {
        return !super.grayscale;
    }

    @Override
    public PCLRelic makeCopy() {
        try {
            return relicData.create();
        }
        catch (Exception e) {
            return null;
        }
    }

    public boolean setEnabled(boolean value) {
        super.grayscale = !value;
        return value;
    }

    public void setupImages(String imagePath) {
        loadImage(imagePath);
    }

    @Override
    public final void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = getUpdatedDescription();
        if (this.mainTooltip != null)
        {
            this.mainTooltip.setDescription(description);
        }
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, getValue());
    }

    @Override
    public void onEquip() {
        super.onEquip();

        if (GameUtilities.inBattle(true)) {
            activateBattleEffect();
        }
    }

    @Override
    public void onUnequip() {
        super.onUnequip();

        if (GameUtilities.inBattle(true)) {
            deactivateBattleEffect();
        }
    }

    @Override
    public void atPreBattle() {
        super.atPreBattle();

        activateBattleEffect();
    }

    @Override
    public void obtain()
    {
        String[] replacements = getReplacementIDs();
        if (replacements != null)
        {
            Set<String> ids = new HashSet<>(Arrays.asList(replacements));
            ArrayList<AbstractRelic> relics = player.relics;
            for (int i = 0; i < relics.size(); i++)
            {
                if (ids.contains(relics.get(i).relicId))
                {
                    instantObtain(player, i, true);
                    setCounter(relics.get(i).counter);
                    return;
                }
            }
        }

        super.obtain();
    }

    @Override
    public void onVictory() {
        super.onVictory();

        deactivateBattleEffect();
    }

    @Override
    public void renderBossTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(tips, Settings.WIDTH * 0.63F, Settings.HEIGHT * 0.63F);
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(this);
    }

    public void renderUnseenTip() {
        EUITooltip.queueTooltip(getHiddenTooltip());
    }

    @Override
    protected void initializeTips() {
        // No-op, use initializePCLTips() instead
    }

    public void renderInTopPanel(SpriteBatch sb) {
        if (!Settings.hideRelics) {
            PCLRenderHelpers.drawGrayscaleIf(sb, s -> renderRelicImage(s, Color.WHITE, getOffsetX() - 64f, -64f,0.5f), grayscale);
            this.renderCounter(sb, true);
            this.renderFlash(sb, true);
            this.hb.render(sb);
        }
    }

    public void renderWithoutAmount(SpriteBatch sb, Color c) {
        renderRelicImage(sb, Color.WHITE, -64f, -64f, 0.5f);
        if (this.hb.hovered) {
            this.renderTip(sb);
        }
        this.hb.render(sb);
    }

    public void render(SpriteBatch sb) {
        if (!Settings.hideRelics) {
            float xOffset = -64;
            float yOffset = -64;

            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD && !isObtained) {
                FloatyEffect f_effect = getFEffect();
                xOffset += f_effect.x;
                yOffset += f_effect.y;
            }

            renderRelicImage(sb, Color.WHITE, xOffset, yOffset, 0.5f);
            renderCounter(sb, false);
            if (this.isDone) {
                renderFlash(sb, false);
            }
            if (this.hb.hovered && !this.isObtained && (!AbstractDungeon.isScreenUp || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP)) {
                this.renderBossTip(sb);
            }

            this.hb.render(sb);
        }
    }

    public void render(SpriteBatch sb, boolean renderAmount, Color outlineColor) {
        renderRelicImage(sb,
                this.isSeen ? Color.WHITE : this.hb.hovered ? Settings.HALF_TRANSPARENT_BLACK_COLOR : Color.BLACK,
                -64f,
                -64f,
                AbstractDungeon.screen == AbstractDungeon.CurrentScreen.NEOW_UNLOCK ? MathUtils.cosDeg((float)(System.currentTimeMillis() / 5L % 360L)) : 0.5f);
        renderHoverTip(sb);
        this.hb.render(sb);
    }


    public void renderHoverTip(SpriteBatch sb) {
        if (this.hb.hovered && !CardCrawlGame.relicPopup.isOpen) {
            if (!this.isSeen) {
                renderUnseenTip();
            }
            else {
                this.renderTip(sb);
            }
        }
    }

    public void renderRelicImage(SpriteBatch sb, Color color, float xOffset, float yOffset, float scaleMult) {
        sb.setColor(color);
        sb.draw(this.img, this.currentX + xOffset, this.currentY + yOffset, 64.0F, 64.0F, 128.0F, 128.0F, this.scale * scaleMult, this.scale * scaleMult, getRotation(), 0, 0, 128, 128, false, false);
    }

    protected void updateFlash() {
        if (this.flashTimer != 0.0F) {
            this.flashTimer -= Gdx.graphics.getDeltaTime();
            if (this.flashTimer < 0.0F) {
                if (this.pulse) {
                    this.flashTimer = 1.0F;
                } else {
                    this.flashTimer = 0.0F;
                }
            }
        }
    }

    protected FloatyEffect getFEffect() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "f_effect");
    }

    protected float getOffsetX() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "offsetX");
    }

    protected float getRotation() {
        return ReflectionHacks.getPrivate(this, AbstractRelic.class, "rotation");
    }

    public boolean canSpawn() {
        return relicData.cardColor == AbstractCard.CardColor.COLORLESS || relicData.cardColor.equals(GameUtilities.getActingColor());
    }

    // Relics that are replaced with this one when obtained
    public String[] getReplacementIDs() {return null;}
}
