package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.fields.PField_CardTransform;

@VisibleSkill
public class PMove_Transform extends PMove_DoCard<PField_CardTransform> {
    public static final PSkillData<PField_CardTransform> DATA = register(PMove_Transform.class, PField_CardTransform.class)
            .setExtra2(0, DEFAULT_MAX)
            .noTarget();

    public PMove_Transform() {
        this(1);
    }

    public PMove_Transform(int amount, PCLCardGroupHelper... groupHelpers) {
        super(DATA, amount, groupHelpers);
    }

    public PMove_Transform(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_Transform(String cards) {
        super(DATA, 1);
        fields.setResult(cards);
    }

    public PMove_Transform(String cards, int amount, PCLCardGroupHelper... groupHelpers) {
        this(amount, groupHelpers);
        fields.setResult(cards);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return SelectFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.transform;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_transform(TEXT.subjects_x, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.act_transform(
                useParent ? getInheritedThemString() : fields.groupTypes.size() > 0 ? EUIRM.strings.numNounPlace(getAmountRawString(), fields.getFullCardString(), TEXT.subjects_from(fields.getGroupString())) : TEXT.subjects_thisCard(), fields.getCardIDString()
        );
    }

    private void transformImpl(AbstractCard c) {
        AbstractCard c2 = PField_CardCategory.getCard(fields.result);
        if (c2 != null) {
            AbstractCard cardCopy = c2.makeCopy();
            for (int i = 0; i < extra2; i++) {
                cardCopy.upgrade();
            }
            PCLActions.last.replaceCard(c.uuid, cardCopy);
            PCLEffects.Queue.showCardBriefly(c2.makeStatEquivalentCopy());
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (!fields.hasGroups() && !useParent && sourceCard != null) {
            transformImpl(sourceCard);
        }
        else {
            // Extra is used for other purposes
            fields.getGenericPileAction(getAction(), info, order, -1)
                    .addCallback(cards -> {
                        for (AbstractCard c : cards) {
                            transformImpl(c);
                        }
                    });
        }
        super.use(info, order);
    }
}
