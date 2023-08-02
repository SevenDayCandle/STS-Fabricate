package pinacolada.cards.base.cardText;

import basemod.BaseMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static pinacolada.cards.base.cardText.PointerToken.DUMMY;

// Copied and modified from STS-AnimatorMod
public class VariableToken extends PCLTextToken {
    public static final char TOKEN = '!';
    protected static Class<?> dynaClass;
    protected final DynamicVariable var;
    protected AbstractCardModifier mod;
    protected Method valueFunc;
    protected ColoredString coloredString;

    protected VariableToken(String text, DynamicVariable var, AbstractCard card) {
        super(PCLTextTokenType.Variable, null);
        this.var = var;
        setDynamics(text, card);
    }

    public static int tryAdd(PCLTextParser parser) {
        if (parser.remaining > 1) {
            builder.setLength(0);
            int i = 1;
            while (true) {
                Character next = parser.nextCharacter(i);
                if (next == null) {
                    break;
                }
                else if (next == '!') {
                    final String key = builder.toString();
                    DynamicVariable var = BaseMod.cardDynamicVariableMap.get(key);
                    if (var != null) {
                        VariableToken token = new VariableToken(key, var, parser.card);
                        parser.addToken(token);
                    }
                    else {
                        EUIUtils.logError(parser.card, "Unknown variable type: !" + key + "!, Raw text is: " + parser.text);
                    }

                    return i + 1;
                }
                else {
                    builder.append(next);
                    i += 1;
                }
            }
        }
        return 0;
    }

    protected Color getColor(AbstractCard card) {
        if (var.isModified(card)) {
            if (var.value(card) >= var.modifiedBaseValue(card)) {
                return var.getIncreasedValueColor();
            }
            else {
                return var.getDecreasedValueColor();
            }
        }
        return var.getNormalColor();
    }

    protected int getVal(AbstractCard card) {
        if (valueFunc != null) {
            try {
                return (int) valueFunc.invoke(mod, card);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return var.value(card);
    }

    @Override
    protected float getWidth(BitmapFont font, String text) {
        if (text == null) {
            return super.getWidth(font, DUMMY); //20f * Settings.scale * font.getScaleX(); // AbstractCard.MAGIC_NUM_W
        }
        else {
            return super.getWidth(font, text);
        }
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context) {
        if (coloredString == null) {
            coloredString = new ColoredString(getVal(context.card), getColor(context.card));
        }
        else if (EUI.elapsed25()) {
            coloredString.text = String.valueOf(getVal(context.card));
            coloredString.color = getColor(context.card);
        }

        super.render(sb, context, coloredString);
    }

    protected void setDynamics(String key, AbstractCard card) {
        if (dynaClass == null) {
            if (Loader.isModLoaded("CardAugments")) {
                try {
                    dynaClass = Class.forName("CardAugments.dynvars.DynamicDynamicVariableManager");
                }
                catch (Exception ignored) {
                    dynaClass = this.getClass();
                }
            }
            else {
                dynaClass = this.getClass();
            }
        }
        try {
            if (dynaClass.isInstance(var)) {
                Class<?> dynvarCarrierClass = Class.forName("CardAugments.cardmods.DynvarCarrier");
                ArrayList<AbstractCardModifier> mods = CardModifierManager.getModifiers(card, key);
                if (mods.size() > 0) {
                    mod = mods.get(0);
                    if (dynvarCarrierClass.isInstance(mod)) {
                        valueFunc = dynvarCarrierClass.getMethod("val", AbstractCard.class);
                    }
                }
            }
        }
        catch (Exception ignored) {
        }
    }
}
