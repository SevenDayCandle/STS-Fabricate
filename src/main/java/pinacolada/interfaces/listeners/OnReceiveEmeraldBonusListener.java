package pinacolada.interfaces.listeners;

public interface OnReceiveEmeraldBonusListener {
    float getEmeraldMaxHPBonus(float bonus);

    int getEmeraldMetallicizeBonus(int bonus);

    int getEmeraldRegenBonus(int bonus);

    int getEmeraldStrengthBonus(int bonus);
}
