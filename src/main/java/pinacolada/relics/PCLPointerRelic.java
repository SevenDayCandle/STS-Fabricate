package pinacolada.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.skills.PSkill;
import pinacolada.skills.Skills;

// TODO Add more overrides
public class PCLPointerRelic extends PCLRelic implements PointerProvider
{
    public Skills skills;

    public PCLPointerRelic(String id, RelicTier tier, LandingSound sfx)
    {
        super(id, tier, sfx);
    }

    public PCLPointerRelic(String id, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass pc)
    {
        super(id, tier, sfx, pc);
    }

    public PCLPointerRelic(String id, Texture texture, RelicTier tier, LandingSound sfx)
    {
        super(id, texture, tier, sfx);
    }

    public PCLPointerRelic(String id, Texture texture, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass pc)
    {
        super(id, texture, tier, sfx, pc);
    }

    public void setup()
    {
    }

    @Override
    public Skills getSkills()
    {
        return skills;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getID()
    {
        return relicId;
    }

    @Override
    public int xValue()
    {
        return counter;
    }

    @Override
    public void atBattleStart()
    {
        super.atBattleStart();
        for (PSkill be : getEffects())
        {
            be.triggerOnStartup();
        }
    }

    @Override
    public void atTurnStartPostDraw()
    {
        super.atTurnStartPostDraw();
        for (PSkill be : getEffects())
        {
            be.triggerOnStartOfTurn();
        }
    }

    @Override
    public void onPlayCard(AbstractCard targetCard, AbstractMonster m)
    {
        super.onPlayCard(targetCard, m);
        for (PSkill be : getEffects())
        {
            be.triggerOnOtherCardPlayed(targetCard);
        }
    }

    @Override
    public void onPlayerEndTurn()
    {
        super.onPlayerEndTurn();
        for (PSkill be : getEffects())
        {
            be.triggerOnEndOfTurn(true);
        }
    }

    @Override
    public void onShuffle()
    {
        super.onShuffle();
        for (PSkill be : getEffects())
        {
            be.triggerOnShuffle(true);
        }
    }

    // Gets called before skills are initialized
    @Override
    public String getUpdatedDescription()
    {
        if (skills == null)
        {
            skills = new Skills();
            setup();
        }
        try
        {
            return EUIUtils.joinStrings(" ", EUIUtils.map(getEffects(), PSkill::getPowerText));
        }
        catch (Exception e)
        {
            return "";
        }
    }
}
