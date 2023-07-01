package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.player.UpgradeRelicEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Relic;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@VisibleSkill
public class PMove_UpgradeRelic extends PMove<PField_Relic> implements OutOfCombatMove {
    public static final PSkillData<PField_Relic> DATA = register(PMove_UpgradeRelic.class, PField_Relic.class)
            .selfTarget();

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

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_upgrade(TEXT.subjects_relic);
    }

    @Override
    public String getSubText() {
        String base = fields.relicIDs.isEmpty() ? EUIRM.strings.numNoun(getExtraRawString(), fields.getFullRelicString()) : fields.getFullRelicString();
        return amount > 1 ? TEXT.act_genericTimes(PGR.core.tooltips.upgrade.title, base, getAmountRawString()) : TEXT.act_upgrade(base);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public PMove_UpgradeRelic onAddToCard(AbstractCard card) {
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
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();

        doEffect();
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(this::doEffect);
        super.use(info, order);
    }

    protected void doEffect() {
        int limit = fields.relicIDs.isEmpty() ? extra : AbstractDungeon.player.relics.size();
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (fields.getFullRelicFilter().invoke(r)) {
                PCLEffects.Queue.add(new UpgradeRelicEffect(r, amount));
                limit -= 1;
            }
            if (limit <= 0) {
                break;
            }
        }
    }
}
