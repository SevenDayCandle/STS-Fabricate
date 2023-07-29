package pinacolada.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIColors;

// Copied and modified from STS-AnimatorMod
public abstract class EUICardDraggable<T extends AbstractCard> extends EUIBase {
    protected static final float EPSILON = 0.00001f;
    protected final STSConfigItem<Vector2> config;
    protected final EUIImage draggablePanel;
    protected final EUIImage draggableIcon;
    private AbstractCard lastCard;
    private AbstractCreature lastTarget;
    protected Vector2 meterSavedPosition;
    public DraggableHitbox hb;

    public EUICardDraggable(STSConfigItem<Vector2> config, DraggableHitbox hb, float iconSize) {
        this.config = config;
        this.hb = hb;
        this.hb.setBounds(hb.width * 0.6f, Settings.WIDTH - (hb.width * 0.6f), screenH(0.35f), screenH(0.85f))
                .setOnDragFinish(this::savePosition);
        draggablePanel = new EUIImage(EUIRM.images.panelRounded.texture(), hb)
                .setColor(0.05f, 0.05f, 0.05f, 0.5f);
        draggableIcon = new EUIImage(EUIRM.images.draggable.texture(), new RelativeHitbox(hb, scale(40f), scale(40f), iconSize / 2, iconSize / 2))
                .setColor(EUIColors.white(0.3f));
    }

    protected AbstractCard getLastCard() {
        return lastCard;
    }

    protected AbstractCreature getLastTarget() {
        return lastTarget;
    }

    public void initialize() {
        lastCard = null;
        lastTarget = null;
        if (meterSavedPosition == null && config != null) {
            meterSavedPosition = config.get().cpy();
            hb.setCenter(screenW(meterSavedPosition.x), screenH(meterSavedPosition.y));
            EUIUtils.logInfoIfDebug(this, "Loaded position: " + meterSavedPosition);
        }
    }

    public boolean isHovered() {
        return draggablePanel.hb.hovered;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.hb.render(sb);
        draggablePanel.renderImpl(sb);
        draggableIcon.renderImpl(sb);
    }

    public final void updateImpl() {
    } // This should not be called

    protected void savePosition(DraggableHitbox hb) {

        if (meterSavedPosition != null && config != null) {
            meterSavedPosition.x = hb.targetCx / (float) Settings.WIDTH;
            meterSavedPosition.y = hb.targetCy / (float) Settings.HEIGHT;
            if (meterSavedPosition.dst2(config.get().cpy()) > EPSILON) {
                EUIUtils.logInfoIfDebug(this, "Saved position: " + meterSavedPosition);
                config.set(meterSavedPosition.cpy());
            }
        }

    }

    public EUICardDraggable<T> setDimensions(float width, float height) {
        this.hb.resize(width, height);
        return this;
    }

    public EUICardDraggable<T> setHitbox(DraggableHitbox hb) {
        this.hb = hb;
        return this;
    }

    public EUICardDraggable<T> setPosition(float cX, float cY) {
        this.hb.move(cX, cY);
        return this;
    }

    public EUICardDraggable<T> setTargetPosition(float cX, float cY) {
        this.hb.setTargetCenter(cX, cY);
        return this;
    }

    public EUICardDraggable<T> translate(float x, float y) {
        this.hb.translate(x, y);
        return this;
    }

    public void update(T card, T originalCard, AbstractCreature target, AbstractCreature originalTarget, boolean draggingCard) {
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.NONE) {
            hb.update();
            draggablePanel.tryUpdate();
            draggableIcon.tryUpdate();
            boolean isHovered = isHovered();
            draggablePanel.setColor(0.05f, 0.05f, 0.05f, isHovered ? 0.5f : 0.05f);
            draggableIcon.setColor(EUIColors.white(isHovered ? 1f : 0.3f));
            updateImpl(card, originalCard, target, originalTarget, draggingCard, lastCard != card, lastTarget != target);
            lastCard = card;
            lastTarget = target;
        }
    }

    public abstract void updateImpl(T card, T originalCard, AbstractCreature target, AbstractCreature originalTarget, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget);
}
