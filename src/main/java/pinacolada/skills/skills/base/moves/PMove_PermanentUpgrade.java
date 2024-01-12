package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.actions.piles.UpgradeFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsToUpgradeEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

@VisibleSkill
public class PMove_PermanentUpgrade extends PMove_DoCard<PField_CardCategory> implements OutOfCombatMove {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_PermanentUpgrade.class, PField_CardCategory.class)
            .setGroups(PCLCardGroupHelper.getAll())
            .noTarget();

    public PMove_PermanentUpgrade() {
        this(1);
    }

    public PMove_PermanentUpgrade(int amount, PCLCardGroupHelper... h) {
        super(DATA, amount, h);
    }

    public PMove_PermanentUpgrade(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return (s, c, i, o, g) -> (SelectFromPile) new UpgradeFromPile(s, c, i, o, g).isPermanent(true).isCancellable(false);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.upgrade;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.subjects_permanentlyX(TEXT.act_upgrade(TEXT.subjects_x));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.subjects_permanentlyX(super.getSubText(perspective, requestor));
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public boolean shouldHideGroupNames() {
        return false;
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        List<? extends AbstractCard> list = info.getDataAsList(AbstractCard.class);
        if (list != null) {
            PCLEffects.Queue.callback(() -> {
                        for (AbstractCard c : list) {
                            ChooseCardsToUpgradeEffect.permanentUpgrade(c);
                        }
                    })
                    .addCallback(() -> {
                        super.useOutsideOfBattle(info);
                    });
        }
        else {
            PCLEffects.Queue.add(new ChooseCardsToUpgradeEffect(refreshAmount(info), fields.getFullCardFilter()))
                    .addCallback(effect -> {
                        info.setData(effect.cards);
                        super.useOutsideOfBattle(info);
                    });
        }
    }
}
