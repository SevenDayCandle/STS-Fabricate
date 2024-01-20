package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.utilities.EUITextHelper;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIClassUtils;
import pinacolada.relics.PCLRelic;

import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.screens.SingleRelicViewPopup.TEXT;

public class PCLSingleRelicPopup extends PCLSingleItemPopup<AbstractRelic, AbstractRelic> {
    protected static final float DESC_LINE_SPACING = 30.0F * Settings.scale;
    protected static final float DESC_LINE_WIDTH = 418.0F * Settings.scale;
    protected static final float IMAGE_Y = (float) Settings.HEIGHT / 2.0F - 64.0F + 76.0F * Settings.scale;

    private Color relicRarityColor = Color.WHITE;
    private String relicName = EUIUtils.EMPTY_STRING;
    private String relicRarity = EUIUtils.EMPTY_STRING;
    private String relicDescription = EUIUtils.EMPTY_STRING;
    private String relicFlavor = EUIUtils.EMPTY_STRING;
    private Texture relicFrameImg = ImageMaster.WHITE_SQUARE_IMG;
    private float renderScale = 1f;
    private ArrayList<AbstractRelic> group;

    public PCLSingleRelicPopup() {
        super(new EUIHitbox(550.0F * Settings.scale, 680.0F * Settings.scale));
    }

    protected void actualClose() {
        super.actualClose();
        FontHelper.ClearSRVFontTextures();
    }

    @Override
    protected String getCredits(AbstractRelic currentItem) {
        return null;
    }

    protected String getFramePath(AbstractRelic current) {
        if (!current.isSeen) {
            return "images/ui/relicFrameCommon.png";
        }
        else {
            switch (current.tier) {
                case BOSS:
                    return "images/ui/relicFrameBoss.png";
                case COMMON:
                case DEPRECATED:
                case STARTER:
                    return "images/ui/relicFrameCommon.png";
                case RARE:
                case SHOP:
                case SPECIAL:
                    return "images/ui/relicFrameRare.png";
                case UNCOMMON:
                    return "images/ui/relicFrameUncommon.png";
            }
        }
        return "images/ui/relicFrameCommon.png";
    }

    protected Color getRelicColor(AbstractRelic relic) {
        switch (relic.tier) {
            case BOSS:
                return Settings.RED_TEXT_COLOR;
            case RARE:
            case SHOP:
            case SPECIAL:
                return Settings.GOLD_COLOR;
            case UNCOMMON:
                return Settings.BLUE_TEXT_COLOR;
            case DEPRECATED:
            default:
                return Settings.CREAM_COLOR;
        }
    }

    protected String getRelicDescription(AbstractRelic relic) {
        if (UnlockTracker.isRelicLocked(currentItem.relicId)) {
            return TEXT[11];
        }
        if (!currentItem.isSeen) {
            return TEXT[12];
        }
        return relic.description;
    }

    protected String getRelicFlavor(AbstractRelic relic) {
        if (relic.isSeen && relic.flavorText != null) {
            return relic.flavorText;
        }
        return EUIUtils.EMPTY_STRING;
    }

    protected String getRelicName(AbstractRelic current) {
        if (UnlockTracker.isRelicLocked(current.relicId)) {
            return TEXT[8];
        }
        if (!current.isSeen) {
            return TEXT[9];
        }
        return current.name;
    }

    protected String getRelicRarity(AbstractRelic current) {
        if (current.isSeen) {
            String base = EUIGameUtils.textForRelicTier(current.tier);
            return EUIRM.strings.adjNoun(base, TEXT[10]);
        }
        return "";
    }

    @Override
    protected Iterable<? extends EUITooltip> getTipsToDisplay(AbstractRelic currentItem) {
        return currentItem.isSeen ? super.getTipsToDisplay(currentItem) : Collections.emptyList();
    }

    @Override
    protected boolean isHovered() {
        return false;
    }

    public void open(AbstractRelic relic, ArrayList<AbstractRelic> group) {
        super.openImpl(relic, group);
        this.group = group;
        relic.playLandingSFX();
        relic.isSeen = UnlockTracker.isRelicSeen(relic.relicId) || relic.isSeen;
        this.relicFrameImg = EUIRM.getTexture(getFramePath(relic));
        setStrings();
        renderScale = relic instanceof PCLRelic ? 1f : 2f;
    }

    public void open(AbstractRelic relic) {
        open(relic, null);
    }

