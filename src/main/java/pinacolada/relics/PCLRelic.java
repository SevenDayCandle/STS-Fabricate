package pinacolada.relics;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
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

import java.util.ArrayList;
import java.util.List;

public abstract class PCLRelic extends AbstractRelic implements KeywordProvider {
    public static AbstractPlayer player;
    public static Random rng;
    public final PCLRelicData relicData;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;

    public PCLRelic(PCLRelicData data) {
        super(data.ID, "", data.tier, data.sfx);
        this.relicData = data;
        loadImage(data.imagePath);
        initializePCLTips();
    }

    public static String createFullID(Class<? extends PCLRelic> type) {
        return createFullID(PGR.core, type);
    }

    public static String createFullID(PCLResources<?, ?, ?, ?> resources, Class<? extends PCLRelic> type) {
        return resources.createID(type.getSimpleName());
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
        loadImage(path, false);
    }

    public void loadImage(String path, boolean refresh) {
        Texture t = EUIRM.getTexture(path, true, refresh, true);
        if (t == null) {
            t = EUIRM.getLocalTexture(path, true, refresh, true);
            if (t == null) {
                path = PCLCoreImages.CardAffinity.unknown.path();
                t = EUIRM.getTexture(path, true, false, true);
            }
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

        AbstractPlayer.PlayerClass playerClass = EUIGameUtils.getPlayerClassForCardColor(relicData.cardColor);
        mainTooltip = playerClass != null ? new EUIKeywordTooltip(getName(), playerClass, description) : new EUIKeywordTooltip(getName(), description);
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
    public void onVictory() {
        super.onVictory();

        deactivateBattleEffect();
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {
        if (this.counter >= 0) {
            final String text = getCounterString();
            if (inTopPanel) {
                float offsetX = ReflectionHacks.getPrivateStatic(AbstractRelic.class, "offsetX");
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, text,
                        offsetX + this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.WHITE);
            }
            else {
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelInfoFont, text,
                        this.currentX + 30.0F * Settings.scale, this.currentY - 7.0F * Settings.scale, Color.WHITE);
            }
        }
    }

    @Override
    public void renderBossTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(tips, Settings.WIDTH * 0.63F, Settings.HEIGHT * 0.63F);
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(this);
    }

    @Override
    protected void initializeTips() {
        // No-op, use initializePCLTips() instead
    }

    public boolean canSpawn() {
        return relicData.cardColor.equals(GameUtilities.getActingColor());
    }
}
