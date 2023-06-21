package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUICardPreview;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.RotatingList;
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.fields.PField_CardTransform;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_Transform extends PMove_Select<PField_CardTransform> {
    public static final PSkillData<PField_CardTransform> DATA = register(PMove_Transform.class, PField_CardTransform.class)
            .selfTarget();

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
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction() {
        return SelectFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.transform;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_transform(TEXT.subjects_x, TEXT.subjects_x);
    }

    @Override
    public String getSubText() {
        return TEXT.act_transform(
                useParent ? getInheritedThemString() : fields.groupTypes.size() > 0 ? EUIRM.strings.numNounPlace(getAmountRawString(), fields.getFullCardString(), TEXT.subjects_from(fields.getGroupString())) : TEXT.subjects_thisCard, fields.getCardIDString()
        );
    }

    @Override
    public PMove_Transform makePreviews(RotatingList<EUICardPreview> previews) {
        fields.makePreviews(previews);
        super.makePreviews(previews);
        return this;
    }

    private void transformImpl(AbstractCard c) {
        AbstractCard c2 = PField_CardCategory.getCard(fields.result);
        if (c2 != null) {
            PCLActions.last.replaceCard(c.uuid, c2.makeCopy());
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
