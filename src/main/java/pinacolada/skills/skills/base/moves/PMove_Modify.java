package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.List;

public abstract class PMove_Modify<T extends PField_CardCategory> extends PMove<T> {
    public PMove_Modify(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMove_Modify(PSkillData<T> data, int amount, int extraAmount, PCLCardGroupHelper... groups) {
        super(data, PCLCardTarget.None, amount, extraAmount);
        fields.setCardGroup(groups);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_giveTarget(TEXT.subjects_card, getObjectSampleText());
    }

    public String getObjectSampleText() {
        return getObjectText();
    }

    public abstract String getObjectText();

    @Override
    public String getSubText() {
        String giveString = getObjectText();
        return useParent ? TEXT.act_giveTarget(getInheritedString(), giveString) :
                fields.hasGroups() ?
                        TEXT.act_giveFrom(EUIRM.strings.numNoun(baseExtra <= 0 ? TEXT.subjects_all : getExtraRawString(), fields.getFullCardString()), fields.getGroupString(), giveString) :
                        TEXT.act_giveTarget(TEXT.subjects_this, giveString);
    }

    @Override
    public void setupEditor(PCLCustomCardEffectEditor<?> editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
    }

    @Override
    public void use(PCLUseInfo info) {
        boolean selectAll = baseExtra <= 0 || useParent;
        getActions().selectFromPile(getName(), selectAll ? Integer.MAX_VALUE : extra, fields.getCardGroup(info))
                .setFilter(this::canCardPass)
                .setOptions((selectAll || fields.groupTypes.isEmpty() ? PCLCardSelection.Random : fields.origin).toSelection(), !fields.forced)
                .addCallback(this::cardAction);
        super.use(info);
    }

    @Override
    public String wrapAmount(int input) {
        return input > 0 ? "+" + input : String.valueOf(input);
    }

    public String wrapExtra(int input) {
        return String.valueOf(input);
    }

    public boolean canCardPass(AbstractCard c) {
        return fields.getFullCardFilter().invoke(c);
    }

    public void cardAction(List<AbstractCard> cards) {
        for (AbstractCard c : cards) {
            getAction().invoke(c);
        }
    }

    public abstract ActionT1<AbstractCard> getAction();
}
