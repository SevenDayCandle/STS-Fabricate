package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.relics.PCLRelic;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Potion;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@VisibleSkill
public class PMove_ObtainPotion extends PMove<PField_Potion> implements OutOfCombatMove {
    public static final PSkillData<PField_Potion> DATA = register(PMove_ObtainPotion.class, PField_Potion.class)
            .selfTarget();

    public PMove_ObtainPotion() {
        super(DATA);
    }

    public PMove_ObtainPotion(Collection<String> potions) {
        super(DATA);
        fields.potionIDs.addAll(potions);
    }

    public PMove_ObtainPotion(String... potions) {
        super(DATA);
        fields.potionIDs.addAll(Arrays.asList(potions));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_obtain(TEXT.subjects_potion);
    }

    @Override
    public String getSubText() {
        return fields.potionIDs.isEmpty() ? TEXT.act_obtainAmount(getAmountRawString(), fields.getFullPotionString()) : TEXT.act_obtain(fields.getFullPotionString());
    }

    @Override
    public PMove_ObtainPotion onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
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
        return this;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        createRelic(order::obtainPotion);
        super.use(info, order);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        createRelic((p) -> AbstractDungeon.player.obtainPotion(p));
    }

    protected void createRelic(ActionT1<AbstractPotion> onCreate) {
        if (!fields.potionIDs.isEmpty()) {
            for (String r : fields.potionIDs) {
                AbstractPotion potion = PotionHelper.getPotion(r);
                if (potion != null) {
                    onCreate.invoke(potion.makeCopy());
                }
            }
        }
        else {
            RandomizedList<AbstractPotion> choices = new RandomizedList<>();
            HashSet<AbstractPotion> uniques = new HashSet<>();
            if (!fields.colors.isEmpty()) {
                if (fields.rarities.isEmpty()) {
                    for (AbstractCard.CardColor color : fields.colors) {
                        uniques.addAll(GameUtilities.getPotions(color));
                    }
                }
                else {
                    for (AbstractCard.CardColor color : fields.colors) {
                        for (AbstractPotion potion : GameUtilities.getPotions(color)) {
                            if (EUIUtils.any(fields.rarities, r -> r == potion.rarity)) {
                                uniques.add(potion);
                            }
                        }
                    }
                }
            }
            else if (!fields.rarities.isEmpty()) {
                for (AbstractPotion.PotionRarity rarity : fields.rarities) {
                    for (AbstractPotion potion : GameUtilities.getPotions(null)) {
                        if (EUIUtils.any(fields.rarities, r -> r == potion.rarity)) {
                            uniques.add(potion);
                        }
                    }
                }
            }

            choices.addAll(uniques);
            for (int i = 0; i < amount; i++) {
                AbstractPotion potion = choices.retrieve(PCLRelic.rng, true);
                if (potion != null) {
                    onCreate.invoke(potion.makeCopy());
                }
            }
        }
    }
}
