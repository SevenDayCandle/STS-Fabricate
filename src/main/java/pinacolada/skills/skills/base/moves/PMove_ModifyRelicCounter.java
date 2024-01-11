package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Relic;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@VisibleSkill
public class PMove_ModifyRelicCounter extends PMove<PField_Relic> implements OutOfCombatMove {
    public static final PSkillData<PField_Relic> DATA = register(PMove_ModifyRelicCounter.class, PField_Relic.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .noTarget();

    public PMove_ModifyRelicCounter() {
        super(DATA);
    }

    public PMove_ModifyRelicCounter(Collection<String> relics) {
        super(DATA);
        fields.relicIDs.addAll(relics);
    }

    public PMove_ModifyRelicCounter(String... relics) {
        super(DATA);
        fields.relicIDs.addAll(Arrays.asList(relics));
    }

    protected void doEffect(PCLUseInfo info) {
        if (fields.isFilterEmpty() && source instanceof AbstractRelic) {
            doModify((AbstractRelic) source, info);
        }
        else {
            int limit = fields.relicIDs.isEmpty() ? extra : AbstractDungeon.player.relics.size();
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (fields.getFullRelicFilter().invoke(r)) {
                    doModify(r, info);
                    limit -= 1;
                }
                if (limit <= 0) {
                    break;
                }
            }
        }
    }

    protected void doModify(AbstractRelic relic, PCLUseInfo info) {
        int actualAmount = refreshAmount(info);
        GameUtilities.modifyRelicCounter(relic, fields.not ? actualAmount : relic.counter + actualAmount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_giveTarget(TEXT.subjects_relic, PGR.core.tooltips.counter.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = fields.relicIDs.isEmpty() ?
                fields.isFilterEmpty() ? TEXT.subjects_thisRelic() : EUIRM.strings.numNoun(getExtraRawString(), fields.getFullRelicString())
                : fields.getFullRelicString();
        if (fields.not) {
            return TEXT.act_setOf(PGR.core.tooltips.counter.title, base, getAmountRawString());
        }
        else if (amount > 0) {
            return TEXT.act_increasePropertyBy(PGR.core.tooltips.counter.title, base, getAmountRawString());
        }
        return TEXT.act_reducePropertyBy(PGR.core.tooltips.counter.title, base, getAmountRawString());
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public PMove_ModifyRelicCounter onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        if (card instanceof KeywordProvider) {
            fields.addRelicTips((KeywordProvider) card);
        }
        return this;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(() -> doEffect(info));
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        super.useOutsideOfBattle(info);

        doEffect(info);
    }
}
