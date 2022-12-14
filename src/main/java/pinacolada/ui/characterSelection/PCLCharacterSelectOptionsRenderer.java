package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import pinacolada.blights.common.AbstractGlyphBlight;
import pinacolada.cards.base.PCLCard;
import pinacolada.interfaces.markers.RunAttributesProvider;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLLoadout;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLCharacterSelectOptionsRenderer extends EUIBase
{
    protected static final float POS_X = 170f * Settings.scale;
    protected static final float POS_Y = ((float) Settings.HEIGHT / 2f) - (20 * Settings.scale);
    protected static final float ROW_OFFSET = 60 * Settings.scale;

    protected static final Random RNG = new Random();
    public final ArrayList<PCLGlyphEditor> glyphEditors;
    protected final ArrayList<PCLLoadout> availableLoadouts;
    protected final ArrayList<PCLLoadout> loadouts;
    public EUIButton seriesButton;
    public EUIButton loadoutEditorButton;
    public EUILabel startingCardsLabel;
    public EUILabel ascensionGlyphsLabel;
    public EUITextBox startingCardsListLabel;
    public EUIToggle simpleModeToggle;
    protected RunAttributesProvider runProvider;
    protected CharacterOption characterOption;
    protected PCLAbstractPlayerData data;
    protected PCLLoadout loadout;


    protected float textScale;

    public PCLCharacterSelectOptionsRenderer()
    {
        final float leftTextWidth = FontHelper.getSmartWidth(FontHelper.cardTitleFont, PGR.core.strings.charSelect.leftText, 9999f, 0f); // Ascension
        final float rightTextWidth = FontHelper.getSmartWidth(FontHelper.cardTitleFont, PGR.core.strings.charSelect.rightText, 9999f, 0f); // Level 22

        textScale = Settings.scale;

        //Need to prevent text from disappearing from scaling too big on 4K resolutions
        if (textScale > 1)
        {
            textScale = 1;
        }

        loadouts = new ArrayList<>();
        availableLoadouts = new ArrayList<>();
        glyphEditors = new ArrayList<>();

        startingCardsLabel = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new EUIHitbox(POS_X, POS_Y, leftTextWidth, 50f * Settings.scale))
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(PGR.core.strings.charSelect.leftText);

        startingCardsListLabel = new EUITextBox(EUIRM.images.panelRounded.texture(),
                new EUIHitbox(POS_X + ROW_OFFSET * 3.5f, POS_Y, leftTextWidth, 50f * Settings.scale))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GREEN_TEXT_COLOR)
                .setFont(EUIFontHelper.cardtitlefontSmall, 0.8f)
                .setAlignment(0.5f, 0.5f, false);

        ascensionGlyphsLabel = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new EUIHitbox(POS_X * 7, POS_Y, leftTextWidth, 50f * Settings.scale))
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f, false)
                .setLabel(PGR.core.strings.charSelect.ascensionGlyph);

        float xOffset = ascensionGlyphsLabel.hb.x + ROW_OFFSET * 4f;

        seriesButton = new EUIButton(PGR.core.images.edit.texture(), new EUIHitbox(0, 0, scale(64), scale(64)))
                .setPosition(xOffset + ROW_OFFSET, startingCardsListLabel.hb.y + scale(192)).setText("")
                .setTooltip(PGR.core.strings.charSelect.seriesEditor, PGR.core.strings.charSelect.seriesEditorInfo)
                .setOnClick(this::openSeriesSelect);

        loadoutEditorButton = new EUIButton(PGR.core.images.swapCards.texture(), new EUIHitbox(0, 0, scale(64), scale(64)))
                .setPosition(seriesButton.hb.x + seriesButton.hb.width + scale(40), startingCardsListLabel.hb.y + scale(192)).setText("")
                .setTooltip(PGR.core.strings.charSelect.deckEditor, PGR.core.strings.charSelect.deckEditorInfo)
                .setOnRightClick(this::changePreset)
                .setOnClick(this::openLoadoutEditor);

        for (AbstractGlyphBlight glyph : PCLAbstractPlayerData.Glyphs)
        {
            glyphEditors.add(new PCLGlyphEditor(glyph, new EUIHitbox(xOffset, POS_Y, glyph.hb.width, glyph.hb.height)));
            xOffset += ROW_OFFSET * 1.7f;
        }

        simpleModeToggle = (EUIToggle) new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(ascensionGlyphsLabel.hb.cX, ascensionGlyphsLabel.hb.y + scale(120))
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.475f)
                .setText(PGR.core.strings.misc.cardModeHeader)
                .setOnToggle(this::toggleSimpleMode)
                .setTooltip(PGR.core.strings.misc.simpleMode, PGR.core.strings.misc.simpleModeDescription);
    }

    private void changeLoadout(int index)
    {
        int actualIndex = index % loadouts.size();
        if (actualIndex < 0)
        {
            actualIndex = loadouts.size() - 1;
        }
        data.selectedLoadout = loadouts.get(actualIndex);
        refresh(runProvider, characterOption);
    }

    private void changeLoadout(PCLLoadout loadout)
    {
        data.selectedLoadout = loadout;
        refresh(runProvider, characterOption);
    }

    private void changePreset()
    {
        final int preset = loadout.canChangePreset(loadout.preset + 1) ? (loadout.preset + 1) : 0;
        if (preset != loadout.preset)
        {
            loadout.preset = preset;
            refreshInternal();
        }
    }

    private void openLoadoutEditor()
    {
        if (loadout != null && characterOption != null && data != null)
        {
            PGR.core.loadoutEditor.open(loadout, data, characterOption, () -> screenRefresh(runProvider, characterOption));
        }
    }

    private void openSeriesSelect()
    {
        if (characterOption != null && data != null)
        {
            PGR.core.seriesSelection.open(characterOption, data, () -> screenRefresh(runProvider, characterOption));
        }
    }

    public void randomizeLoadout()
    {
        if (availableLoadouts.size() > 1)
        {
            while (loadout == data.selectedLoadout)
            {
                data.selectedLoadout = GameUtilities.getRandomElement(availableLoadouts, RNG);
            }

            refresh(runProvider, characterOption);
        }
    }

    public void refresh(RunAttributesProvider provider, CharacterOption characterOption)
    {
        this.runProvider = provider;
        this.characterOption = characterOption;

        if (characterOption != null && GameUtilities.isPCLPlayerClass(characterOption.c.chosenClass))
        {
            boolean isSimpleMode = PGR.core.config.simpleMode.get();
            simpleModeToggle.tooltip.setText(isSimpleMode ? PGR.core.strings.misc.simpleMode : PGR.core.strings.misc.complexMode, isSimpleMode ? PGR.core.strings.misc.simpleModeDescription : PGR.core.strings.misc.complexModeDescription);
            EUI.actingColor = characterOption.c.getCardColor();
            this.data = PGR.getPlayerData(characterOption.c.chosenClass);
            this.loadouts.clear();
            this.availableLoadouts.clear();

            final int unlockLevel = data.resources.getUnlockLevel();
            for (PCLLoadout loadout : data.loadouts.values())
            {
                this.loadouts.add(loadout);
                if (unlockLevel >= loadout.unlockLevel)
                {
                    this.availableLoadouts.add(loadout);
                }
            }

            this.loadouts.sort((a, b) ->
            {
                final int diff = a.id - b.id;
                final int level = data.resources.getUnlockLevel();
                final int levelA = a.unlockLevel - level;
                final int levelB = b.unlockLevel - level;
                if (levelA > 0 || levelB > 0)
                {
                    return diff + Integer.compare(levelA, levelB) * 1313;
                }

                return diff;
            });

            this.loadout = data.selectedLoadout;
            if (this.loadout == null || this.loadout.getStartingDeck().isEmpty() || !loadouts.contains(this.loadout))
            {
                this.loadout = data.selectedLoadout = loadouts.get(0);
            }

            refreshInternal();

            seriesButton.setActive(loadouts.size() > 1);
            loadoutEditorButton.setActive(true);
            startingCardsLabel.setActive(provider instanceof PCLCharacterSelectProvider);
            startingCardsListLabel.setActive(provider instanceof PCLCharacterSelectProvider);
            ascensionGlyphsLabel.setActive(true);
            simpleModeToggle.setToggle(PGR.core.config.simpleMode.get()).setActive(true);
            for (PCLGlyphEditor geditor : glyphEditors)
            {
                geditor.refresh(provider.ascensionLevel());
                geditor.setActive(true);
            }
        }
        else
        {
            seriesButton.setActive(false);
            loadoutEditorButton.setActive(false);
            startingCardsLabel.setActive(false);
            startingCardsListLabel.setActive(false);
            ascensionGlyphsLabel.setActive(false);
            simpleModeToggle.setActive(false);
            for (PCLGlyphEditor geditor : glyphEditors)
            {
                geditor.setActive(false);
            }
        }
    }

    public void refreshInternal()
    {
        EUIClassUtils.setField(characterOption, "gold", loadout.getGold());
        EUIClassUtils.setField(characterOption, "hp", String.valueOf(loadout.getHP()));
        ((CharSelectInfo) EUIClassUtils.getField(characterOption, "charInfo")).relics = loadout.getStartingRelics();


        int currentLevel = data.resources.getUnlockLevel();
        if (currentLevel < loadout.unlockLevel)
        {
            startingCardsListLabel.setLabel(PGR.core.strings.charSelect.unlocksAtLevel(loadout.unlockLevel, currentLevel)).setFontColor(Settings.RED_TEXT_COLOR);
            loadoutEditorButton.setInteractable(false);
            runProvider.disableConfirm(true);
        }
        else if (!loadout.validate().isValid)
        {
            startingCardsListLabel.setLabel(PGR.core.strings.charSelect.invalidLoadout).setFontColor(Settings.RED_TEXT_COLOR);
            loadoutEditorButton.setInteractable(true);
            runProvider.disableConfirm(true);
        }
        else
        {
            startingCardsListLabel.setLabel(loadout.getDeckPreviewString(true)).setFontColor(Settings.GREEN_TEXT_COLOR);
            loadoutEditorButton.setInteractable(true);
            runProvider.disableConfirm(false);
        }
    }

    public void screenRefresh(RunAttributesProvider provider, CharacterOption characterOption)
    {
        refresh(provider, characterOption);
        this.runProvider.onRefresh();
    }

    protected void toggleSimpleMode(boolean val)
    {
        PGR.core.config.simpleMode.set(val);
        simpleModeToggle.tooltip.setText(val ? PGR.core.strings.misc.simpleMode : PGR.core.strings.misc.complexMode, val ? PGR.core.strings.misc.simpleModeDescription : PGR.core.strings.misc.complexModeDescription);
        PCLCard.refreshSimpleModePreview(val);
    }

    public void updateImpl()
    {
        seriesButton.tryUpdate();
        loadoutEditorButton.tryUpdate();
        startingCardsLabel.tryUpdate();
        startingCardsListLabel.tryUpdate();
        ascensionGlyphsLabel.tryUpdate();
        simpleModeToggle.tryUpdate();
        for (PCLGlyphEditor geditor : glyphEditors)
        {
            geditor.tryUpdate();
        }
    }

    public void renderImpl(SpriteBatch sb)
    {
        seriesButton.tryRender(sb);
        loadoutEditorButton.tryRender(sb);
        startingCardsLabel.tryRender(sb);
        startingCardsListLabel.tryRender(sb);
        ascensionGlyphsLabel.tryRender(sb);
        simpleModeToggle.tryRender(sb);
        for (PCLGlyphEditor geditor : glyphEditors)
        {
            geditor.tryRender(sb);
        }
    }

    public void updateForAscension()
    {
        if (runProvider != null)
        {
            for (PCLGlyphEditor glyphEditor : glyphEditors)
            {
                glyphEditor.refresh(runProvider.ascensionLevel());
            }
        }
    }
}