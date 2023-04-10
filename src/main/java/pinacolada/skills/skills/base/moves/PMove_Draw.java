package pinacolada.skills.skills.base.moves;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCallbackMove;

@VisibleSkill
public class PMove_Draw extends PCallbackMove<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Draw.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile)
            .setOrigins(PCLCardSelection.Top);

    public PMove_Draw()
    {
        this(1);
    }

    public PMove_Draw(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_Draw(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_draw(TEXT.subjects_x);
    }

    @Override
    public void use(PCLUseInfo info, ActionT1<PCLUseInfo> callback)
    {
        getActions().draw(amount)
                .setFilter(fields.getFullCardFilter(), true)
                .addCallback(ca -> {
                    info.setData(ca);
                    callback.invoke(info);
                    if (this.childEffect != null)
                    {
                        this.childEffect.use(info);
                    }
                });
    }

    @Override
    public String getSubText()
    {
        return TEXT.act_drawType(getAmountRawString(), fields.getFullCardString());
    }
}
