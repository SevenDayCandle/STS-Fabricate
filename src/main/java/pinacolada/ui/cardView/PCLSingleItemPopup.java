package pinacolada.ui.cardView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIControllerButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIVerticalScrollBar;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PGR;

import java.util.ArrayList;

import static pinacolada.skills.PSkill.COLON_SEPARATOR;

public abstract class PCLSingleItemPopup<T, U extends T> extends EUIBase {
    protected static final float TIP_RENDER_X = 0.75f * Settings.WIDTH;
    protected static final float DESC_LINE_SPACING = 30.0F * Settings.scale;
    protected static final float DESC_LINE_WIDTH = 418.0F * Settings.scale;
    protected static final float IMAGE_Y = (float) Settings.HEIGHT / 2.0F - 64.0F + 76.0F * Settings.scale;
    public static final float POPUP_TOOLTIP_Y_BASE = Settings.HEIGHT * 0.85f;
    protected final ArrayList<EUITooltip> tooltips = new ArrayList<>();
    protected final EUIHitbox popupHb;
    protected final EUIControllerButton nextButton;
    protected final EUIControllerButton prevButton;
    protected final EUIVerticalScrollBar scrollBar;
    protected final EUILabel artAuthorLabel;
    protected final EUILabel whatModLabel;
    protected float popupTooltipY = POPUP_TOOLTIP_Y_BASE;
    protected float popupTooltipYTarget = POPUP_TOOLTIP_Y_BASE;
    protected U currentItem;
    protected T prevItem;
    protected T nextItem;
    protected Color fadeColor = Color.BLACK.cpy();
    protected EUICardPreview preview;
    protected float fadeTimer = 0.0F;

