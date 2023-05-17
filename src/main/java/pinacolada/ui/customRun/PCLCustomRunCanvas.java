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
import extendedui.ui.AbstractScreen;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.screen.ViewInGamePoolEffect;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLValueEditor;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;


public class PCLCustomRunCanvas extends EUICanvas {
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
    public final EUIButton editCardPoolButton;
    public final ArrayList<PCLCustomRunCharacterButton> characters = new ArrayList<>();
    public final EUISearchableDropdown<CustomMod> modifierDropdown;
    public final MenuCancelButton cancelButton = new MenuCancelButton();
    public final GridSelectConfirmButton confirmButton;
    private ViewInGamePoolEffect cardEffect;

    public PCLCustomRunCanvas(PCLCustomRunScreen screen) {
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
        selectedCharacterLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall, new EUIHitbox(screenW(0.18f), screenH(0.05f)))
                .setColor(Settings.BLUE_TEXT_COLOR);

        RunModStrings endlessStrings = PGR.getRunModStrings(MOD_ENDLESS);
        RunModStrings endingActStrings = PGR.getRunModStrings(MOD_THE_ENDING);

        endlessToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(endlessStrings.NAME)
                .setOnToggle(v -> {
                    screen.isEndless = v;
                })
                .setTooltip(endlessStrings.NAME, endlessStrings.DESCRIPTION);

        endingActToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(endingActStrings.NAME)
                .setOnToggle(v -> {
                    screen.isFinalActAvailable = v;
                })
                .setTooltip(endingActStrings.NAME, endingActStrings.DESCRIPTION);

        customCardToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(PGR.core.strings.misc_customCards)
                .setOnToggle(v -> {
                    screen.allowCustomCards = v;
                })
                .setTooltip(PGR.core.strings.misc_customCards, PGR.core.strings.misc_customCardsDesc);

        seedInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.panel.texture(),
                new EUIHitbox(scale(280), scale(48)))
                .setOnComplete(screen::setSeed)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[7])
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.9f);
        seedInput.header.setAlignment(1f, 0);

        ascensionEditor = new PCLValueEditor(new EUIHitbox(scale(64), scale(48)), CustomModeScreen.TEXT[3], screen::setAscension)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[3])
                .setLimits(0, 20)
                .setTooltip(CustomModeScreen.TEXT[3], "");
        ascensionEditor.header.setAlignment(0.4f, 0f);

        modifierDropdown = (EUISearchableDropdown<CustomMod>) new EUISearchableDropdown<CustomMod>(new EUIHitbox(scale(128), scale(48)), mod -> mod.name)
                .setRowFunction(PCLCustomModDropdownRow::new)
                .setOnChange(items -> this.screen.activeMods = items)
                .setIsMultiSelect(true)
                .setHeader(FontHelper.panelNameFont, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[6])
                .setCanAutosize(true, true);

        editCardPoolButton = AbstractScreen.createHexagonalButton(0, 0, scale(230), scale(70))
                .setText(PGR.core.strings.csel_seriesEditor)
                .setOnClick(this::openCardPool);

        this.confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);
        this.confirmButton.isDisabled = false;
        this.scrollBar.setOnScroll(this::onScroll);

    }

    protected void onScroll(float newPercent) {
        super.onScroll(newPercent);
        updatePositions();
    }

    public void updateImpl() {
        if (cardEffect != null) {
            cardEffect.update();
            if (cardEffect.isDone) {
                cardEffect = null;
            }
        }
        else {
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
            editCardPoolButton.updateImpl();
            confirmButton.update();
            cancelButton.update();
            for (PCLCustomRunCharacterButton b : characters) {
                b.tryUpdate();
            }

            if (this.cancelButton.hb.clicked || InputHelper.pressedEscape) {
                InputHelper.pressedEscape = false;
                this.cancelButton.hb.clicked = false;
                this.cancelButton.hide();
                CardCrawlGame.mainMenuScreen.panelScreen.refresh();
            }
            else if (this.confirmButton.hb.clicked || CInputActionSet.proceed.isJustPressed()) {
                this.confirmButton.hb.clicked = false;
                this.screen.confirm();
            }
        }
    }

    public void renderImpl(SpriteBatch sb) {
        if (cardEffect != null) {
            cardEffect.render(sb);
        }
        else {
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
            for (PCLCustomRunCharacterButton b : characters) {
                b.tryRenderCentered(sb);
            }
            editCardPoolButton.renderImpl(sb);
            confirmButton.render(sb);
            cancelButton.render(sb);
        }
    }

    public void open() {
        confirmButton.show();
        cancelButton.show(CharacterSelectScreen.TEXT[5]);
    }

    public void openCardPool() {
        cardEffect = new ViewInGamePoolEffect(screen.getAllPossibleCards(), screen.bannedCards);
    }

    protected float positionElement(EUIHoverable element, float xPos, float yPos, float diff) {
        element.setPosition(xPos, yPos);
        return yPos - diff;
    }

    protected float positionElement(EUIHoverable element, float yPos, float diff) {
        return positionElement(element, SCREEN_X, yPos, diff);
    }

    protected float positionElement(EUIHoverable element, float yPos) {
        return positionElement(element, yPos, GAP_Y);
    }

    public void resetPositions() {
        scrollBar.scroll(0, true);
    }

    public void setAscension(int i) {
        ascensionEditor.setValue(i, false);
        int value = ascensionEditor.getValue();
        ascensionEditor.tooltip.setTitle(EUIRM.strings.generic2(CustomModeScreen.TEXT[3], value));
        ascensionEditor.tooltip.setDescription(value > 0 ? CharacterSelectScreen.A_TEXT[value - 1] : "");
    }

    public void setCharacter(CharacterOption c) {
        selectedCharacterLabel.setLabel(c.c.getLocalizedCharacterName());
        for (PCLCustomRunCharacterButton button : characters) {
            button.glowing = (button.character == c);
        }
        ascensionEditor.setLimits(0, GameUtilities.getMaxAscensionLevel(c.c)).setValue(screen.ascensionLevel);
    }

    public void setup(CustomModeScreen original) {
        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
            characters.add(new PCLCustomRunCharacterButton(screen, p));
        }

        // Snag the modifiers from the original custom run screen, excluding endless/ending act because we handle this manually
        ArrayList<CustomMod> modList = EUIClassUtils.getField(original, "modList");
        modifierDropdown.setItems(EUIUtils.filter(modList, mod -> !MOD_ENDLESS.equals(mod.ID) && !MOD_THE_ENDING.equals(mod.ID)));
    }

    protected void updatePositions() {
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT + scale(550);
        float yPos = SCREEN_Y;
        yPos = positionElement(titleLabel, SCREEN_X - scale(80), yPos, scale(10));
        yPos = positionElement(trophiesLabel, titleLabel.hb.cX + titleLabel.getAutoWidth() + scale(160), yPos, scale(80));
        yPos = positionElement(charTitleLabel, yPos, scale(105));
        selectedCharacterLabel.setPosition(charTitleLabel.hb.cX + charTitleLabel.getAutoWidth() + scale(40), charTitleLabel.hb.cY - scale(10));
        editCardPoolButton.setPosition(charTitleLabel.hb.cX + charTitleLabel.getAutoWidth() + scale(400), charTitleLabel.hb.cY);

        int column = 0;
        for (PCLCustomRunCharacterButton character : characters) {
            character.setPosition(SCREEN_X + column * scale(105), yPos);
            column += 1;
            if (column >= ROW_SIZE) {
                column = 0;
                yPos -= scale(105);
                upperScrollBound += scale(105);
            }
        }
        yPos -= scale(105);

        yPos = positionElement(modifiersLabel, yPos, scale(70));
        yPos = positionElement(endlessToggle, yPos, scale(35));
        yPos = positionElement(endingActToggle, yPos, scale(35));
        yPos = positionElement(customCardToggle, yPos, scale(125));
        seedInput.setPosition(endlessToggle.hb.cX + seedInput.hb.width + scale(50), endingActToggle.hb.cY + scale(30));
        ascensionEditor.setPosition(seedInput.hb.cX + seedInput.hb.width, endlessToggle.hb.cY);

        yPos = positionElement(modifierDropdown, SCREEN_X - scale(135), yPos, scale(80));
        lowerScrollBound = upperScrollBound * -1;
    }
}
