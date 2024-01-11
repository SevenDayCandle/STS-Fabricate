package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.actions.cards.ModifyCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsForModifierEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModify;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public abstract class PMove_PermanentModify extends PMove_Modify<PField_CardModify> implements OutOfCombatMove {

    public PMove_PermanentModify(PSkillData<PField_CardModify> data, int amount, int extra, PCLCardGroupHelper... groups) {
        super(data, amount, extra, groups);
    }

    public PMove_PermanentModify(PSkillData<PField_CardModify> data, PSkillSaveData content) {
        super(data, content);
    }

    @Override
    public ActionT1<AbstractCard> getAction(PCLUseInfo info, PCLActions order) {
        return (c) -> {
            int actualAmount = refreshAmount(info);
            order.add(modifyCard(c, actualAmount, fields.forced, !fields.not, fields.or));
            AbstractCard deckCopy = GameUtilities.getMasterDeckInstance(c.uuid);
            if (deckCopy != null && deckCopy != c) {
                order.add(modifyCard(deckCopy, actualAmount, fields.forced, !fields.not, fields.or));
            }
        };
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.subjects_permanentlyX(super.getSampleText(callingSkill, parentSkill));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.subjects_permanentlyX(super.getSubText(perspective, requestor));
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        int actualAmount = refreshAmount(info);
        List<? extends AbstractCard> list = info.getDataAsList(AbstractCard.class);
        if (list != null) {
            PCLEffects.Queue.callback(() -> {
                        for (AbstractCard c : list) {
                            applyModifierOutsideOfCombat(c, actualAmount);
                        }
                    })
                    .addCallback(() -> {
                        super.useOutsideOfBattle(info);
                    });
        }
        else {
            PCLEffects.Queue.add(new ChooseCardsForModifierEffect(this, c -> {
                        applyModifierOutsideOfCombat(c, actualAmount);
                    }))
                    .addCallback(effect -> {
                        info.setData(effect.cards);
                        super.useOutsideOfBattle(info);
                    });
        }
    }

    protected abstract void applyModifierOutsideOfCombat(AbstractCard c, int amount);

    protected abstract ModifyCard modifyCard(AbstractCard c, int amount, boolean forced, boolean relative, boolean untilPlayed);
}
