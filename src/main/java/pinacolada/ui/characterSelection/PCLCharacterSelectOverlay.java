package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.text.EUITextHelper;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLPlayerMeter;
import pinacolada.dungeon.modifiers.AbstractGlyph;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.screen.PCLYesNoConfirmationEffect;
import pinacolada.interfaces.providers.RunAttributesProvider;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.loadout.PCLLoadoutValidation;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCharacterSelectOverlay extends EUIBase implements RunAttributesProvider {
    protected static final float POS_X = 1100f * Settings.scale;
    protected static final float POS_Y = Settings.HEIGHT * 0.47f;
    protected static final float ROW_OFFSET = 60 * Settings.scale;

    protected static final Random RNG = new Random();
    protected final ArrayList<PCLLoadout> availableLoadouts;
    protected final ArrayList<PCLLoadout> loadouts;
    public final ArrayList<PCLGlyphEditor> glyphEditors;
    protected ArrayList<AbstractBlight> cachedBlights;
    protected ArrayList<AbstractRelic> cachedRelics;
    protected CharacterSelectScreen charScreen;
    protected CharacterOption characterOption;
    protected AbstractPlayerData<?, ?> data;
    protected PCLLoadout loadout;
    protected PCLEffect currentDialog;
    protected float textScale;
    public PCLEffect playEffect;
    public EUIButton seriesButton;
    public EUIButton loadoutEditorButton;
    public EUIButton infoButton;
    public EUIButton resetButton;
    public EUILabel startingCardsLabel;
    public EUILabel ascensionGlyphsLabel;
    public EUITextBox startingCardsListLabel;


    public PCLCharacterSelectOverlay() {
        final float leftTextWidth = FontHelper.getSmartWidth(FontHelper.cardTitleFont, PGR.core.strings.csel_leftText, 9999f, 0f) + scale(10);

        textScale = Settings.scale;

        //Need to prevent text from disappearing from scaling too big on 4K resolutions
        if (textScale > 1) {
            textScale = 1;
        }

        loadouts = new ArrayList<>();
        availableLoadouts = new ArrayList<>();
        glyphEditors = new ArrayList<>();

        startingCardsLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(POS_X, POS_Y, leftTextWidth, 50f * Settings.scale))
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(PGR.core.strings.csel_leftText);

        startingCardsListLabel = new EUITextBox(EUIRM.images.panelRounded.texture(),
                new EUIHitbox(POS_X + ROW_OFFSET * 4f, POS_Y, leftTextWidth, 50f * Settings.scale))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GREEN_TEXT_COLOR)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.8f)
                .setAlignment(0.5f, 0.5f, false);

        ascensionGlyphsLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(POS_X, POS_Y - startingCardsLabel.hb.height * 1.5f, leftTextWidth, 50f * Settings.scale))
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(PGR.core.strings.csel_ascensionGlyph);

        final float buttonWidth = Settings.WIDTH * (0.11f);
        final float gapY = scale(76);
        final float buttonheight = scale(46);
        final float textScale = 0.8f;

        resetButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setPosition(startingCardsListLabel.hb.cX, startingCardsListLabel.hb.y + scale(110))
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_resetTutorial)
                .setTooltip(PGR.core.strings.csel_resetTutorial, PGR.core.strings.csel_resetTutorialInfo)
                .setColor(new Color(0.8f, 0.3f, 0.5f, 1))
                .setOnClick(this::openResetDialog);

        infoButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setPosition(startingCardsListLabel.hb.cX, resetButton.hb.y + gapY)
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_charTutorial)
                .setTooltip(PGR.core.strings.csel_charTutorial, PGR.core.strings.csel_charTutorialInfo)
                .setColor(new Color(0.5f, 0.3f, 0.8f, 1))
                .setOnClick(this::openInfo);

        loadoutEditorButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setPosition(startingCardsListLabel.hb.cX, infoButton.hb.y + gapY)
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_deckEditor)
                .setTooltip(PGR.core.strings.csel_deckEditor, PGR.core.strings.csel_deckEditorInfo)
                .setColor(new Color(0.3f, 0.5f, 0.8f, 1))
                .setOnRightClick(this::changePreset)
                .setOnClick(this::openLoadoutEditor);

        seriesButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setPosition(startingCardsListLabel.hb.cX, loadoutEditorButton.hb.y + gapY)
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_seriesEditor)
                .setTooltip(PGR.core.strings.csel_seriesEditor, PGR.core.strings.csel_seriesEditorInfo)
                .setColor(new Color(0.3f, 0.8f, 0.5f, 1))
                .setOnClick(this::openSeriesSelect);

        float xOffset = ascensionGlyphsLabel.hb.x + ROW_OFFSET * 4f;
        for (AbstractGlyph glyph : AbstractPlayerData.GLYPHS) {
            glyphEditors.add(new PCLGlyphEditor(glyph, new EUIHitbox(xOffset, ascensionGlyphsLabel.hb.y, ROW_OFFSET, ROW_OFFSET)));
            xOffset += ROW_OFFSET * 1.7f;
        }
    }

    @Override
    public int ascensionLevel() {
        return charScreen != null ? charScreen.ascensionLevel : 0;
    }

    private void changeLoadout(int index) {
        int actualIndex = index % loadouts.size();
        if (actualIndex < 0) {
            actualIndex = loadouts.size() - 1;
        }
        data.selectedLoadout = loadouts.get(actualIndex);
        refresh(characterOption);
    }

    private void changeLoadout(PCLLoadout loadout) {
        data.selectedLoadout = loadout;
        refresh(characterOption);
    }

    private void changePreset() {
        final int preset = loadout.canChangePreset(loadout.preset + 1) ? (loadout.preset + 1) : 0;
        if (preset != loadout.preset) {
            loadout.preset = preset;
            refreshInternal();
        }
    }

    @Override
    public void disableConfirm(boolean value) {
        if (charScreen != null) {
            charScreen.confirmButton.isDisabled = value;
        }
    }

    protected float getInfoX() {
        return EUIClassUtils.getField(characterOption, "infoX");
    }

    protected float getInfoY() {
        return EUIClassUtils.getField(characterOption, "infoY");
    }

    public void initialize(CharacterSelectScreen selectScreen) {
        charScreen = selectScreen;
        characterOption = null;
    }

    private void openInfo() {
        if (characterOption != null && data != null) {
            PCLPlayerMeter meter = CombatManager.playerSystem.getMeter(data.resources.playerClass);
            if (meter != null) {
                EUI.tutorialScreen.open(new EUITutorial(meter.getInfoPages()), () -> refresh(characterOption));
            }
        }
    }

    private void openLoadoutEditor() {
        if (loadout != null && characterOption != null && data != null) {
            PGR.loadoutEditor.open(loadout, data, characterOption, () -> refresh(characterOption));
        }
    }

    private void openResetDialog() {
        currentDialog = new PCLYesNoConfirmationEffect(PGR.core.strings.csel_resetTutorial, PGR.core.strings.csel_resetTutorialConfirm)
                .addCallback(() -> {
                    data.config.resetTutorial();
                });
    }

    private void openSeriesSelect() {
        if (characterOption != null && data != null) {
            PGR.seriesSelection.open(characterOption, data, () -> refresh(characterOption));
        }
    }

    public void randomizeLoadout() {
        if (availableLoadouts.size() > 1) {
            while (loadout == data.selectedLoadout) {
                data.selectedLoadout = GameUtilities.getRandomElement(availableLoadouts, RNG);
            }

            refresh(characterOption);
        }
    }

    public void refresh(CharacterOption characterOption) {
        refresh(characterOption, GameUtilities.isPCLPlayerClass(characterOption.c.chosenClass));
    }

    public void refresh(CharacterOption characterOption, boolean canOpen) {
        this.characterOption = characterOption;

        if (characterOption != null && canOpen) {
            EUI.actingColor = characterOption.c.getCardColor();
            refreshPlayerData();
            refreshInternal();
        }

        if (canOpen && data != null) {
            playEffect = data.getCharSelectScreenAnimation();
            startingCardsLabel.setActive(true);
            startingCardsListLabel.setActive(true);
            seriesButton.setActive(true);
            loadoutEditorButton.setActive(true);
            infoButton.setActive(true);
            resetButton.setActive(true);

            // Only show the glyphs if any glyphs are unlocked
            for (PCLGlyphEditor geditor : glyphEditors) {
                geditor.refresh(ascensionLevel());
            }

            boolean showGlyphs = EUIUtils.any(glyphEditors, geditor -> geditor.enabled);
            ascensionGlyphsLabel.setActive(showGlyphs);
            for (PCLGlyphEditor geditor : glyphEditors) {
                geditor.setActive(showGlyphs);
            }

            EUITourTooltip.queueFirstView(PGR.config.tourCharSelect,
                    seriesButton.makeTour(true),
                    loadoutEditorButton.makeTour(true),
                    infoButton.makeTour(true),
                    resetButton.makeTour(true));
        }
        else {
            playEffect = null;
            seriesButton.setActive(false);
            loadoutEditorButton.setActive(false);
            infoButton.setActive(false);
            resetButton.setActive(false);
            startingCardsLabel.setActive(false);
            startingCardsListLabel.setActive(false);
            ascensionGlyphsLabel.setActive(false);
            for (PCLGlyphEditor geditor : glyphEditors) {
                geditor.setActive(false);
            }
        }
    }

    public void refreshInternal() {
        EUIClassUtils.setField(characterOption, "gold", loadout.getGold());
        EUIClassUtils.setField(characterOption, "hp", loadout.getHP() + "/" + loadout.getHP());
        ArrayList<String> startingRelics = loadout.getStartingRelics();
        ((CharSelectInfo) EUIClassUtils.getField(characterOption, "charInfo")).relics = startingRelics;
        cachedRelics = EUIUtils.mapAsNonnull(startingRelics, RelicLibrary::getRelic);
        cachedBlights = EUIUtils.mapAsNonnull(loadout.getStartingBlights(), BlightHelper::getBlight);

        // Instead of continually refreshing relics at every render, change them only when the character or loadout changes
        for (AbstractBlight b : cachedBlights) {
            b.isSeen = true;
            b.updateDescription(characterOption.c.chosenClass);
        }
        for (AbstractRelic r : cachedRelics) {
            r.updateDescription(characterOption.c.chosenClass);
        }

        int currentLevel = data.resources.getUnlockLevel();
        startingCardsListLabel.setLabel(loadout.getDeckPreviewString(true));
        PCLLoadoutValidation validation = loadout.createValidation();
        if (currentLevel < loadout.unlockLevel) {
            EUITooltip invalidTooltip = new EUITooltip(PGR.core.strings.loadout_invalidLoadout, PGR.core.strings.loadout_invalidLoadoutDescLocked);
            startingCardsListLabel.setFontColor(Settings.RED_TEXT_COLOR).setTooltip(invalidTooltip);
            loadoutEditorButton.setInteractable(false);
            disableConfirm(true);
        }
        else if (!validation.isValid) {
            EUITooltip invalidTooltip = new EUITooltip(PGR.core.strings.loadout_invalidLoadout, validation.getFailingString());
            startingCardsListLabel.setFontColor(Settings.RED_TEXT_COLOR).setTooltip(invalidTooltip);
            loadoutEditorButton.setInteractable(true);
            disableConfirm(true);
        }
        else {
            startingCardsListLabel.setFontColor(Settings.GREEN_TEXT_COLOR).setTooltip(null);
            loadoutEditorButton.setInteractable(true);
            disableConfirm(false);
        }
    }

    protected void refreshPlayerData() {
        this.data = PGR.getPlayerData(characterOption.c.chosenClass);
        this.loadouts.clear();
        this.availableLoadouts.clear();

        if (data != null) {
            final int unlockLevel = data.resources.getUnlockLevel();
            for (PCLLoadout loadout : data.loadouts.values()) {
                this.loadouts.add(loadout);
                if (unlockLevel >= loadout.unlockLevel) {
                    this.availableLoadouts.add(loadout);
                }
            }

            this.loadouts.sort((a, b) ->
            {
                if (a.isCore()) {
                    return 1;
                }
                else if (b.isCore()) {
                    return -1;
                }
                final int diff = StringUtils.compare(a.ID, b.ID);
                final int level = data.resources.getUnlockLevel();
                final int levelA = a.unlockLevel - level;
                final int levelB = b.unlockLevel - level;

                if (levelA > 0 || levelB > 0) {
                    return diff + Integer.compare(levelA, levelB) * 1313;
                }

                return diff;
            });

            this.loadout = data.selectedLoadout;
            if (this.loadout == null || this.loadout.getStartingDeck().isEmpty() || !loadouts.contains(this.loadout)) {
                this.loadout = data.selectedLoadout = loadouts.get(0);
            }
        }
        else {
            this.loadout = null;
        }
    }

    public void renderImpl(SpriteBatch sb) {
        seriesButton.tryRender(sb);
        loadoutEditorButton.tryRender(sb);
        infoButton.tryRender(sb);
        resetButton.tryRender(sb);
        startingCardsLabel.tryRender(sb);
        startingCardsListLabel.tryRender(sb);
        ascensionGlyphsLabel.tryRender(sb);
        for (PCLGlyphEditor geditor : glyphEditors) {
            geditor.tryRender(sb);
        }
        if (currentDialog != null) {
            currentDialog.render(sb);
        }
    }

    public void renderPCLInfo(SpriteBatch sb) {
        if (currentDialog != null) {
            return;
        }

        float infoX = getInfoX();
        float infoY = getInfoY();
        int unlocksRemaining = EUIClassUtils.getField(characterOption, "unlocksRemaining");
        int gold = EUIClassUtils.getField(characterOption, "gold");
        String hp = EUIClassUtils.getField(characterOption, "hp");
        String flavor = EUIClassUtils.getField(characterOption, "flavorText");

        EUITextHelper.renderSmart(sb, FontHelper.bannerNameFont, characterOption.name, infoX - 35.0F * Settings.scale, infoY + 350.0F * Settings.scale, 99999.0F, 38.0F * Settings.scale, Settings.GOLD_COLOR);
        EUIRenderHelpers.drawCentered(sb, Color.WHITE, PCLCoreImages.CardIcons.hp.texture(), infoX - 10.0F * Settings.scale, infoY + 230.0F * Settings.scale, Settings.scale * 48, Settings.scale * 48, 0.7f, 0);
        EUITextHelper.renderSmart(sb, EUIFontHelper.cardTitleFontSmall, CharacterOption.TEXT[4] + hp, infoX + 18.0F * Settings.scale, infoY + 243.0F * Settings.scale, 10000.0F, 10000.0F, Settings.RED_TEXT_COLOR);
        EUIRenderHelpers.drawCentered(sb, Color.WHITE, PCLCoreImages.Tooltips.gold.texture(), infoX + 260.0F * Settings.scale, infoY + 230.0F * Settings.scale, Settings.scale * 48, Settings.scale * 48, 0.7f, 0);
        EUITextHelper.renderSmart(sb, EUIFontHelper.cardTitleFontSmall, CharacterOption.TEXT[5] + gold, infoX + 290.0F * Settings.scale, infoY + 243.0F * Settings.scale, 10000.0F, 10000.0F, Settings.GOLD_COLOR);

        if (cachedBlights != null) {
            EUITextHelper.renderSmart(sb, EUIFontHelper.cardTitleFontSmall, PGR.core.strings.csel_ability, infoX - 20.0F * Settings.scale, infoY + 80.0F * Settings.scale, 99999.0F, 38.0F * Settings.scale, Settings.GOLD_COLOR);
            for (AbstractBlight r : cachedBlights) {
                r.render(sb, false, Color.WHITE);
            }
        }
        if (cachedRelics != null) {
            EUITextHelper.renderSmart(sb, EUIFontHelper.cardTitleFontSmall, PGR.core.strings.loadout_relicHeader, infoX - 20.0F * Settings.scale, infoY + 10.0F * Settings.scale, 99999.0F, 38.0F * Settings.scale, Settings.GOLD_COLOR);
            for (AbstractRelic r : cachedRelics) {
                r.renderWithoutAmount(sb, Color.WHITE);
            }
        }

        EUIFontHelper.cardTitleFontSmall.getData().setScale(0.8f);
        EUITextHelper.renderSmart(sb, EUIFontHelper.cardTitleFontSmall, flavor, infoX - 26.0F * Settings.scale, infoY + 160.0F * Settings.scale, 10000.0F, 30.0F * Settings.scale, Settings.CREAM_COLOR);
        if (unlocksRemaining > 0) {
            EUITextHelper.renderSmart(sb, EUIFontHelper.cardTitleFontSmall, unlocksRemaining + CharacterOption.TEXT[6], infoX - 26.0F * Settings.scale, infoY - 80.0F * Settings.scale, 10000.0F, 10000.0F, Settings.CREAM_COLOR);
            int unlockProgress = UnlockTracker.getCurrentProgress(characterOption.c.chosenClass);
            int unlockCost = UnlockTracker.getCurrentScoreCost(characterOption.c.chosenClass);
            EUITextHelper.renderSmart(sb, EUIFontHelper.cardTitleFontSmall, unlockProgress + "/" + unlockCost + CharacterOption.TEXT[9], infoX - 26.0F * Settings.scale, infoY - 108.0F * Settings.scale, 10000.0F, 10000.0F, Settings.CREAM_COLOR);
        }
        EUIRenderHelpers.resetFont(EUIFontHelper.cardTitleFontSmall);
    }

    // When rendering PCL players, we should use our own relic method because the default method won't render PCL relics properly
    public boolean shouldRenderPCLInfo() {
        return characterOption != null && startingCardsLabel.isActive;
    }

    public void updateForAscension() {
        for (PCLGlyphEditor glyphEditor : glyphEditors) {
            glyphEditor.refresh(ascensionLevel());
        }
    }

    public void updateImpl() {
        if (playEffect != null) {
            playEffect.update();
        }

        if (currentDialog != null) {
            currentDialog.update();
            if (currentDialog.isDone) {
                currentDialog = null;
            }
        }
        else {
            seriesButton.tryUpdate();
            loadoutEditorButton.tryUpdate();
            infoButton.tryUpdate();
            resetButton.tryUpdate();
            startingCardsLabel.tryUpdate();
            startingCardsListLabel.tryUpdate();
            ascensionGlyphsLabel.tryUpdate();
            for (PCLGlyphEditor geditor : glyphEditors) {
                geditor.tryUpdate();
            }
            if (characterOption != null) {
                float x = getInfoX() + Settings.scale * 155;
                float y = getInfoY() + Settings.scale * 75;
                if (cachedBlights != null) {
                    for (int i = 0; i < cachedBlights.size(); i++) {
                        AbstractBlight r = cachedBlights.get(i);
                        r.currentX = x + i * 73.0F * Settings.scale;
                        r.currentY = y;
                        r.hb.move(r.currentX, r.currentY);
                        r.hb.update();
                    }
                }
                if (cachedRelics != null) {
                    for (int i = 0; i < cachedRelics.size(); i++) {
                        AbstractRelic r = cachedRelics.get(i);
                        r.currentX = x + i * 73.0F * Settings.scale;
                        r.currentY = y - 75.0F * Settings.scale;
                        r.hb.move(r.currentX, r.currentY);
                        r.hb.update();
                    }
                }
            }
        }
    }

    public void updateSelectedCharacter(CharacterSelectScreen selectScreen) {
        charScreen = selectScreen;
        final CharacterOption current = characterOption;
        characterOption = null;

        for (CharacterOption o : selectScreen.options) {
            if (o.selected) {
                characterOption = o;

                if (current != o) {
                    refresh(o);
                }

                return;
            }
        }
    }
}