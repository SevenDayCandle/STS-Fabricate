package pinacolada.misc;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.cards.base.AffinityReactions;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.powers.PCLAffinityPower;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collections;

// TODO create class specific infos
public class PCLUseInfo
{
    public final AbstractCreature source;
    public final AbstractCreature target;
    public final AbstractCard card;
    public final AbstractCard previousCard;
    public final PCLAffinity currentAffinity;
    public final AffinityReactions reactions;
    public final ArrayList<AbstractMonster> enemies;
    public final ArrayList<? extends PCLAffinityPower> auras;
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
        this.auras = CombatManager.playerSystem.getActivePowers();
        if (card != null)
        {
            this.reactions = CombatManager.playerSystem.getReactions(card,
                    target != null ? Collections.singleton(target) :
                            card.target == AbstractCard.CardTarget.ALL_ENEMY ? enemies
                                    : card.target == AbstractCard.CardTarget.SELF ? Collections.singleton(AbstractDungeon.player) : new ArrayList<>());
            this.canActivateSemiLimited = CombatManager.canActivateSemiLimited(card.cardID);
            this.canActivateLimited = CombatManager.canActivateLimited(card.cardID);
            this.isMatch = CombatManager.playerSystem.isMatch(card);
            this.isStarter = GameUtilities.isStarter(card);
        }
        else
        {
            this.reactions = new AffinityReactions();
            this.canActivateSemiLimited = false;
            this.canActivateLimited = false;
            this.isMatch = false;
            this.isStarter = false;
        }

    }

    public <T> T getData(T defaultValue)
    {
        try
        {
            return data != null ? (T) data : defaultValue;
        }
        catch (Exception e)
        {
            EUIUtils.logWarning(this, e.getMessage());
        }
        return defaultValue;
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
