package pinacolada.interfaces.markers;

import basemod.interfaces.CloneablePowerInterface;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.hitboxes.EUIHitbox;

public interface TooltipPower extends CloneablePowerInterface, TooltipProvider
{
    EUIHitbox hitbox();
}
