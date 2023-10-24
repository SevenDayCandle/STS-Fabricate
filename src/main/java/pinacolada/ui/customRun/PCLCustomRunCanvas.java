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
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.ViewInGameCardPoolEffect;
import pinacolada.effects.screen.ViewInGameRelicPoolEffect;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.ui.PCLValueEditor;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;


public class PCLCustomRunCanvas extends EUICanvas {
    private static final float SCREEN_X = Settings.WIDTH * 0.25f;
    private static final float SCREEN_Y = Settings.HEIGHT * 0.9f;
    private static final float GAP_Y = scale(160);
    private static final int ROW_SIZE = 10;
    // Dammit basegame why hardcoded strings -_-
    private static final String MOD_ENDLESS = "Endless";
    private static final String MOD_THE_ENDING = "The Ending";

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
    public final EUIToggle customRelicToggle;
    public final EUIToggle customPotionToggle;
    public final EUIToggle customBlightToggle;
    public final EUIToggle allowLoadoutToggle;
    public final EUIButton editCardPoolButton;
    public final EUIButton editRelicPoolButton;
    public final EUIButton editLoadoutButton;
    public final ArrayList<PCLCustomRunCharacterButton> characters = new ArrayList<>();
    public final EUISearchableDropdown<CustomMod> modifierDropdown;
    public final MenuCancelButton cancelButton = new MenuCancelButton();
    public final GridSelectConfirmButton confirmButton;
    public final EUITooltip modsTooltip;
    private PCLEffectWithCallback<?> currentEffect;

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

        editCardPoolButton = EUIButton.createHexagonalButton(0, 0, scale(230), scale(55))
                .setLabel(EUIFontHelper.cardTitleFontSmall, 1f, EUIRM.strings.uipool_viewCardPool)
                .setOnClick(this::openCardPool);

        editRelicPoolButton = EUIButton.createHexagonalButton(0, 0, scale(230), scale(55))
                .setLabel(EUIFontHelper.cardTitleFontSmall, 1f, EUIRM.strings.uipool_viewRelicPool)
                .setOnClick(this::openRelicPool);

        editLoadoutButton = EUIButton.createHexagonalButton(0, 0, scale(230), scale(55))
                .setLabel(EUIFontHelper.cardTitleFontSmall, 1f, PGR.core.strings.csel_deckEditor)
                .setTooltip(PGR.core.strings.csel_deckEditor, PGR.core.strings.csel_deckEditorInfo)
                .setColor(Color.FOREST)
                .setOnClick(this::openLoadoutEditor);

        RunModStrings endlessStrings = PGR.getRunModStrings(MOD_ENDLESS);
        RunModStrings endingActStrings = PGR.getRunModStrings(MOD_THE_ENDING);

        endlessToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 32f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(endlessStrings.NAME)
                .setOnToggle(v -> {
                    screen.isEndless = v;
                })
                .setTooltip(endlessStrings.NAME, endlessStrings.DESCRIPTION);

        endingActToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 32f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(endingActStrings.NAME)
                .setOnToggle(v -> {
                    screen.isFinalActAvailable = v;
                })
                .setTooltip(endingActStrings.NAME, endingActStrings.DESCRIPTION);

        customCardToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 32f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(PGR.core.strings.misc_customCards)
                .setOnToggle(v -> {
                    screen.allowCustomCards = v;
                })
                .setToggle(screen.allowCustomCards)
                .setTooltip(PGR.core.strings.misc_customCards, PGR.core.strings.misc_customCardsDesc);

        customRelicToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 32f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(PGR.core.strings.misc_customRelics)
                .setOnToggle(v -> {
                    screen.allowCustomRelics = v;
                })
                .setToggle(screen.allowCustomRelics)
                .setTooltip(PGR.core.strings.misc_customRelics, PGR.core.strings.misc_customRelicsDesc);

        customPotionToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 32f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(PGR.core.strings.misc_customPotions)
                .setOnToggle(v -> {
                    screen.allowCustomPotions = v;
                })
                .setToggle(screen.allowCustomPotions)
                .setTooltip(PGR.core.strings.misc_customPotions, PGR.core.strings.misc_customPotionsDesc);

        customBlightToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 32f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(PGR.core.strings.misc_customBlights)
                .setOnToggle(v -> {
                    screen.allowCustomPotions = v;
                })
                .setToggle(screen.allowCustomBlights)
                .setTooltip(PGR.core.strings.misc_customBlights, PGR.core.strings.misc_customBlightsDesc);

        allowLoadoutToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 32f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.475f)
                .setText(PGR.core.strings.misc_customLoadout)
                .setOnToggle(v -> {
                    screen.allowLoadout = v;
                })
                .setTooltip(PGR.core.strings.misc_customLoadout, PGR.core.strings.misc_customLoadoutDesc);

        seedInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.greySquare.texture(),
                new EUIHitbox(scale(280), scale(48)))
                .setOnComplete(screen::setSeed)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[7])
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.9f);
        seedInput.header.setAlignment(1f, 0);

        ascensionEditor = new PCLValueEditor(new EUIHitbox(scale(64), scale(48)), CustomModeScreen.TEXT[3], this::setAscension)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[3])
                .setLimits(0, 20)
                .setTooltip(CustomModeScreen.TEXT[3], "");
        ascensionEditor.header.setAlignment(0.4f, 0.25f);

        modifierDropdown = (EUISearchableDropdown<CustomMod>) new EUISearchableDropdown<CustomMod>(new EUIHitbox(scale(128), scale(48)), mod -> mod.name)
                .setRowFunction(PCLCustomModDropdownRow::new)
                .setOnChange(this::onUpdateModifiers)
                .setIsMultiSelect(true)
                .setHeader(EUIFontHelper.cardTitleFontSmall, 1f, Settings.GOLD_COLOR, CustomModeScreen.TEXT[6])
                .setCanAutosize(true, true);


        modsTooltip = new EUITooltip(CustomModeScreen.TEXT[6]);
        modifierDropdown.setTooltip(modsTooltip);

        this.confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);
        this.confirmButton.isDisabled = false;
    }

    public void close() {
        InputHelper.pressedEscape = false;
        this.cancelButton.hb.clicked = false;
        this.cancelButton.hide();
        CardCrawlGame.mainMenuScreen.panelScreen.refresh();
    }

    // Group mods by original color, then sort them alphabetically
    protected int compareMod(CustomMod a, CustomMod b) {
        int colorComp = StringUtils.compare(a.color, b.color);
        if (colorComp != 0) {
            return colorComp;
        }
        return StringUtils.compare(a.name, b.name);
    }

    protected void onScroll(float newPercent) {
        super.onScroll(newPercent);
        updatePositions();
    }

    protected void onUpdateModifiers(List<CustomMod> mods) {
        this.screen.activeMods = mods;
        modsTooltip.setDescription(EUIUtils.joinStringsMap(EUIUtils.SPLIT_LINE, m -> PCLCoreStrings.colorString(m.color, m.name), mods));
    }

    public void open() {
        confirmButton.show();
        cancelButton.show(CharacterSelectScreen.TEXT[5]);
    }

    public void openCardPool() {
        currentEffect = new ViewInGameCardPoolEffect(screen.getAllPossibleCards(), screen.bannedCards)
                .addCallback(pool -> {
                    screen.bannedCards = pool.bannedCards;
                });
    }

    public void openLoadoutEditor() {
        allowLoadoutToggle.toggle(true);
        PGR.loadoutEditor.open(screen.fakeLoadout, null, screen.currentOption, () -> {
        });
    }

    public void openRelicPool() {
        currentEffect = new ViewInGameRelicPoolEffect(screen.getAllPossibleRelics(), screen.bannedRelics)
                .addCallback(pool -> {
                    screen.bannedRelics = pool.bannedRelics;
                });;
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

    public void renderImpl(SpriteBatch sb) {
        if (currentEffect != null) {
            currentEffect.render(sb);
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
            customRelicToggle.tryRender(sb);
            customPotionToggle.tryRender(sb);
            customBlightToggle.tryRender(sb);
            allowLoadoutToggle.tryRender(sb);
            seedInput.tryRender(sb);
            ascensionEditor.tryRender(sb);
            modifierDropdown.tryRender(sb);
            for (PCLCustomRunCharacterButton b : characters) {
                b.tryRenderCentered(sb);
            }
            editCardPoolButton.renderImpl(sb);
            editRelicPoolButton.renderImpl(sb);
            editLoadoutButton.renderImpl(sb);
            confirmButton.render(sb);
            cancelButton.render(sb);
        }
    }

    public void resetPositions() {
        scrollBar.scroll(0, true);
    }

    public void setAscension(int i) {
        screen.ascensionLevel = i;
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
        modList = EUIUtils.filter(modList, mod -> !MOD_ENDLESS.equals(mod.ID) && !MOD_THE_ENDING.equals(mod.ID));
        modList.sort(this::compareMod);
        modifierDropdown.setItems(modList);
    }

    public void updateImpl() {
        if (currentEffect != null) {
            currentEffect.update();
            if (currentEffect.isDone) {
                currentEffect = null;
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
            customRelicToggle.tryUpdate();
            customPotionToggle.tryUpdate();
            customBlightToggle.tryUpdate();
            allowLoadoutToggle.tryUpdate();
            seedInput.tryUpdate();
            ascensionEditor.tryUpdate();
            modifierDropdown.tryUpdate();
            editCardPoolButton.updateImpl();
            editRelicPoolButton.updateImpl();
            editLoadoutButton.updateImpl();
            confirmButton.update();
            cancelButton.update();
            for (PCLCustomRunCharacterButton b : characters) {
                b.tryUpdate();
            }

            if (!EUI.doesActiveElementExist()) {
                if (this.cancelButton.hb.clicked || InputHelper.pressedEscape) {
                    close();
                }
                else if (this.confirmButton.hb.clicked || CInputActionSet.proceed.isJustPressed()) {
                    this.confirmButton.hb.clicked = false;
                    this.screen.confirm();
                }
            }
        }
    }

    protected void updatePositions() {
        upperScrollBound = Settings.DEFAULT_SCROLL_LIMIT + scale(550);
        float yPos = SCREEN_Y;
        yPos = positionElement(titleLabel, SCREEN_X - scale(80), yPos, scale(10));
        yPos = positionElement(trophiesLabel, titleLabel.hb.cX + titleLabel.getAutoWidth() + scale(160), yPos, scale(80));
        yPos = positionElement(charTitleLabel, yPos, scale(105));
        selectedCharacterLabel.setPosition(charTitleLabel.hb.cX + charTitleLabel.getAutoWidth() + scale(40), charTitleLabel.hb.cY - scale(20));
        editLoadoutButton.setPosition(Settings.WIDTH * 0.72f, selectedCharacterLabel.hb.cY + scale(20));

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
        yPos = positionElement(customCardToggle, yPos, scale(35));
        yPos = positionElement(customRelicToggle, yPos, scale(35));
        yPos = positionElement(customPotionToggle, yPos, scale(35));
        yPos = positionElement(customBlightToggle, yPos, scale(35));
        yPos = positionElement(allowLoadoutToggle, yPos, scale(125));
        modifierDropdown.setPosition(endlessToggle.hb.cX + modifierDropdown.hb.width, endingActToggle.hb.y);
        ascensionEditor.setPosition(modifierDropdown.hb.cX + modifierDropdown.hb.width, endlessToggle.hb.y - scale(5));
        seedInput.setPosition(ascensionEditor.hb.cX + seedInput.hb.width, modifierDropdown.hb.y + scale(20));

        yPos = positionElement(editCardPoolButton, yPos, scale(66));
        yPos = positionElement(editRelicPoolButton, yPos, scale(66));

        lowerScrollBound = upperScrollBound * -1;
    }
}
