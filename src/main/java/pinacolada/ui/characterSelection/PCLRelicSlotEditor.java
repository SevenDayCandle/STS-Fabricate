package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIRelic;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.loadout.PCLRelicSlot;
import pinacolada.resources.pcl.PCLCoreImages;

// Copied and modified from STS-AnimatorMod
public class PCLRelicSlotEditor extends EUIBase
{
    public static final float SPACING = 64f * Settings.scale;
    public static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    protected static final float CARD_SCALE = 0.75f;
    public PCLRelicSlot slot;
    public PCLLoadoutEditor loadoutEditor;

    protected EUITextBox relicnameText;
    protected EUITextBox relicvalueText;
    protected EUIButton changeButton;
    protected EUIButton clearButton;
    protected EUIRelic relicImage;
    protected PCLRelic relic;

    public PCLRelicSlotEditor(PCLLoadoutEditor loadoutEditor, float cX, float cY)
    {
        this.loadoutEditor = loadoutEditor;

        relicvalueText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(cX, cY, AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);

        relicnameText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(relicvalueText.hb.x + relicvalueText.hb.width + SPACING, cY, AbstractCard.IMG_WIDTH * 1.1f, ITEM_HEIGHT))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontNormal, 1f);

        clearButton = new EUIButton(EUIRM.images.x.texture(), new EUIHitbox(relicnameText.hb.x + relicnameText.hb.width, relicnameText.hb.y, 64, 64))
                .setClickDelay(0.02f);
        changeButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new EUIHitbox(clearButton.hb.x + clearButton.hb.width, relicnameText.hb.y, 64, 64))
                .setClickDelay(0.02f);

        setSlot(null);
    }

    public PCLRelicSlotEditor setSlot(PCLRelicSlot slot)
    {
        if (slot == null)
        {
            this.slot = null;
            this.relic = null;
            this.relicnameText.setActive(false);
            this.relicvalueText.setActive(false);
            this.changeButton.setActive(false);
            this.clearButton.setActive(false);
            return this;
        }

        final boolean change = slot.relics.size() > 1;

        this.slot = slot;
        this.relic = slot.getRelic();
        this.relicnameText.setLabel(relic != null ? relic.name : "").setActive(true);
        this.relicvalueText.setActive(true);
        this.clearButton.setOnClick(() -> {
            this.slot.clear();
            this.relicnameText.setLabel("");
            this.relicImage = null;
        }).setInteractable(slot.canRemove()).setActive(relic != null);
        this.changeButton.setOnClick(() -> loadoutEditor.trySelectRelic(this.slot)).setActive(change);
        if (relic != null)
        {
            this.relicImage = new EUIRelic(relic, new EUIHitbox(relicvalueText.hb.x + relicvalueText.hb.width + SPACING / 2, relicvalueText.hb.y, relic.hb.width, relic.hb.height));
        }
        else
        {
            this.relicImage = null;
        }

        return this;
    }

    public PCLRelicSlotEditor translate(float cX, float cY)
    {
        relicvalueText.setPosition(cX, cY);
        relicnameText.setPosition(relicvalueText.hb.x + relicvalueText.hb.width + SPACING, cY);
        clearButton.setPosition(relicnameText.hb.x + relicnameText.hb.width, cY);
        changeButton.setPosition(clearButton.hb.x + clearButton.hb.width, cY);
        if (relic != null && this.relicImage != null)
        {
            this.relicImage.translate(relicvalueText.hb.x + relicvalueText.hb.width + SPACING / 2, relicvalueText.hb.y);
        }

        return this;
    }

    @Override
    public void updateImpl()
    {
        if (slot == null)
        {
            return;
        }
        relicnameText.tryUpdate();

        if (changeButton.isActive && relicnameText.hb.hovered)
        {
            if (InputHelper.justClickedLeft)
            {
                relicnameText.hb.clickStarted = true;
            }

            if (relicnameText.hb.clicked)
            {
                relicnameText.hb.clicked = false;
                loadoutEditor.trySelectRelic(this.slot);
                return;
            }

            relicnameText.setFontColor(Color.WHITE);
        }
        else
        {
            relicnameText.setFontColor(Color.GOLD);
        }

        relic = slot.getRelic();
        if (relic != null && this.relicImage != null)
        {
            relicImage.translate(relicvalueText.hb.x + relicvalueText.hb.width, relicvalueText.hb.y);
            relicImage.updateImpl();
        }

        int value = slot.getEstimatedValue();
        relicvalueText.setLabel(value)
                .setFontColor(value == 0 ? Settings.CREAM_COLOR : value < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                .tryUpdate();

        if (changeButton.isActive)
        {
            changeButton.updateImpl();
        }
        if (clearButton.isActive)
        {
            clearButton.setInteractable(slot.canRemove()).updateImpl();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        relicnameText.tryRender(sb);
        if (this.relicImage != null)
        {
            relicImage.renderImpl(sb);
        }
        relicvalueText.tryRender(sb);
        changeButton.tryRender(sb);
        clearButton.tryRender(sb);
    }
}