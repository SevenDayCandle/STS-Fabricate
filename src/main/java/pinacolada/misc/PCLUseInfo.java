package pinacolada.misc;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLUseInfo
{
    public final AbstractCreature source;
    public final AbstractCreature target;
    public final AbstractCard card;
    public final AbstractCard previousCard;
    public final PCLAffinity currentAffinity;
    public final ArrayList<AbstractMonster> enemies;
    public final boolean canActivateSemiLimited;
    public final boolean canActivateLimited;
    public final boolean isMatch;
    public final boolean isStarter;
    protected Object data;

    public PCLUseInfo(AbstractCard card, AbstractCreature source, AbstractCreature target)
    {
        this.card = card;
        this.source = source;
        this.target = target;
        this.previousCard = CombatManager.playerSystem.getLastCardPlayed();
        this.currentAffinity = CombatManager.playerSystem.getActiveMeter().getCurrentAffinity();
        this.enemies = GameUtilities.getEnemies(true);
        if (card != null)
        {
            this.canActivateSemiLimited = CombatManager.canActivateSemiLimited(card.cardID);
            this.canActivateLimited = CombatManager.canActivateLimited(card.cardID);
            this.isMatch = CombatManager.playerSystem.isMatch(card);
            this.isStarter = GameUtilities.isStarter(card);
        }
        else
        {
            this.canActivateSemiLimited = false;
            this.canActivateLimited = false;
            this.isMatch = false;
            this.isStarter = false;
        }

    }

    public <T> T getData()
    {
        T item = null;
        try
        {
            item = (T) data;
        }
        catch (Exception e)
        {
            EUIUtils.logWarning(this, e.getMessage());
        }
        return item;
    }

    public String getPreviousCardID()
    {
        return previousCard != null ? previousCard.cardID : "";
    }

    public boolean hasData()
    {
        return data != null;
    }

    public PCLUseInfo setData(Object data)
    {
        this.data = data;
        return this;
    }

    public boolean tryActivateLimited()
    {
        return CombatManager.tryActivateLimited(card.cardID);
    }

    public boolean tryActivateSemiLimited()
    {
        return CombatManager.tryActivateSemiLimited(card.cardID);
    }
}
