package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.effects.screen.PCLRelicSlotSelectionEffect;
import pinacolada.resources.loadout.LoadoutRelicSlot;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLRelicSlotList extends EUICanvasGrid {
    private static final EUITextBox cardValue_text = new
            EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(AbstractCard.IMG_WIDTH * 0.15f, AbstractCard.IMG_HEIGHT * 0.15f))
            .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.5f, 0.5f)
            .setFont(EUIFontHelper.cardTitleFontSmall, 1f);
    public static final float TARGET_X = Settings.WIDTH * 0.25f;
    public static final float START_XY = Settings.WIDTH * 0.5f;
    public final ArrayList<RenderItem> relics = new ArrayList<>();

    public PCLRelicSlotList() {
        super(1, 0.05f * Settings.HEIGHT);
    }

    public void addListItem(LoadoutRelicSlot.Item item) {
        this.relics.add(new RenderItem(item, this.relics.size()));
    }

    public void clear() {
        relics.clear();
    }

    @Override
    public int currentSize() {
        return relics.size();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        for (RenderItem item : relics) {
            item.relicImage.tryRender(sb);
            cardValue_text
                    .setLabel(item.estimatedValue)
                    .setFontColor(item.estimatedValue < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                    .setPosition(item.relicImage.hb.x - 40 * Settings.scale, item.relicImage.hb.cY)
                    .renderImpl(sb);
            item.relicNameText.renderImpl(sb);
        }
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        int row = 0;
        int column = 0;

        for (RenderItem item : relics) {
            item.update();
        }
    }


    public class RenderItem {
        public static final int LERP_SPEED = 8;
        public float targetY;
        public final int estimatedValue;
        public final int index;
        public final EUIRelic relicImage;
        public final AbstractRelic relic;
        public final EUILabel relicNameText = new EUILabel(FontHelper.cardTitleFont, new EUIHitbox(AbstractCard.IMG_WIDTH, AbstractCard.IMG_HEIGHT * 0.15f))
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.01f);

        public RenderItem(LoadoutRelicSlot.Item item, int index) {
            this.relic = item.relic;
            this.estimatedValue = item.estimatedValue;
            this.relicImage = new EUIRelic(relic, new EUIHitbox(START_XY, START_XY, item.relic.hb.width, item.relic.hb.height));
            this.index = index;
            this.relicNameText.setLabel(GameUtilities.getRelicName(item.relic));
        }

        public void update() {
            this.targetY = Settings.HEIGHT * (0.8f - (index * 0.05f)) + scrollDelta;
            float newX = EUIUtils.lerpSnap(relicImage.hb.x, TARGET_X, LERP_SPEED);
            float newY = EUIUtils.lerpSnap(relicImage.hb.y, targetY, LERP_SPEED);
            this.relicImage.translate(newX, newY);
            this.relicNameText.setPosition(relicImage.hb.x + 256 * Settings.scale, relicImage.hb.cY);
            this.relicNameText.tryUpdate();
            this.relicImage.tryUpdate();

            if (relicImage.hb.hovered || relicNameText.hb.hovered) {
                relicNameText.setColor(Color.WHITE);
            }
            else {
                relicNameText.setColor(Color.GOLD);
            }
        }
    }
}
