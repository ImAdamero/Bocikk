package pl.easybot.gui;

import java.util.function.BooleanSupplier;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_6382;

public class PillButton extends class_339 {
   private final int color;
   private final BooleanSupplier enabled;
   private final Runnable action;
   private final boolean filled;

   public PillButton(int x, int y, int w, int h, String label, int color, BooleanSupplier enabled, Runnable action) {
      this(x, y, w, h, label, color, true, enabled, action);
   }

   public PillButton(int x, int y, int w, int h, String label, int color, boolean filled, BooleanSupplier enabled, Runnable action) {
      super(x, y, w, h, class_2561.method_43470(label));
      this.color = color;
      this.filled = filled;
      this.enabled = enabled;
      this.action = action;
   }

   protected void method_48579(class_332 c, int mouseX, int mouseY, float delta) {
      boolean on = this.enabled.getAsBoolean();
      this.field_22763 = on;
      boolean hover = on && this.method_49606();
      String label = this.method_25369().getString();
      if (on) {
         Draw.button(
            c,
            class_310.method_1551().field_1772,
            this.method_46426(),
            this.method_46427(),
            this.field_22758,
            this.field_22759,
            label,
            this.color,
            this.filled,
            hover
         );
      } else {
         Draw.box(c, this.method_46426(), this.method_46427(), this.field_22758, this.field_22759, Theme.PANEL_SOLID, Theme.OUTLINE);
         int tw = class_310.method_1551().field_1772.method_1727(label);
         c.method_51433(
            class_310.method_1551().field_1772,
            label,
            this.method_46426() + (this.field_22758 - tw) / 2,
            this.method_46427() + (this.field_22759 - 8) / 2,
            Theme.TEXT_FAINT,
            false
         );
      }
   }

   public void method_25348(double mouseX, double mouseY) {
      this.action.run();
   }

   protected void method_47399(class_6382 builder) {
      this.method_37021(builder);
   }
}
