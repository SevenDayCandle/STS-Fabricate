package pinacolada.ui.characterSelection;

import extendedui.ui.controls.EUICanvas;

import java.util.ArrayList;

public class PCLLoadoutCanvas extends EUICanvas {
    protected final ArrayList<PCLCardSlotEditor> cardEditors = new ArrayList<>();
    protected final ArrayList<PCLRelicSlotEditor> relicsEditors = new ArrayList<>();
    protected final ArrayList<PCLAbilityEditor> abilityEditors = new ArrayList<>();
}
