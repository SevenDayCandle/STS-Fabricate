package pinacolada.ui.characterSelection;

import extendedui.ui.controls.EUICanvas;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class PCLLoadoutCanvas extends EUICanvas {
    protected final ArrayList<PCLCardSlotEditor> cardEditors = new ArrayList<>();
    protected final ArrayList<PCLRelicSlotEditor> relicsEditors = new ArrayList<>();
    protected final ArrayList<PCLAbilityEditor> abilityEditors = new ArrayList<>();
    protected final PCLLoadoutScreen screen;
    protected EUILabel deckText;
    protected EUILabel relicText;
    protected EUILabel abilityText;

    public PCLLoadoutCanvas(PCLLoadoutScreen screen) {
        this.screen = screen;

        final float buttonHeight = screenH(0.07f);
        deckText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.1f), screenH(0.84f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_deckHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.01f);

        relicText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.1f), screenH(0.4f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_relicHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.01f);

        abilityText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.1f), screenH(0.2f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.csel_ability)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.01f);
    }

    protected void addCardSlot() {

    }

    public void open() {

    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        deckText.updateImpl();
        relicText.updateImpl();
        abilityText.updateImpl();
        for (PCLCardSlotEditor editor : cardEditors) {
            editor.tryUpdate();
        }
        for (PCLRelicSlotEditor editor : relicsEditors) {
            editor.tryUpdate();
        }
        for (PCLAbilityEditor editor : abilityEditors) {
            editor.tryUpdate();
        }
    }
}
