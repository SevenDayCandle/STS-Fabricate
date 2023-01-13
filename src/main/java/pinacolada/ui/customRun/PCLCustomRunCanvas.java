package pinacolada.ui.customRun;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.RunModStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;
import pinacolada.ui.characterSelection.PCLCharacterSelectOptionsRenderer;
import pinacolada.ui.characterSelection.PCLGlyphEditor;
import pinacolada.ui.common.PCLValueEditor;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;


public class PCLCustomRunCanvas extends EUICanvas
{
    protected static final float SCREEN_X = Settings.WIDTH * 0.25f;
    protected static final float SCREEN_Y = Settings.HEIGHT * 0.9f;
    protected static final float GAP_Y = scale(160);
    protected static final int ROW_SIZE = 10;
    // Dammit basegame why hardcoded strings -_-
    protected static final String MOD_ENDLESS = "Endless";
    protected static final String MOD_THE_ENDING = "The Ending";

    public final PCLCustomRunScreen screen;
    public final EUILabel titleLabel;
    public final EUILabel trophiesLabel;
    public final EUILabel charTitleLabel;
    public final EUILabel modifiersLabel;
    public final EUITextBoxInput seedInput;
    public final PCLValueEditor ascensionEditor;
    public final EUILabel selectedCharacterLabel;
    public final EUIToggle endlessToggle;
    public final EUIToggle endingActToggle;
    public final EUIToggle customCardToggle;
    public final ArrayList<PCLCustomRunCharacterButton> characters = new ArrayList<>();
    public final EUISearchableDropdown<CustomMod> modifierDropdown;
    public final MenuCancelButton cancelButton = new MenuCancelButton();
    public final GridSelectConfirmButton confirmButton;
    public final PCLCharacterSelectOptionsRenderer loadoutRenderer = new PCLCharacterSelectOptionsRenderer();

    public PCLCustomRunCanvas(PCLCustomRunScreen screen)
    {
        super();
        this.screen = screen;

        titleLabel = new EUILabel(FontHelper.charTitleFont, new EUIHitbox(screenW(0.18f), screenH(0.05f)))
                .setColor(Settings.GOLD_COLOR)
                .setLabel(CustomModeScreen.TEXT[0]);
        charTitleLabel = new EUILabel(FontHelper.panelNameFont, new EUIHitbox(screenW(0.18f), screenH(0.05f)))
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setLabel(CustomModeScreen.TEXT[2]);
        modifiersLabel = new EUILabel(FontHelper.panelNameFont, new EUIHitbox(screenW(0.18f), screenH(0.05f)))
                .setColor(Settings.GOLD_COLOR)
                .setLabel(MenuButton.TEXT[12]);

        trophiesLabel = new EUILabel(FontHelper.tipBodyFont, new EUIHitbox(screenW(0.3f), screenH(0.05f)))
                .setColor(Settings.RED_TEXT_COLOR)
                .setLabel(CustomModeScreen.TEXT[1]);
        selectedCharacterLabel = new EUILabel(EUIFontHelper.cardtitlefontSmall, new EUIHitbox(screenW(0.18f), screenH(0.05f)))
                .setColor(Settings.BLUE_TEXT_COLOR);

        RunModStrings endlessStrings = PGR.getRunModStrings(MOD_ENDLESS);
        RunModStrings endingActStrings = PGR.getRunModStrings(MOD_THE_ENDING);

        endlessToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.475f)
                .setText(endlessStrings.NAME)
                .setOnToggle(v -> {
                    screen.isEndless = v;
                })
                .setTooltip(endlessStrings.NAME, endlessStrings.DESCRIPTION);

        endingActToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.475f)
                .setText(endingActStrings.NAME)
                .setOnToggle(v -> {
                    screen.isFinalActAvailable = v;
                })
                .setTooltip(endingActStrings.NAME, endingActStrings.DESCRIPTION);

        customCardToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.475f)
                .setText(PGR.core.strings.cardEditor.customCards)
                .setOnToggle(v -> {
                    screen.allowCustomCards = v;
                })
                .setTooltip(PGR.core.strings.cardEditor.customCards, PGR.core.strings.misc.allowCustomCards);

        seedInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.panel.texture(),
                new EUIHitbox(scale(280), scale(48)))
                .setOnComplete(screen::setSeed)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[7])
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 0.9f);
        seedInput.header.setAlignment(1f, 0);

        ascensionEditor = new PCLValueEditor(new EUIHitbox(scale(64), scale(48)), CustomModeScreen.TEXT[3], screen::setAscension)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[3])
                .setLimits(0, 20)
                .setTooltip(CustomModeScreen.TEXT[3], "");
        ascensionEditor.header.setAlignment(0.4f, 0f);
        loadoutRenderer.ascensionGlyphsLabel.setFont(EUIFontHelper.cardtitlefontSmall, 1f);

        modifierDropdown = (EUISearchableDropdown<CustomMod>) new EUISearchableDropdown<CustomMod>(new EUIHitbox(scale(128), scale(48)))
                .setRowFunction(PCLCustomModDropdownRow::new)
                .setOnChange(items -> this.screen.activeMods = items)
                .setIsMultiSelect(true)
                .setHeader(FontHelper.panelNameFont, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[6])
                .setCanAutosize(true, true);

        this.confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);
        this.confirmButton.isDisabled = false;
        this.scrollBar.setOnScroll(this::onScroll);

    }

    public void open()
    {
        confirmButton.show();
        cancelButton.show(CharacterSelectScreen.TEXT[5]);
    }

    protected float positionElement(EUIHoverable element, float yPos)
    {
        return positionElement(element, yPos, GAP_Y);
    }

    protected float positionElement(EUIHoverable element, float yPos, float diff)
    {
        return positionElement(element, SCREEN_X, yPos, diff);
    }

    protected float positionElement(EUIHoverable element, float xPos, float yPos, float diff)
    {
        element.setPosition(xPos, yPos);
        return yPos - diff;
    }

    public void resetPositions()
    {
        scrollBar.scroll(0, true);
    }

    public void setAscension(int i)
    {
        ascensionEditor.setValue(i, false);
        int value = ascensionEditor.getValue();
        ascensionEditor.tooltip.setTitle(EUIRM.strings.generic2(CustomModeScreen.TEXT[3], value));
        ascensionEditor.tooltip.setDescription(value > 0 ? CharacterSelectScreen.A_TEXT[value - 1] : "");
        loadoutRenderer.updateForAscension();
    }

    public void setCharacter(CharacterOption c)
    {
        selectedCharacterLabel.setLabel(c.c.getLocalizedCharacterName());
        for (PCLCustomRunCharacterButton button : characters)
        {
            button.glowing = (button.character == c);
        }
        loadoutRenderer.refresh(screen, c);
        ascensionEditor.setLimits(0, GameUtilities.getMaxAscensionLevel(c.c)).setValue(screen.ascensionLevel);
    }

    public void setup(CustomModeScreen original)
    {
        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters())
        {
            characters.add(new PCLCustomRunCharacterButton(screen, p));
        }

        // Snag the modifiers from the original custom run screen, excluding endless/ending act because we handle this manually
        ArrayList<CustomMod> modList = EUIClassUtils.getField(original, "modList");
        modifierDropdown.setItems(EUIUtils.filter(modList, mod -> !MOD_ENDLESS.equals(mod.ID) && !MOD_THE_ENDING.equals(mod.ID)));

        setCharacter(characters.get(0).character);
    }

    public void updateImpl()
    {
        super.updateImpl();
        titleLabel.tryUpdate();
        charTitleLabel.tryUpdate();
        modifiersLabel.tryUpdate();
        trophiesLabel.tryUpdate();
        selectedCharacterLabel.tryUpdate();
        endlessToggle.tryUpdate();
        endingActToggle.tryUpdate();
        customCardToggle.tryUpdate();
        seedInput.tryUpdate();
        ascensionEditor.tryUpdate();
        modifierDropdown.tryUpdate();
        loadoutRenderer.updateImpl();
        confirmButton.update();
        cancelButton.update();
        for (PCLCustomRunCharacterButton b : characters)
        {
            b.tryUpdate();
        }

        if (this.cancelButton.hb.clicked || InputHelper.pressedEscape)
        {
            InputHelper.pressedEscape = false;
            this.cancelButton.hb.clicked = false;
            this.cancelButton.hide();
            CardCrawlGame.mainMenuScreen.panelScreen.refresh();
        }
        else if (this.confirmButton.hb.clicked || CInputActionSet.proceed.isJustPressed())
        {
            this.confirmButton.hb.clicked = false;
            this.screen.confirm();
        }
    }

    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        titleLabel.tryRender(sb);
        charTitleLabel.tryRender(sb);
        modifiersLabel.tryRender(sb);
        trophiesLabel.tryRender(sb);
        selectedCharacterLabel.tryRender(sb);
        endlessToggle.tryRender(sb);
        endingActToggle.tryRender(sb);
        customCardToggle.tryRender(sb);
        seedInput.tryRender(sb);
        ascensionEditor.tryRender(sb);
        modifierDropdown.tryRender(sb);
        for (PCLCustomRunCharacterButton b : characters)
        {
            b.tryRenderCentered(sb);
        }
        loadoutRenderer.renderImpl(sb);
        confirmButton.render(sb);
        cancelButton.render(sb);
    }

    protected void onScroll(float newPercent)
    {
        super.onScroll(newPercent);
        updatePositions();
    }

    protected void updatePositions()
    {
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT + scale(550);
        float yPos = SCREEN_Y;
        yPos = positionElement(titleLabel, SCREEN_X - scale(80), yPos, scale(10));
        yPos = positionElement(trophiesLabel, titleLabel.hb.cX + titleLabel.getAutoWidth() + scale(160), yPos, scale(80));
        yPos = positionElement(charTitleLabel, yPos, scale(105));
        selectedCharacterLabel.setPosition(charTitleLabel.hb.cX + charTitleLabel.getAutoWidth() + scale(40), charTitleLabel.hb.cY - scale(10));
        loadoutRenderer.loadoutEditorButton.setPosition(charTitleLabel.hb.cX + charTitleLabel.getAutoWidth() + scale(400), charTitleLabel.hb.cY);
        loadoutRenderer.seriesButton.setPosition(loadoutRenderer.loadoutEditorButton.hb.cX + loadoutRenderer.loadoutEditorButton.hb.width, charTitleLabel.hb.cY);

        int column = 0;
        for (PCLCustomRunCharacterButton character : characters)
        {
            character.setPosition(SCREEN_X + column * scale(105), yPos);
            column += 1;
            if (column >= ROW_SIZE)
            {
                column = 0;
                yPos -= scale(105);
                upperScrollBound += scale(105);
            }
        }
        yPos -= scale(105);

        yPos = positionElement(modifiersLabel, yPos, scale(70));
        yPos = positionElement(endlessToggle, yPos, scale(35));
        yPos = positionElement(endingActToggle, yPos, scale(35));
        yPos = positionElement(customCardToggle, yPos, scale(35));
        yPos = positionElement(loadoutRenderer.simpleModeToggle, yPos, scale(180));
        seedInput.setPosition(endlessToggle.hb.cX + seedInput.hb.width + scale(50), endingActToggle.hb.cY + scale(30));
        ascensionEditor.setPosition(seedInput.hb.cX + seedInput.hb.width, endlessToggle.hb.cY);
        loadoutRenderer.ascensionGlyphsLabel.setPosition(ascensionEditor.hb.cX + ascensionEditor.hb.width + scale(230), ascensionEditor.hb.cY + scale(40));

        column = 0;
        for (PCLGlyphEditor geditor : loadoutRenderer.glyphEditors)
        {
            geditor.setPosition(loadoutRenderer.ascensionGlyphsLabel.hb.cX + column * scale(105), endingActToggle.hb.cY);
            column += 1;
        }


        yPos = positionElement(modifierDropdown, SCREEN_X - scale(135), yPos, scale(80));
        lowerScrollBound = upperScrollBound * -1;
    }
}
