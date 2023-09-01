package pinacolada.skills.skills.base.moves;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCallbackMove;

@VisibleSkill
public class PMove_Draw extends PCallbackMove<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Draw.class, PField_CardCategory.class)
            .noTarget()
            .setGroups(PCLCardGroupHelper.DrawPile)
            .setOrigins(PCLCardSelection.Top);

    public PMove_Draw() {
        this(1);
    }

    public PMove_Draw(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PMove_Draw(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_draw(TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.act_drawType(getAmountRawString(), fields.getFullCardString());
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> callback) {
        order.draw(amount)
                .setFilter(fields.getFullCardFilter(), false)
                .addCallback(ca -> {
                    info.setData(ca);
                    callback.invoke(info);
                    if (this.childEffect != null) {
                        this.childEffect.use(info, order);
                    }
                });
    }
}
