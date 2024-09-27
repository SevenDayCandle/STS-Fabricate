package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.interfaces.markers.KeywordProvider;
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

@VisibleSkill
public class PMove_UpgradeRelic extends PMove<PField_Relic> implements OutOfCombatMove {
    public static final PSkillData<PField_Relic> DATA = register(PMove_UpgradeRelic.class, PField_Relic.class)
            .setExtra(1, Integer.MAX_VALUE)
            .noTarget();

    public PMove_UpgradeRelic() {
        super(DATA);
    }

    public PMove_UpgradeRelic(Collection<String> relics) {
        super(DATA);
        fields.relicIDs.addAll(relics);
    }

    public PMove_UpgradeRelic(String... relics) {
        super(DATA);
        fields.relicIDs.addAll(Arrays.asList(relics));
    }

    protected void doEffect(PCLUseInfo info) {
        int actualAmount = refreshAmount(info);
        if (fields.isFilterEmpty() && source instanceof AbstractRelic) {
            GameUtilities.upgradeRelic((AbstractRelic) source, actualAmount);
        }
        else {
            int limit = fields.relicIDs.isEmpty() ? extra : AbstractDungeon.player.relics.size();
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (fields.getFullRelicFilter().invoke(r)) {
                    GameUtilities.upgradeRelic(r, actualAmount);
                    limit -= 1;
                }
                if (limit <= 0) {
                    break;
                }
            }
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_upgrade(TEXT.subjects_relic);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = fields.relicIDs.isEmpty() ?
                fields.isFilterEmpty() ? TEXT.subjects_thisRelic() : EUIRM.strings.numNoun(getExtraRawString(requestor), fields.getFullRelicString(requestor))
                : fields.getFullRelicString(requestor);
        return amount > 1 ? TEXT.act_genericTimes(PGR.core.tooltips.upgrade.title, base, getAmountRawString(requestor)) : TEXT.act_upgrade(base);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void onSetupTips(AbstractCard card) {
        if (card instanceof KeywordProvider) {
            fields.addRelicTips((KeywordProvider) card);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(() -> this.doEffect(info));
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        super.useOutsideOfBattle(info);

        doEffect(info);
    }
}
