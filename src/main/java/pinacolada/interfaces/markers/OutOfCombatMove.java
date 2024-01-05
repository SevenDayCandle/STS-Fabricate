package pinacolada.interfaces.markers;

import pinacolada.dungeon.PCLUseInfo;

/* Marks skills that can be used outside of combat */
public interface OutOfCombatMove {
    void useOutsideOfBattle(PCLUseInfo info);
}
