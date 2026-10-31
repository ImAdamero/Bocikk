package pl.easybot.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5481;
import pl.easybot.EasyBotClient;
import pl.easybot.bot.Bot;

public class BotConsoleScreen extends class_437 {
   private static final int LINE_H = 10;
   private static final int PAD = 8;
   private static final int TOP = 22;
   private final class_437 parent;
   private final int idx;
   private int scroll = 0;
   private int lastTotal = -1;

   public BotConsoleScreen(class_437 var1, int var2) {
      super(class_2561.method_43470("Konsola bota " + (var2 + 1)));
      this.parent = var1;
      this.idx = var2;
   }

   private Bot bot() {
      return this.idx >= 0 && this.idx < EasyBotClient.bots.length ? EasyBotClient.bots[this.idx] : null;
   }

   private String nick() {
      String var1 = this.idx >= 0 && this.idx < EasyBotClient.nicks.length ? EasyBotClient.nicks[this.idx] : null;
      return var1 != null && !var1.isBlank() ? var1 : "Bot " + (this.idx + 1);
   }

   public void method_25394(class_332 var1, int var2, int var3, float var4) {
      var1.method_25294(0, 0, this.field_22789, this.field_22790, -267382768);
      class_327 var5 = this.field_22793;
      Bot var6 = this.bot();
      var1.method_51433(
         var5,
         "Konsola bota: "
            + this.nick()
            + (var6 == null ? "  [jeszcze nie wystartowal]" : (var6.active() ? "" : "  [offline]"))
            + "   |   kolko/PgUp/PgDn/Home/End - przewijanie, ESC - powrot",
         8,
         7,
         -1,
         true
      );
      var1.method_25294(8, 20, this.field_22789 - 8, 21, 1728053247);
      int var7 = Math.max(40, this.field_22789 - 16);
      ArrayList var8 = new ArrayList();

      Object[] var9;
      try {
         var9 = var6 == null ? new Object[0] : new ArrayList<>(var6.lines()).toArray();
      } catch (Throwable var18) {
         var9 = new Object[0];
      }

      for (Object var13 : var9) {
         try {
            Bot.Line var14 = (Bot.Line)var13;
            List var15 = var5.method_1728(var14.text(), var7);
            if (var15 != null && !var15.isEmpty()) {
               var8.addAll(var15);
            }
         } catch (Throwable var17) {
         }
      }

      int var19 = Math.max(1, (this.field_22790 - 22 - 8) / 10);
      int var20 = var8.size();
      if (this.lastTotal >= 0 && this.scroll > 0 && var20 > this.lastTotal) {
         this.scroll = this.scroll + (var20 - this.lastTotal);
      }

      this.lastTotal = var20;
      int var21 = Math.max(0, var20 - var19);
      this.scroll = Math.min(Math.max(this.scroll, 0), var21);
      int var22 = var20 - this.scroll;
      int var23 = Math.max(0, var22 - var19);
      byte var24 = 22;

      for (int var16 = var23; var16 < var22; var16++) {
         var1.method_51430(var5, (class_5481)var8.get(var16), 8, var24, -1, true);
         var24 += 10;
      }

      if (this.scroll > 0) {
         var1.method_51433(var5, "^ " + this.scroll + " nizej", this.field_22789 - 8 - 70, 7, -22016, true);
      }
   }

   public boolean method_25401(double var1, double var3, double var5, double var7) {
      this.scroll = this.scroll + (int)Math.signum(var7) * 3;
      if (this.scroll < 0) {
         this.scroll = 0;
      }

      return true;
   }

   public boolean method_25404(int var1, int var2, int var3) {
      switch (var1) {
         case 256:
            this.method_25419();
            return true;
         case 257:
         case 258:
         case 259:
         case 260:
         case 261:
         case 262:
         case 263:
         case 264:
         case 265:
         default:
            return false;
         case 266:
            this.scroll += 10;
            return true;
         case 267:
            this.scroll = Math.max(0, this.scroll - 10);
            return true;
         case 268:
            this.scroll = 1073741823;
            return true;
         case 269:
            this.scroll = 0;
            return true;
      }
   }

   public void method_25419() {
      class_310 var1 = this.field_22787;
      if (var1 != null) {
         var1.method_1507(this.parent);
      }
   }

   public boolean method_25421() {
      return false;
   }
}