    protected void openNext(AbstractRelic relic) {
        this.close();
        CardCrawlGame.relicPopup.open(relic, this.group);
        forceUnfade();
        EUIClassUtils.setField(CardCrawlGame.relicPopup, "fadeTimer", 0f);
        Color otherFadeColor = EUIClassUtils.getField(CardCrawlGame.relicPopup, "fadeColor");
        otherFadeColor.a = 0.9f;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        sb.setColor(this.fadeColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);

        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.RELIC_POPUP, (float) Settings.WIDTH / 2.0F - 960.0F, (float) Settings.HEIGHT / 2.0F - 540.0F, 960.0F, 540.0F, 1920.0F, 1080.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);
        sb.draw(this.relicFrameImg, (float) Settings.WIDTH / 2.0F - 960.0F, (float) Settings.HEIGHT / 2.0F - 540.0F, 960.0F, 540.0F, 1920.0F, 1080.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

        this.renderRelicImage(sb);

        FontHelper.renderWrappedText(sb, FontHelper.cardDescFont_L, relicName, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F + 270.0F * Settings.scale, 9999.0F, Settings.CREAM_COLOR, 0.9F);
        FontHelper.renderWrappedText(sb, FontHelper.cardDescFont_N, relicRarity, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F + 235.0F * Settings.scale, 9999.0F, relicRarityColor, 1.0F);

        float height = EUITextHelper.getSmartHeight(FontHelper.cardDescFont_N, relicDescription, DESC_LINE_WIDTH, DESC_LINE_SPACING) / 2.0F;
        EUITextHelper.renderSmart(sb, FontHelper.cardDescFont_N, relicDescription, (float) Settings.WIDTH / 2.0F - 200.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F - 140.0F * Settings.scale - height, DESC_LINE_WIDTH, DESC_LINE_SPACING, Settings.CREAM_COLOR);

        FontHelper.renderWrappedText(sb, FontHelper.SRV_quoteFont, relicFlavor, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F - 310.0F * Settings.scale, DESC_LINE_WIDTH, Settings.CREAM_COLOR, 1.0F);

        this.renderTips(sb);

        super.renderImpl(sb);
        scrollBar.tryRender(sb);
    }

    private void renderRelicImage(SpriteBatch sb) {
        if (UnlockTracker.isRelicLocked(currentItem.relicId)) {
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.5F));
            sb.draw(ImageMaster.RELIC_LOCK_OUTLINE, (float) Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale * 2.0F, Settings.scale * 2.0F, 0.0F, 0, 0, 128, 128, false, false);
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.RELIC_LOCK, (float) Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale * 2.0F, Settings.scale * 2.0F, 0.0F, 0, 0, 128, 128, false, false);
        }
        else {
            if (!currentItem.isSeen) {
                sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.75F));
            }
            else {
                sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.5F));
            }

            if (currentItem instanceof PCLRelic) {
                // TODO move popup render logic to PCLRelic
                if (!currentItem.isSeen) {
                    sb.setColor(Color.BLACK);
                    EUIRenderHelpers.drawSilhouette(sb, Color.LIGHT_GRAY, s -> {
                        s.draw(currentItem.img, (float) Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale * renderScale, Settings.scale * renderScale, 0.0F, 0, 0, 128, 128, false, false);
                    });
                }
                else {
                    sb.setColor(Color.WHITE);
                    sb.draw(currentItem.img, (float) Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale * renderScale, Settings.scale * renderScale, 0.0F, 0, 0, 128, 128, false, false);
                }
            }
            else {
                sb.draw(currentItem.outlineImg, (float) Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale * renderScale, Settings.scale * renderScale, 0.0F, 0, 0, 128, 128, false, false);
                if (!currentItem.isSeen) {
                    sb.setColor(Color.BLACK);
                }
                else {
                    sb.setColor(Color.WHITE);
                }
                sb.draw(currentItem.img, (float) Settings.WIDTH / 2.0F - 64.0F, IMAGE_Y, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale * renderScale, Settings.scale * renderScale, 0.0F, 0, 0, 128, 128, false, false);
            }
        }
    }

    protected void setStrings() {
        relicName = getRelicName(currentItem);
        relicRarity = getRelicRarity(currentItem);
        relicDescription = getRelicDescription(currentItem);
        relicFlavor = getRelicFlavor(currentItem);
        relicRarityColor = getRelicColor(currentItem);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        scrollBar.update();
    }
}
