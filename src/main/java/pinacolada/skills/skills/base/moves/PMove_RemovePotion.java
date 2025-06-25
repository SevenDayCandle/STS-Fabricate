package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
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
import pinacolada.skills.fields.PField_Potion;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.*;

@VisibleSkill
public class PMove_RemovePotion extends PMove<PField_Potion> implements OutOfCombatMove {
    public static final PSkillData<PField_Potion> DATA = register(PMove_RemovePotion.class, PField_Potion.class)
            .noTarget();

    public PMove_RemovePotion() {
        super(DATA);
    }

    public PMove_RemovePotion(Collection<String> potions) {
        super(DATA);
        fields.potionIDs.addAll(potions);
    }

    public PMove_RemovePotion(String... potions) {
        super(DATA);
        fields.potionIDs.addAll(Arrays.asList(potions));
    }

    protected void doEffect(PCLUseInfo info) {
        if (fields.isFilterEmpty() && source instanceof AbstractPotion) {
            GameUtilities.removePotions((AbstractPotion) source);
        }
        else {
            int limit = fields.potionIDs.isEmpty() ? refreshAmount(info) : AbstractDungeon.player.potions.size();
            ArrayList<AbstractPotion> toRemove = new ArrayList<>();
            for (AbstractPotion r : AbstractDungeon.player.potions) {
                if (fields.getFullPotionFilter().invoke(r)) {
                    toRemove.add(r);
                    limit -= 1;
                }
                if (limit <= 0) {
                    break;
                }
            }
            GameUtilities.removePotions(toRemove);
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_remove(TEXT.subjects_potion);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = fields.potionIDs.isEmpty() ?
                fields.isFilterEmpty() ? TEXT.subjects_thisRelic() : EUIRM.strings.numNoun(getAmountRawString(requestor), fields.getFullPotionString(requestor))
                : fields.getFullPotionString(requestor);
        return TEXT.act_remove(base);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void onSetupTips(AbstractCard card) {
        if (card instanceof KeywordProvider) {
            List<EUIKeywordTooltip> tips = ((KeywordProvider) card).getTips();
            if (tips != null) {
                for (String r : fields.potionIDs) {
                    AbstractPotion potion = PotionHelper.getPotion(r);
                    if (potion != null) {
                        tips.add(new EUIKeywordTooltip(potion.name, potion.description));
                    }
                }
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(() -> this.doEffect(info)).addCallback(() -> {
            super.use(info, order);
        });
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        // Use callback to avoid concurrent modification
        PCLEffects.Queue.callback(() -> this.doEffect(info))
                .addCallback(() -> {
                    super.useOutsideOfBattle(info);
                });
    }
}
