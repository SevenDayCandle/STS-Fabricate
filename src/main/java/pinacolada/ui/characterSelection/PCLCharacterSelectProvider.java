package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import pinacolada.interfaces.providers.RunAttributesProvider;
import pinacolada.patches.UnlockTrackerPatches;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

// TODO merge with PCLCharacterSelectOptionsRenderer
@Deprecated
public class PCLCharacterSelectProvider implements RunAttributesProvider {
    protected final PCLCharacterSelectOptionsRenderer loadoutRenderer = new PCLCharacterSelectOptionsRenderer();
    protected CharacterSelectScreen charScreen;
    protected CharacterOption selectedOption;

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

    public void initialize(CharacterSelectScreen selectScreen) {
        GameUtilities.unlockAllKeys();
        charScreen = selectScreen;
        selectedOption = null;

        final float size = Settings.scale * 36;

        for (PCLResources<?, ?, ?, ?> resources : PGR.getRegisteredResources()) {
            UnlockTrackerPatches.validate(resources);
        }
    }

    public void randomizeLoadout() {
        loadoutRenderer.randomizeLoadout();
    }

    public void render(CharacterSelectScreen selectScreen, SpriteBatch sb) {
        // RenderOption is being called instead
    }

    public void renderOption(CharacterOption instance, SpriteBatch sb) {
        AbstractPlayer.PlayerClass pc = getCurrentClass(instance);
        loadoutRenderer.renderImpl(sb);
    }

    public AbstractPlayer.PlayerClass getCurrentClass(CharacterOption instance) {
        return selectedOption != null && selectedOption.c != null ? selectedOption.c.chosenClass : null;
    }

    public void update(CharacterSelectScreen selectScreen) {
        updateSelectedCharacter(selectScreen);
    }

    private void updateSelectedCharacter(CharacterSelectScreen selectScreen) {
        charScreen = selectScreen;
        final CharacterOption current = selectedOption;

        selectedOption = null;

        for (CharacterOption o : selectScreen.options) {
            if (o.selected) {
                selectedOption = o;

                if (current != o) {
                    loadoutRenderer.refresh(this, o);
                }

                return;
            }
        }
    }

    public void updateForAscensionChange(CharacterSelectScreen selectScreen) {
        loadoutRenderer.updateForAscension();
    }

    public void updateOption(CharacterOption instance) {
        AbstractPlayer.PlayerClass pc = getCurrentClass(instance);
        loadoutRenderer.updateImpl();
    }
}
