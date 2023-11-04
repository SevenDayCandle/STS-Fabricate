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
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Relic;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@VisibleSkill
public class PMove_RemoveRelic extends PMove<PField_Relic> implements OutOfCombatMove {
    public static final PSkillData<PField_Relic> DATA = register(PMove_RemoveRelic.class, PField_Relic.class, 1, Integer.MAX_VALUE)
            .noTarget();

    public PMove_RemoveRelic() {
        super(DATA);
    }

    public PMove_RemoveRelic(Collection<String> relics) {
        super(DATA);
        fields.relicIDs.addAll(relics);
    }

    public PMove_RemoveRelic(String... relics) {
        super(DATA);
        fields.relicIDs.addAll(Arrays.asList(relics));
    }

    protected void doEffect() {
        if (fields.isFilterEmpty() && source instanceof AbstractRelic) {
            GameUtilities.removeRelics((AbstractRelic) source);
        }
        else {
            int limit = fields.relicIDs.isEmpty() ? amount : AbstractDungeon.player.relics.size();
            ArrayList<AbstractRelic> toRemove = new ArrayList<>();
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (fields.getFullRelicFilter().invoke(r)) {
                    toRemove.add(r);
                    limit -= 1;
                }
                if (limit <= 0) {
                    break;
                }
            }
            GameUtilities.removeRelics(toRemove);
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_remove(TEXT.subjects_relic);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = fields.relicIDs.isEmpty() ?
                fields.isFilterEmpty() ? TEXT.subjects_thisRelic() : EUIRM.strings.numNoun(getAmountRawString(), fields.getFullRelicString())
                : fields.getFullRelicString();
        return TEXT.act_remove(base);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public PMove_RemoveRelic onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        if (card instanceof KeywordProvider) {
            List<EUIKeywordTooltip> tips = ((KeywordProvider) card).getTips();
            if (tips != null) {
                for (String r : fields.relicIDs) {
                    AbstractRelic relic = RelicLibrary.getRelic(r);
                    if (relic != null) {
                        tips.add(new EUIKeywordTooltip(relic.name, relic.description));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(this::doEffect);
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        PCLEffects.Queue.callback(this::doEffect); // Use callback to avoid concurrent modification if called from relic
    }
}
