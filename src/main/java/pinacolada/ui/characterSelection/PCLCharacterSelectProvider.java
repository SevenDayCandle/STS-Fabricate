package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.text.EUISmartText;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.pcl.replacement.RitualDagger;
import pinacolada.interfaces.markers.RunAttributesProvider;
import pinacolada.patches.UnlockTrackerPatches;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class PCLCharacterSelectProvider implements RunAttributesProvider
{
    protected final PCLCharacterSelectOptionsRenderer loadoutRenderer = new PCLCharacterSelectOptionsRenderer();
    protected CharacterSelectScreen charScreen;
    protected CharacterOption selectedOption;
    protected EUITutorial simpleModeInfo;
    protected EUILabel simpleModeOn;
    protected EUILabel simpleModeOff;
    protected RitualDagger simplePreview;
    protected RitualDagger complexPreview;

    @Override
    public int ascensionLevel()
    {
        return charScreen != null ? charScreen.ascensionLevel : 0;
    }

    @Override
    public void disableConfirm(boolean value)
    {
        if (charScreen != null)
        {
            charScreen.confirmButton.isDisabled = value;
        }
    }

    public AbstractPlayer.PlayerClass getCurrentClass(CharacterOption instance)
    {
        return selectedOption != null && selectedOption.c != null ? selectedOption.c.chosenClass : null;
    }

    public void initialize(CharacterSelectScreen selectScreen)
    {
        GameUtilities.unlockAllKeys();
        charScreen = selectScreen;
        selectedOption = null;

        final float size = Settings.scale * 36;

        for (PCLResources<?,?,?> resources : PGR.getAllResources())
        {
            UnlockTrackerPatches.validate(resources);
        }
    }

    protected void openFtue()
    {
        simplePreview = new RitualDagger();
        PCLCard.toggleSimpleMode(simplePreview, true);
        complexPreview = new RitualDagger();
        PCLCard.toggleSimpleMode(complexPreview, false);
        simplePreview.current_x = simplePreview.target_x = Settings.WIDTH * 0.42f;
        complexPreview.current_x = complexPreview.target_x = Settings.WIDTH * 0.58f;
        simplePreview.current_y = simplePreview.target_y = complexPreview.current_y = complexPreview.target_y = Settings.HEIGHT * 0.22f;
        simplePreview.hb.move(simplePreview.current_x, simplePreview.current_y);
        complexPreview.hb.move(complexPreview.current_x, complexPreview.current_y);

        simpleModeOn = new EUILabel(EUIFontHelper.cardtitlefontSmall, RelativeHitbox.fromPercentages(simplePreview.hb, 1, 0.5f, 0, -0.6f))
                .setLabel(PGR.core.strings.misc.simpleMode)
                .setColor(Settings.BLUE_TEXT_COLOR);
        simpleModeOff = new EUILabel(EUIFontHelper.cardtitlefontSmall, RelativeHitbox.fromPercentages(complexPreview.hb, 1, 0.5f, 0, -0.6f))
                .setLabel(PGR.core.strings.misc.complexMode)
                .setColor(EUISmartText.ORANGE_TEXT_COLOR);
        simpleModeOn.updateImpl();
        simpleModeOff.updateImpl();

        simpleModeInfo = new EUITutorial(
                new EUIHitbox((float) Settings.WIDTH / 2.0F - 675.0F, Settings.OPTION_Y - 100.0F, 1350.0F, 720.0F), EUIRM.images.panelLarge.texture(),
                LeaderboardScreen.TEXT[2], EUIUtils.list(PGR.core.strings.tutorial.characterTutorial1, PGR.core.strings.tutorial.characterTutorial2));
        simpleModeInfo.setPostRenders(sb -> {
            simplePreview.render(sb);
            complexPreview.render(sb);
            simpleModeOn.renderImpl(sb);
            simpleModeOff.renderImpl(sb);
        }, sb -> {
            loadoutRenderer.loadoutEditorButton.background.renderCentered(sb, Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.22f, loadoutRenderer.loadoutEditorButton.hb.width, loadoutRenderer.loadoutEditorButton.hb.height);
        });
        PGR.core.ftueScreen.open(simpleModeInfo);

    }

    public void randomizeLoadout()
    {
        loadoutRenderer.randomizeLoadout();
    }

    public void render(CharacterSelectScreen selectScreen, SpriteBatch sb)
    {
        // RenderOption is being called instead
    }

    public void renderOption(CharacterOption instance, SpriteBatch sb)
    {
        AbstractPlayer.PlayerClass pc = getCurrentClass(instance);
        loadoutRenderer.renderImpl(sb);
    }

    public void update(CharacterSelectScreen selectScreen)
    {
        updateSelectedCharacter(selectScreen);
    }

    public void updateForAscensionChange(CharacterSelectScreen selectScreen)
    {
        loadoutRenderer.updateForAscension();
    }

    public void updateOption(CharacterOption instance)
    {
        AbstractPlayer.PlayerClass pc = getCurrentClass(instance);
        loadoutRenderer.updateImpl();
    }

    private void updateSelectedCharacter(CharacterSelectScreen selectScreen)
    {
        charScreen = selectScreen;
        final CharacterOption current = selectedOption;

        selectedOption = null;

        for (CharacterOption o : selectScreen.options)
        {
            if (o.selected)
            {
                selectedOption = o;

                if (current != o)
                {
                    loadoutRenderer.refresh(this, o);
                    if (GameUtilities.isPCLPlayerClass(o.c.chosenClass) && !PGR.core.config.simpleModeFtueSeen.get())
                    {
                        PGR.core.config.simpleModeFtueSeen.set(true);
                        openFtue();
                    }
                }

                return;
            }
        }
    }
}
