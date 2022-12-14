package pinacolada.ui.combat;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUI;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// TODO Merge with PowerFormulaDisplay
public class CombatHelper
{
    private static final ArrayList<CreatureStatus> creatures = new ArrayList<>();

    public static int getHealthBarAmount(AbstractCreature c, int amount, boolean subtractBlock, boolean subtractTempHP)
    {
        if (c == null || (!subtractBlock && !subtractTempHP))
        {
            return amount;
        }

        CreatureStatus status = null;
        for (CreatureStatus s : creatures)
        {
            if (s.owner == c)
            {
                status = s;
                break;
            }
        }

        if (status == null)
        {
            status = new CreatureStatus(c);
            creatures.add(status);
        }

        if (amount > 0 && subtractBlock)
        {
            int blocked = Math.min(status.block, amount);
            status.block -= blocked;
            amount -= blocked;
        }

        if (amount > 0 && subtractTempHP)
        {
            int blocked = Math.min(status.tempHP, amount);
            status.tempHP -= blocked;
            amount -= blocked;
        }

        return Math.max(amount, 0);
    }

    public void clear()
    {
        creatures.clear();
    }

    public void update()
    {
        if (creatures.isEmpty())
        {
            return;
        }

        if (EUI.elapsed100() && !GameUtilities.inBattle())
        {
            clear();
        }
        else
        {
            for (CreatureStatus s : creatures)
            {
                s.refresh();
            }
        }
    }

    protected static class CreatureStatus
    {
        public AbstractCreature owner;
        public int block;
        public int tempHP;

        public CreatureStatus(AbstractCreature creature)
        {
            this.owner = creature;
            refresh();
        }

        public void refresh()
        {
            tempHP = TempHPField.tempHp.get(owner);
            block = owner.currentBlock;
        }
    }
}
