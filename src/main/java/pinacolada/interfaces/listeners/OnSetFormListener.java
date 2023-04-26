package pinacolada.interfaces.listeners;

import pinacolada.cards.base.PCLCard;

public interface OnSetFormListener {
    void onSetForm(PCLCard card, int oldForm, int newForm);
}
