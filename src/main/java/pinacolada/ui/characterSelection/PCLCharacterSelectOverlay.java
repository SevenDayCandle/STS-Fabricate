package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.Diverse;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.*;
import extendedui.text.EUITextHelper;
import extendedui.ui.EUIBase;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.screens.CustomCardLibraryScreen;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLPlayerMeter;
import pinacolada.dungeon.modifiers.AbstractGlyph;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.screen.PCLYesNoConfirmationEffect;
import pinacolada.effects.screen.ViewInGameCardPoolEffect;
import pinacolada.effects.screen.ViewInGameRelicPoolEffect;
import pinacolada.interfaces.providers.RunAttributesProvider;
import pinacolada.resources.PCLPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.loadout.PCLLoadoutValidation;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

// Copied and modified from STS-AnimatorMod
public class PCLCharacterSelectOverlay extends EUIBase implements RunAttributesProvider {
    private static final float GAP_Y = scale(55);
    private static final float POS_X = 1100f * Settings.scale;
    private static final float POS_Y = Settings.HEIGHT * 0.47f;
    private static final float ROW_OFFSET = 60 * Settings.scale;

    private final ArrayList<PCLLoadout> loadouts;
    private final ArrayList<PCLGlyphEditor> glyphEditors;
    private final ArrayList<EUIButton> activeButtons;
    private ArrayList<AbstractBlight> cachedBlights;
    private ArrayList<AbstractRelic> cachedRelics;
    private CharacterSelectScreen charScreen;
    private CharacterOption characterOption;
    private PCLPlayerData<?, ?, ?> data;
    private PCLLoadout loadout;
    private PCLEffect currentDialog;
    public PCLEffect playEffect;
    public EUIButton seriesButton;
    public EUIButton editCardsButton;
    public EUIButton editRelicsButton;
    public EUIButton loadoutEditorButton;
    public EUIButton infoButton;
    public EUIButton resetButton;
    public EUILabel startingCardsLabel;
    public EUILabel ascensionGlyphsLabel;
    public EUITextBox startingCardsListLabel;


    public PCLCharacterSelectOverlay() {
        final float leftTextWidth = FontHelper.getSmartWidth(FontHelper.cardTitleFont, PGR.core.strings.csel_leftText, 9999f, 0f) + scale(10);

        loadouts = new ArrayList<>();
        glyphEditors = new ArrayList<>();
        activeButtons = new ArrayList<>();

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
        final float buttonheight = scale(46);
        final float textScale = 0.8f;

        resetButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_resetTutorial)
                .setTooltip(PGR.core.strings.csel_resetTutorial, PGR.core.strings.csel_resetTutorialInfo)
                .setColor(new Color(0.8f, 0.3f, 0.5f, 1))
                .setOnClick(this::openResetDialog);

        infoButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_charTutorial)
                .setTooltip(PGR.core.strings.csel_charTutorial, PGR.core.strings.csel_charTutorialInfo)
                .setColor(new Color(0.5f, 0.3f, 0.8f, 1))
                .setOnClick(this::openInfo);

        loadoutEditorButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_deckEditor)
                .setTooltip(PGR.core.strings.csel_deckEditor, PGR.core.strings.csel_deckEditorInfo)
                .setColor(new Color(0.3f, 0.5f, 0.8f, 1))
                .setOnClick(this::openLoadoutEditor);

        seriesButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_seriesEditor)
                .setTooltip(PGR.core.strings.csel_seriesEditor, PGR.core.strings.csel_seriesEditorInfo)
                .setColor(new Color(0.3f, 0.8f, 0.5f, 1))
                .setOnClick(this::openSeriesSelect);

        editCardsButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.csel_seriesEditor)
                .setTooltip(PGR.core.strings.csel_seriesEditor, PGR.core.strings.csel_seriesEditorInfo)
                .setColor(new Color(0.5f, 0.8f, 0.3f, 1))
                .setOnClick(this::previewCards);

        editRelicsButton = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(0, 0, buttonWidth, buttonheight))
                .setLabel(EUIFontHelper.cardTitleFontSmall, textScale, PGR.core.strings.sui_relicPool)
                .setTooltip(PGR.core.strings.sui_relicPool, PGR.core.strings.sui_relicPoolInfo)
                .setColor(new Color(0.3f, 0.8f, 0.5f, 1))
                .setOnClick(this::previewRelics);

        float xOffset = ascensionGlyphsLabel.hb.x + ROW_OFFSET * 4f;
        for (AbstractGlyph glyph : PCLPlayerData.GLYPHS) {
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

    @Override
    public void disableConfirm(boolean value) {
        if (charScreen != null) {
            charScreen.confirmButton.isDisabled = value;
        }
    }

    protected ArrayList<AbstractCard> getAvailableCards() {
        ArrayList<AbstractCard> group = new ArrayList<>();

        // Add loadout cards
        for (PCLLoadout series : data.getEveryLoadout()) {
            if (!series.isLocked() && (!series.isCore() || data.canEditCore())) {
                for (PCLCardData cardData : series.cardDatas) {
                    group.add(cardData.makeCardFromLibrary(0));
                }
            }
        }

        // Add available colorless
        for (AbstractCard c : CustomCardLibraryScreen.getCards(AbstractCard.CardColor.COLORLESS)) {
            if (PCLSeriesSelectScreen.isRarityAllowed(c.rarity, c.type) && data.resources.containsColorless(c)) {
                group.add(c);
            }
        }

        // Get additional cards that this character can use
        String[] additional = data.getAdditionalCardIDs();
        if (additional != null) {
            for (String id : additional) {
                group.add(CardLibrary.getCard(id));
            }
        }

        return group;
    }

    protected ArrayList<AbstractRelic> getAvailableRelics() {
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

        relics.removeIf(r -> {
            return UnlockTracker.isRelicLocked(r.relicId);
        });

        return relics;
    }

    protected float getInfoX() {
        return EUIClassUtils.getField(characterOption, "infoX");
    }

    protected float getInfoY() {
        return EUIClassUtils.getField(characterOption, "infoY");
    }

    public boolean hasDialog() {
        return currentDialog != null;
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

    private void positionButton(EUIButton button) {
        button.setPosition(startingCardsListLabel.hb.cX, startingCardsListLabel.hb.y + (GAP_Y * (activeButtons.size() + 2)));
        activeButtons.add(button);
    }

    private void previewCards() {
        currentDialog = new ViewInGameCardPoolEffect(getAvailableCards(), new HashSet<>(data.config.bannedCards.get()))
                .addCallback((effect) -> {
                    data.config.bannedCards.set(effect.bannedCards);
                });
    }

    private void previewRelics() {
        currentDialog = new ViewInGameRelicPoolEffect(getAvailableRelics(), new HashSet<>(data.config.bannedRelics.get()))
                .addCallback((effect) -> {
                    data.config.bannedRelics.set(effect.bannedRelics);
                });
    }

    public void refresh(CharacterOption characterOption) {
        refresh(characterOption, GameUtilities.isPCLPlayerClass(characterOption.c.chosenClass));
    }

    public void refresh(CharacterOption characterOption, boolean canOpen) {
        this.characterOption = characterOption;
        activeButtons.clear();

        if (characterOption != null && canOpen) {
            EUI.actingColor = characterOption.c.getCardColor();
            refreshPlayerData();
            refreshInternal();
        }

        if (canOpen && data != null) {
            playEffect = data.getCharSelectScreenAnimation();
            startingCardsLabel.setActive(true);
            startingCardsListLabel.setActive(true);

            if (data.hasTutorials()) {
                positionButton(resetButton);
            }
            PCLPlayerMeter meter = CombatManager.playerSystem.getMeter(data.resources.playerClass);
            if (meter != null && meter.getInfoPages() != null && meter.getInfoPages().length > 0) {
                positionButton(infoButton);
            }
            positionButton(loadoutEditorButton);
            if (data.canEditPool()) {
                if (data.loadouts.size() > 1) {
                    positionButton(seriesButton);
                }
                else {
                    positionButton(editCardsButton);
                    positionButton(editRelicsButton);
                }
            }

            // Only show the glyphs if any glyphs are unlocked
            for (PCLGlyphEditor geditor : glyphEditors) {
                geditor.refresh(ascensionLevel());
            }

            boolean showGlyphs = EUIUtils.any(glyphEditors, geditor -> geditor.enabled);
            ascensionGlyphsLabel.setActive(showGlyphs);
            for (PCLGlyphEditor geditor : glyphEditors) {
                geditor.setActive(showGlyphs);
            }

            if (activeButtons.size() > 0) {
                EUITourTooltip.queueFirstView(PGR.config.tourCharSelect,
                        EUIUtils.map(activeButtons, b -> b.makeTour(true)));
            }

        }
        else {
            playEffect = null;
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
        startingCardsListLabel.setLabel(loadout.getDeckPreviewString());
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

        if (data != null) {
            final int unlockLevel = data.resources.getUnlockLevel();
            this.loadouts.addAll(data.loadouts.values());

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
        startingCardsLabel.tryRender(sb);
        startingCardsListLabel.tryRender(sb);
        ascensionGlyphsLabel.tryRender(sb);
        for (EUIButton button : activeButtons) {
            button.renderImpl(sb);
        }
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

        if (cachedBlights != null && cachedBlights.size() > 0) {
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
            startingCardsLabel.tryUpdate();
            startingCardsListLabel.tryUpdate();
            ascensionGlyphsLabel.tryUpdate();
            for (EUIButton button : activeButtons) {
                button.updateImpl();
            }
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