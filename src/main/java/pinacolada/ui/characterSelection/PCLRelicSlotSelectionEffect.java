package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIRelic;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffect;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.loadout.PCLRelicSlot;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLRelicSlotSelectionEffect extends PCLEffect
{
    public static final float TARGET_X = Settings.WIDTH * 0.25f;
    public static final float START_XY = Settings.WIDTH * 0.5f;

    private static final EUITextBox cardValue_text = new
            EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(AbstractCard.IMG_WIDTH * 0.15f, AbstractCard.IMG_HEIGHT * 0.15f))
            .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.5f, 0.5f)
            .setFont(EUIFontHelper.cardtitlefontSmall, 1f);

    private final PCLRelicSlot slot;
    private final ArrayList<RenderItem> relics = new ArrayList<>();
    //private boolean draggingScreen = false;
    private PCLRelic selectedRelic;

    public PCLRelicSlotSelectionEffect(PCLRelicSlot slot)
    {
        super(0.7f, true);

        this.selectedRelic = slot.getRelic();
        this.slot = slot;
        final ArrayList<PCLRelicSlot.Item> slotItems = slot.getSelectableRelics();
        for (int i = 0; i < slotItems.size(); i++)
        {
            this.relics.add(new RenderItem(slotItems.get(i), i));
        }

        if (relics.isEmpty())
        {
            complete();
        }
    }

    private void onRelicClicked(PCLRelic relic)
    {
        if (selectedRelic != null)
        {
            selectedRelic.stopPulse();

            if (selectedRelic == relic)
            {
                slot.select((PCLRelic) null);
                selectedRelic = null;
                return;
            }
        }

        selectedRelic = relic;
        CardCrawlGame.sound.play("CARD_SELECT");
        slot.select(relic);
        relic.beginLongPulse();
    }

    @Override
    public void render(SpriteBatch sb)
    {

        for (RenderItem item : relics)
        {
            item.relicImage.tryRender(sb);
            cardValue_text
                    .setLabel(item.estimatedValue)
                    .setFontColor(item.estimatedValue < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                    .setPosition(item.relicImage.hb.x - 40 * Settings.scale, item.relicImage.hb.cY)
                    .renderImpl(sb);
            item.relicnameText.renderImpl(sb);
        }
    }

    @Override
    protected void firstUpdate()
    {
        super.firstUpdate();

        if (selectedRelic != null)
        {
            for (RenderItem item : relics)
            {
                if (item.relic.relicId.equals(selectedRelic.relicId))
                {
                    selectedRelic = item.relic;
                    selectedRelic.beginLongPulse();
                    break;
                }
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        for (RenderItem item : relics)
        {
            item.update(deltaTime);
            if (item.relicImage.hb.hovered || item.relicnameText.hb.hovered)
            {
                item.relicnameText.setColor(Color.WHITE);
            }
            else
            {
                item.relicnameText.setColor(Color.GOLD);
            }
        }

        if (tickDuration(deltaTime))
        {
            if (EUIInputManager.leftClick.isJustReleased())
            {
                for (RenderItem item : relics)
                {
                    if (item.relicImage.hb.hovered || item.relicnameText.hb.hovered)
                    {
                        onRelicClicked(item.relic);
                    }
                }
                complete();
                return;
            }

            isDone = false;
        }
    }

    @Override
    protected void complete()
    {
        super.complete();

        if (selectedRelic != null && !selectedRelic.relicId.equals(slot.getRelic().relicId))
        {
            slot.select(selectedRelic);
        }
    }

    public static class RenderItem
    {
        public final float targetX;
        public final float targetY;
        public final int estimatedValue;
        public final EUIRelic relicImage;
        public final PCLRelic relic;
        public final EUILabel relicnameText = new EUILabel(FontHelper.cardTitleFont, new EUIHitbox(AbstractCard.IMG_WIDTH, AbstractCard.IMG_HEIGHT * 0.15f))
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.01f);
        public float animTimer;
        public float duration = 0.2f;

        public RenderItem(PCLRelicSlot.Item item, int index)
        {
            this.relic = item.relic;
            this.estimatedValue = item.estimatedValue;
            this.relicImage = new EUIRelic(relic, new EUIHitbox(Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f, item.relic.hb.width, item.relic.hb.height));
            this.targetX = TARGET_X;
            this.targetY = Settings.HEIGHT * (0.8f - (index * 0.05f));
            this.relicnameText.setLabel(item.relic.name);
        }

        public void update(float deltaTime)
        {
            this.animTimer += deltaTime;
            if (this.animTimer <= duration)
            {
                float newX = Interpolation.pow2.apply(START_XY, targetX, this.animTimer / duration);
                float newY = Interpolation.pow2.apply(START_XY, targetY, this.animTimer / duration);
                this.relicImage.translate(newX, newY);
                this.relicnameText.setPosition(relicImage.hb.x + 256 * Settings.scale, relicImage.hb.cY);
            }
            this.relicnameText.tryUpdate();
            this.relicImage.tryUpdate();
        }
    }
}