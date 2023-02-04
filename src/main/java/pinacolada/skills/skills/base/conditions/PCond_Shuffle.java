package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnShuffleSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_Shuffle extends PPassiveCond<PField_Empty> implements OnShuffleSubscriber
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_Shuffle.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PCond_Shuffle()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Shuffle(PSkillSaveData content)
    {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public String getSampleText()
    {
        return TEXT.subjects_shuffleYourDeck;
    }

    @Override
    public String getSubText()
    {
        return TEXT.cond_wheneverYou(TEXT.subjects_shuffleYourDeck);
    }

    @Override
    public void onShuffle(boolean triggerRelics)
    {
        tryPassParent(makeInfo(null));
    }

    @Override
    public void use(PCLUseInfo info)
    {
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
    }

    @Override
    public boolean canPlay(AbstractCard card, AbstractMonster m)
    {
        return true;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fromTrigger;
    }
}
