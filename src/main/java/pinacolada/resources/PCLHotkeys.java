package pinacolada.resources;

import com.badlogic.gdx.Input;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;

import java.util.HashMap;

public class PCLHotkeys {
    private static final String KEYMAP_CONTROL_PILE_CHANGE = PCLMainConfig.createFullID("ControlPileChange");
    private static final String KEYMAP_CONTROL_PILE_SELECT = PCLMainConfig.createFullID("ControlPileSelect");
    private static final String KEYMAP_VIEW_AUGMENT_SCREEN = PCLMainConfig.createFullID("ViewAugmentScreen");
    private static final String KEYMAP_REROLL_CURRENT = PCLMainConfig.createFullID("RerollCurrent");
    private static final String KEYMAP_TOGGLE_FORMULA_DISPLAY = PCLMainConfig.createFullID("ToggleFormulaDisplay");
    public static final HashMap<Integer, Integer> EQUIVALENT_KEYS = new HashMap<>();
    public static InputAction controlPileChange;
    public static InputAction controlPileSelect;
    public static InputAction viewAugmentScreen;
    public static InputAction rerollCurrent;
    public static InputAction toggleFormulaDisplay;

    static {
        EQUIVALENT_KEYS.put(Input.Keys.ALT_LEFT, Input.Keys.ALT_RIGHT);
        EQUIVALENT_KEYS.put(Input.Keys.ALT_RIGHT, Input.Keys.ALT_LEFT);
        EQUIVALENT_KEYS.put(Input.Keys.CONTROL_LEFT, Input.Keys.CONTROL_RIGHT);
        EQUIVALENT_KEYS.put(Input.Keys.CONTROL_RIGHT, Input.Keys.CONTROL_LEFT);
        EQUIVALENT_KEYS.put(Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT);
        EQUIVALENT_KEYS.put(Input.Keys.SHIFT_RIGHT, Input.Keys.SHIFT_LEFT);
    }

    public static void load() {
        controlPileChange = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_CONTROL_PILE_CHANGE, Input.Keys.T));
        controlPileSelect = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_CONTROL_PILE_SELECT, Input.Keys.Y));
        viewAugmentScreen = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_VIEW_AUGMENT_SCREEN, Input.Keys.U));
        rerollCurrent = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_REROLL_CURRENT, Input.Keys.Q));
        toggleFormulaDisplay = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_TOGGLE_FORMULA_DISPLAY, Input.Keys.F));
    }

    public static void resetToDefaults() {
        controlPileChange.remap(Input.Keys.T);
        controlPileSelect.remap(Input.Keys.Y);
        viewAugmentScreen.remap(Input.Keys.U);
        rerollCurrent.remap(Input.Keys.Q);
        toggleFormulaDisplay.remap(Input.Keys.F);
    }

    public static void save() {
        InputActionSet.prefs.putInteger(KEYMAP_CONTROL_PILE_CHANGE, controlPileChange.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_CONTROL_PILE_SELECT, controlPileSelect.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_VIEW_AUGMENT_SCREEN, viewAugmentScreen.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_REROLL_CURRENT, rerollCurrent.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_TOGGLE_FORMULA_DISPLAY, toggleFormulaDisplay.getKey());
    }
}