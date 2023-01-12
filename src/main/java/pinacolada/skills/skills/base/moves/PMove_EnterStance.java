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
import pinacolada.skills.fields.PField_Stance;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PMove_EnterStance extends PMove<PField_Stance>
{
    public static final PSkillData<PField_Stance> DATA = register(PMove_EnterStance.class, PField_Stance.class, 1, 1)
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
        super(DATA);
        fields.setStance(stance);
    }

    public void chooseEffect(PCLUseInfo info, List<PCLStanceHelper> choices)
    {
        if (fields.random)
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
        if (fields.stances.isEmpty())
        {
            getActions().changeStance(NeutralStance.STANCE_ID);
        }
        else if (extra > 0)
        {
            chooseEffect(info, PCLStanceHelper.inGameValues(AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS));
        }
        else if (fields.stances.size() == 1)
        {
            getActions().changeStance(fields.stances.get(0));
        }
        else
        {
            chooseEffect(info, fields.stances);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        if (fields.stances.isEmpty())
        {
            return TEXT.actions.exitStance();
        }
        if (extra > 0)
        {
            return fields.random ? TEXT.subjects.randomly(TEXT.actions.enterStance(TEXT.subjects.anyX(PGR.core.tooltips.stance))) : TEXT.actions.enterStance(TEXT.subjects.anyX(PGR.core.tooltips.stance));
        }
        return fields.random ? TEXT.subjects.randomly(TEXT.actions.enterStance(fields.getStanceString())) : TEXT.actions.enterStance(fields.getStanceString());
    }
}