    public PCLSingleItemPopup(EUIHitbox popupHb) {
        this.isActive = false;
        this.popupHb = popupHb;
        this.popupHb.move((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F);
        this.prevButton = new EUIControllerButton(CInputActionSet.pageLeftViewDeck, ImageMaster.POPUP_ARROW, new EUIHitbox(256.0F * Settings.scale, 256.0F * Settings.scale))
                .setOnClick(() -> openNext(prevItem));
        this.nextButton = new EUIControllerButton(CInputActionSet.pageRightViewExhaust, ImageMaster.POPUP_ARROW, new EUIHitbox(256.0F * Settings.scale, 256.0F * Settings.scale))
                .setButtonFlip(true, false)
                .setOnClick(() -> openNext(nextItem));
        this.prevButton.hb.move((float) Settings.WIDTH / 2.0F - 400.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F);
        this.nextButton.hb.move((float) Settings.WIDTH / 2.0F + 400.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F);

        this.scrollBar = new EUIVerticalScrollBar(new EUIHitbox(EUIGameUtils.screenW(0.96f), EUIGameUtils.screenH(0.15f), EUIGameUtils.screenW(0.026f), EUIGameUtils.screenH(0.7f))
                .setIsPopupCompatible(true))
                .setOnScroll(this::onScroll);

        this.artAuthorLabel = new EUILabel(EUIFontHelper.cardTooltipFont,
                new EUIHitbox(screenW(0.008f), screenH(0.91f), screenW(0.21f), screenH(0.07f)))
                .setAlignment(0.9f, 0.1f, true)
                .setLabel(PGR.core.strings.scp_artAuthor);

        this.whatModLabel = new EUILabel(EUIFontHelper.cardTooltipFont,
                new EUIHitbox(screenW(0.008f), screenH(0.89f), screenW(0.21f), screenH(0.07f)))
                .setAlignment(0.9f, 0.1f, true)
                .setLabel(PGR.core.strings.scp_artAuthor);
    }

    public void close() {
        InputHelper.justReleasedClickLeft = false;
        CardCrawlGame.isPopupOpen = false;
        this.isActive = false;
        this.currentItem = null;
    }

    public void forceUnfade() {
        this.fadeTimer = 0.0F;
        this.fadeColor.a = 0.9F;
    }

    protected Iterable<? extends EUITooltip> getTipsForRender(U currentItem) {
        return currentItem instanceof TooltipProvider ? ((TooltipProvider) currentItem).getTipsForRender() : new ArrayList<>();
    }

    protected void initializeLabels() {
        String author = getCredits(currentItem);
        artAuthorLabel.setLabel(author != null ? PGR.core.strings.scp_artAuthor + COLON_SEPARATOR + EUIUtils.modifyString(author, w -> "#y" + w) : "");

        ModInfo info = EUIGameUtils.getModInfo(currentItem);
        whatModLabel.setLabel(info != null ? EUIRM.strings.ui_origins + COLON_SEPARATOR + EUIUtils.modifyString(info.Name, w -> "#y" + w) : "");
    }

    protected void initializeTips() {
        tooltips.clear();
        for (EUITooltip tip : getTipsForRender(currentItem)) {
            if (tip.isRenderable()) {
                tooltips.add(tip);
            }
        }
        EUITooltip.scanListForAdditionalTips(tooltips);

        if (currentItem instanceof TooltipProvider) {
            preview = ((TooltipProvider) currentItem).getPreview();
        }

        scrollBar.scroll(0, true);
    }

    protected void onScroll(float scrollPercentage) {
        scrollBar.scroll(scrollPercentage, false);
        popupTooltipYTarget = POPUP_TOOLTIP_Y_BASE + Settings.HEIGHT * 0.1f * tooltips.size() * scrollPercentage;
    }

    protected void openImpl(U item, ArrayList<T> group) {
        currentItem = item;
        this.isActive = true;
        this.prevItem = null;
        this.nextItem = null;
        if (group != null) {
            for (int i = 0; i < group.size(); ++i) {
                if (group.get(i) == currentItem) {
                    if (i != 0) {
                        this.prevItem = group.get(i - 1);
                    }

                    if (i != group.size() - 1) {
                        this.nextItem = group.get(i + 1);
                    }
                    break;
                }
            }
        }
        this.prevButton.setActive(prevItem != null);
        this.nextButton.setActive(nextItem != null);
        this.prevButton.hb.unhover();
        this.nextButton.hb.unhover();
        this.fadeTimer = 0.25F;
        this.fadeColor.a = 0.0F;
        initializeTips();
        initializeLabels();
    }

    // Scroll updating should be handled in the individual popup because we may not always want to render/update it
    @Override
    public void renderImpl(SpriteBatch sb) {
        prevButton.tryRender(sb);
        nextButton.tryRender(sb);
        this.popupHb.render(sb);
        artAuthorLabel.renderImpl(sb);
        whatModLabel.renderImpl(sb);
        renderTips(sb);
    }

    @Override
    public void updateImpl() {
        this.popupHb.update();
        this.nextButton.tryUpdate();
        this.prevButton.tryUpdate();
        this.updateFade();
        this.updateInput();
        this.artAuthorLabel.tryUpdate();
        this.whatModLabel.tryUpdate();
    }

    protected void renderTips(SpriteBatch sb) {
        popupTooltipY = EUIUtils.lerpSnap(popupTooltipY, popupTooltipYTarget, 8);
        float y = popupTooltipY;
        for (int i = 0; i < tooltips.size(); i++) {
            EUITooltip tip = tooltips.get(i);
            if (StringUtils.isEmpty(tip.description)) {
                continue;
            }
            float projected = y - tip.getTotalHeight();
            y -= tip.render(sb, TIP_RENDER_X, y, i) + EUITooltip.BOX_EDGE_H * 3.15f;
        }

        if (preview != null) {
            preview.render(sb, popupHb.cX, popupHb.cY, 1f, EUIGameUtils.canShowUpgrades(false), true);
        }

        scrollBar.render(sb);
    }

    protected void updateFade() {
        this.fadeTimer -= Gdx.graphics.getDeltaTime();
        if (this.fadeTimer < 0.0F) {
            this.fadeTimer = 0.0F;
        }

        this.fadeColor.a = Interpolation.pow2In.apply(0.9F, 0.0F, this.fadeTimer * 4.0F);
    }

    protected void updateInput() {
        if (InputHelper.justClickedLeft) {
            if (!this.popupHb.hovered &&
                    !this.prevButton.hb.hovered && !this.nextButton.hb.hovered &&
                    !this.scrollBar.hb.hovered && !isHovered()) {
                close();
                InputHelper.justClickedLeft = false;
            }
        }
        else if (InputHelper.pressedEscape || CInputActionSet.cancel.isJustPressed()) {
            CInputActionSet.cancel.unpress();
            InputHelper.pressedEscape = false;
            close();
        }

        if (this.prevItem != null && InputActionSet.left.isJustPressed()) {
            openNext(prevItem);
        }
        else if (this.nextItem != null && InputActionSet.right.isJustPressed()) {
            openNext(nextItem);
        }
    }

    abstract protected String getCredits(U currentItem);

    abstract protected boolean isHovered();

    abstract protected void openNext(T relic);
}
