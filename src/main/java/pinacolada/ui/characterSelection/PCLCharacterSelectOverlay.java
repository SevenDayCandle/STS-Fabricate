package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
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
import pinacolada.blights.common.AbstractGlyphBlight;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLPlayerMeter;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.screen.PCLYesNoConfirmationEffect;
import pinacolada.effects.screen.ViewInGameRelicPoolEffect;
import pinacolada.interfaces.providers.RunAttributesProvider;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.loadout.PCLLoadoutValidation;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public class PCLCharacterSelectOverlay extends EUIBase implements RunAttributesProvider {
    protected static final float POS_X = 1100f * Settings.scale;
    protected static final float POS_Y = Settings.HEIGHT * 0.47f;
    protected static final float ROW_OFFSET = 60 * Settings.scale;

    protected static final Random RNG = new Random();
    protected final ArrayList<PCLLoadout> availableLoadouts;
    protected final ArrayList<PCLLoadout> loadouts;
    public final ArrayList<PCLGlyphEditor> glyphEditors;
    protected ArrayList<AbstractRelic> cachedRelics;
    protected CharacterSelectScreen charScreen;
    protected CharacterOption characterOption;
    protected AbstractPlayerData<?, ?> data;
    protected PCLLoadout loadout;
    protected PCLEffect currentDialog;
    protected float textScale;
    public PCLEffect playEffect;
    public EUIButton seriesButton;
    public EUIButton relicsButton;
    public EUIButton loadoutEditorButton;
    public EUIButton infoButton;
    public EUIButton resetButton;
    public EUILabel startingCardsLabel;
    public EUILabel ascensionGlyphsLabel;
    public EUITextBox startingCardsListLabel;


    public PCLCharacterSelectOverlay() {
        final float leftTextWidth = FontHelper.getSmartWidth(FontHelper.cardTitleFont, PGR.core.strings.csel_leftText, 9999f, 0f);

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
                .setPosition(startingCardsListLabel.hb.cX, startingCardsListLabel.hb.y + scale(110)).setText("")
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_resetTutorial)
                .setTooltip(PGR.core.strings.csel_resetTutorial, PGR.core.strings.csel_resetTutorialInfo)
                .setColor(new Color(0.8f, 0.3f, 0.5f, 1))
                .setOnClick(this::openResetDialog);

        infoButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setPosition(startingCardsListLabel.hb.cX, resetButton.hb.y + gapY).setText("")
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_charTutorial)
                .setTooltip(PGR.core.strings.csel_charTutorial, PGR.core.strings.csel_charTutorialInfo)
                .setColor(new Color(0.5f, 0.3f, 0.8f, 1))
                .setOnClick(this::openInfo);

        loadoutEditorButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setPosition(startingCardsListLabel.hb.cX, infoButton.hb.y + gapY).setText("")
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_deckEditor)
                .setTooltip(PGR.core.strings.csel_deckEditor, PGR.core.strings.csel_deckEditorInfo)
                .setColor(new Color(0.3f, 0.5f, 0.8f, 1))
                .setOnRightClick(this::changePreset)
                .setOnClick(this::openLoadoutEditor);

        relicsButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setPosition(startingCardsListLabel.hb.cX, loadoutEditorButton.hb.y + gapY).setText("")
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_relicPool)
                .setTooltip(PGR.core.strings.csel_relicPool, PGR.core.strings.csel_relicPoolInfo)
                .setColor(new Color(0.3f, 0.8f, 0.5f, 1))
                .setOnClick(this::openRelicsDialog);

        seriesButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setPosition(startingCardsListLabel.hb.cX, relicsButton.hb.y + gapY).setText("")
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_seriesEditor)
                .setTooltip(PGR.core.strings.csel_seriesEditor, PGR.core.strings.csel_seriesEditorInfo)
                .setColor(new Color(0.5f, 0.8f, 0.3f, 1))
                .setOnClick(this::openSeriesSelect);

        float xOffset = ascensionGlyphsLabel.hb.x + ROW_OFFSET * 4f;
        for (AbstractGlyphBlight glyph : AbstractPlayerData.GLYPHS) {
            glyphEditors.add(new PCLGlyphEditor(glyph, new EUIHitbox(xOffset, ascensionGlyphsLabel.hb.y, glyph.hb.width, glyph.hb.height)));
            xOffset += ROW_OFFSET * 1.7f;
        }
    }

    @Override
    public int ascensionLevel() {
        return charScreen != null ? charScreen.ascensionLevel : 0;
    }

    @Override
    public void disableConfirm(boolean value) {
        if (charScreen != null) {
            charScreen.confirmButton.isDisabled = value;
        }
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

    private ArrayList<AbstractRelic> getAvailableRelics() {
        ArrayList<AbstractRelic> relics = new ArrayList<>(GameUtilities.getRelics(data.resources.cardColor).values());

        // Get additional relics that this character can use
        String[] additional = data.getAdditionalRelicIDs();
        if (additional != null) {
            for (String id : additional) {
                relics.add(RelicLibrary.getRelic(id));
            }
        }

        // Get other non base-game relics
        for (AbstractRelic r : GameUtilities.getRelics(AbstractCard.CardColor.COLORLESS).values()) {
            if (EUIGameUtils.getModInfo(r) != null && GameUtilities.isRelicTierSpawnable(r.tier)) {
                relics.add(r);
            }
        }

        List<String> startingRelics = data.getStartingRelics();
        relics.removeIf(r -> {
            if (UnlockTracker.isRelicLocked(r.relicId) || startingRelics.contains(r.relicId)) {
                return true;
            }
            for (PCLLoadout loadout : data.loadouts.values()) {
                if (loadout.isRelicFromLoadout(r.relicId) && loadout.isLocked()) {
                    return true;
                }
            }
            return false;
        });

        return relics;
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

    private void openRelicsDialog() {
        if (data != null) {
            currentDialog = new ViewInGameRelicPoolEffect(getAvailableRelics(), new HashSet<>(data.config.bannedRelics.get()))
                    .addCallback((effect) -> {
                        data.config.bannedRelics.set(effect.bannedRelics);
                    });
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
            relicsButton.setActive(true);
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
                    relicsButton.makeTour(true),
                    loadoutEditorButton.makeTour(true),
                    infoButton.makeTour(true),
                    resetButton.makeTour(true));
        }
        else {
            playEffect = null;
            seriesButton.setActive(false);
            relicsButton.setActive(false);
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
        EUIClassUtils.setField(characterOption, "hp", String.valueOf(loadout.getHP()));
        ArrayList<String> startingRelics = loadout.getStartingRelics();
        ((CharSelectInfo) EUIClassUtils.getField(characterOption, "charInfo")).relics = startingRelics;
        cachedRelics = EUIUtils.map(startingRelics, RelicLibrary::getRelic);

        // Instead of continually refreshing relics at every render, change them only when the character or loadout changes
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
        relicsButton.tryRender(sb);
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
            relicsButton.tryUpdate();
            loadoutEditorButton.tryUpdate();
            infoButton.tryUpdate();
            resetButton.tryUpdate();
            startingCardsLabel.tryUpdate();
            startingCardsListLabel.tryUpdate();
            ascensionGlyphsLabel.tryUpdate();
            for (PCLGlyphEditor geditor : glyphEditors) {
                geditor.tryUpdate();
            }
            if (cachedRelics != null && characterOption != null) {
                for (int i = 0; i < cachedRelics.size(); i++) {
                    AbstractRelic r = cachedRelics.get(i);
                    r.currentX = getInfoX() + i * 72.0F * Settings.scale * (1.01F - 0.019F * cachedRelics.size());
                    r.currentY = getInfoY() - 60.0F * Settings.scale;
                    r.hb.move(r.currentX, r.currentY);
                    r.hb.update();
                }
            }
        }
    }

    public void renderRelicInfo(SpriteBatch sb) {
        if (cachedRelics != null) {
            for (AbstractRelic r : cachedRelics) {
                r.renderWithoutAmount(sb, Color.WHITE);
            }
        }
    }

    // When rendering PCL players, we should use our own relic method because the default method won't render PCL relics properly
    public boolean shouldRenderPCLRelics() {
        return startingCardsLabel.isActive && cachedRelics != null;
    }

    public void updateForAscension() {
        for (PCLGlyphEditor glyphEditor : glyphEditors) {
            glyphEditor.refresh(ascensionLevel());
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