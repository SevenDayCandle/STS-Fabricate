package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.stances.NeutralStance;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PMove_EnterStance extends PMove
{
    public static final PSkillData DATA = register(PMove_EnterStance.class, PCLEffectType.Stance, 1, 1)
            .selfTarget();

    public PMove_EnterStance()
    {
        this((PCLStanceHelper) null);
    }

    public PMove_EnterStance(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_EnterStance(PCLStanceHelper... stance)
    {
        super(DATA, stance);
    }

    public void chooseEffect(PCLUseInfo info, List<PCLStanceHelper> choices)
    {
        if (alt)
        {
            getActions().changeStance(GameUtilities.getRandomElement(choices));
            return;
        }
        getActions().tryChooseSkill(getPCLSource().cardData, amount, info.source, info.target, EUIUtils.map(choices, PMove::enterStance));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.enterStance("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (stances.isEmpty())
        {
            getActions().changeStance(NeutralStance.STANCE_ID);
        }
        else if (extra > 0)
        {
            chooseEffect(info, PCLStanceHelper.inGameValues(AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS));
        }
        else if (stances.size() == 1)
        {
            getActions().changeStance(stances.get(0));
        }
        else
        {
            chooseEffect(info, stances);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        if (stances.isEmpty())
        {
            return TEXT.actions.exitStance();
        }
        if (extra > 0)
        {
            return alt ? TEXT.subjects.randomly(TEXT.actions.enterStance(TEXT.subjects.anyX(PGR.core.tooltips.stance))) : TEXT.actions.enterStance(TEXT.subjects.anyX(PGR.core.tooltips.stance));
        }
        return alt ? TEXT.subjects.randomly(TEXT.actions.enterStance(getStanceString())) : TEXT.actions.enterStance(getStanceString());
    }
}
