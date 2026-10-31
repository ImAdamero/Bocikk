package pl.easybot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_5481;
import pl.easybot.bot.Bot;

public class ErrorsConsoleScreen extends class_437 {
   private static final int LINE_H = 10;
   private static final int PAD = 8;
   private static final int TOP = 22;
   private static final int MAX_ENTRIES = 400;
   private final class_437 parent;
   private int scroll = 0;
   private int lastTotal = -1;
   private long clearedAt = 0L;

   public static void show(class_310 var0, class_437 var1) {
      System.out.println("[EasyBot] klawisz bledow nacisniety - otwieram konsole bledow");

      try {
         var0.method_1507(new ErrorsConsoleScreen(var1));
      } catch (Throwable var3) {
         System.out.println("[EasyBot] nie udalo sie otworzyc konsoli bledow:");
         var3.printStackTrace();
      }
   }

   public static void started() {
      System.out.println("[EasyBot] ErrorsClient zaladowany - konsola bledow pod klawiszem B");
   }

   public ErrorsConsoleScreen(class_437 var1) {
      super(class_2561.method_43470("Bledy botow"));
      this.parent = var1;
   }

   private List<ErrorsConsoleScreen.Entry> collect() {
      ArrayList var1 = new ArrayList();

      for (int var2 = 0; var2 < EasyBotClient.bots.length; var2++) {
         Bot var3 = EasyBotClient.bots[var2];
         if (var3 != null) {
            String var4 = var2 < EasyBotClient.nicks.length ? EasyBotClient.nicks[var2] : null;
            String var5 = var4 != null && !var4.isBlank() ? var4 : var3.name;

            Object[] var6;
            try {
               var6 = new ArrayList<>(var3.lines()).toArray();
            } catch (Throwable var13) {
               continue;
            }

            for (Object var10 : var6) {
               try {
                  Bot.Line var11 = (Bot.Line)var10;
                  if (var11.time() > this.clearedAt && ErrorsClient.isError(var11.text())) {
                     var1.add(new ErrorsConsoleScreen.Entry(var11.time(), var5, var11.text()));
                  }
               } catch (Throwable var12) {
               }
            }
         }
      }

      var1.sort(Comparator.comparingLong(ErrorsConsoleScreen.Entry::time));
      return var1.size() > 400 ? var1.subList(var1.size() - 400, var1.size()) : var1;
   }

   public void method_25394(class_332 var1, int var2, int var3, float var4) {
      var1.method_25294(0, 0, this.field_22789, this.field_22790, -267382768);
      class_327 var5 = this.field_22793;
      List var6 = this.collect();
      var1.method_51433(
         var5, "Bledy wszystkich botow (" + var6.size() + ")   |   kolko/PgUp/PgDn/Home/End - przewijanie, C - wyczysc, ESC - powrot", 8, 7, -43691, true
      );
      var1.method_25294(8, 20, this.field_22789 - 8, 21, 1728053247);
      int var7 = Math.max(40, this.field_22789 - 16);
      ArrayList var8 = new ArrayList();

      for (ErrorsConsoleScreen.Entry var10 : var6) {
         try {
            class_5250 var11 = class_2561.method_43470("[" + var10.nick() + "] ").method_54663(11184810);
            List var12 = var5.method_1728(var11.method_10852(var10.text()), var7);
            if (var12 != null) {
               var8.addAll(var12);
            }
         } catch (Throwable var15) {
         }
      }

      if (var8.isEmpty()) {
         var1.method_51433(var5, "Brak bledow - zaden bot nie zglosil problemu.", 8, 26, -7811960, true);
      } else {
         int var16 = Math.max(1, (this.field_22790 - 22 - 8) / 10);
         int var17 = var8.size();
         if (this.lastTotal >= 0 && this.scroll > 0 && var17 > this.lastTotal) {
            this.scroll = this.scroll + (var17 - this.lastTotal);
         }

         this.lastTotal = var17;
         this.scroll = Math.min(Math.max(this.scroll, 0), Math.max(0, var17 - var16));
         int var18 = var17 - this.scroll;
         int var19 = Math.max(0, var18 - var16);
         byte var13 = 22;

         for (int var14 = var19; var14 < var18; var14++) {
            var1.method_51430(var5, (class_5481)var8.get(var14), 8, var13, -1, true);
            var13 += 10;
         }

         if (this.scroll > 0) {
            var1.method_51433(var5, "^ " + this.scroll + " nizej", this.field_22789 - 8 - 70, 7, -22016, true);
         }
      }
   }

   public boolean method_25401(double var1, double var3, double var5, double var7) {
      this.scroll = Math.max(0, this.scroll + (int)Math.signum(var7) * 3);
      return true;
   }

   public boolean method_25404(int var1, int var2, int var3) {
      switch (var1) {
         case 67:
            this.clearedAt = System.currentTimeMillis();
            this.scroll = 0;
            return true;
         case 256:
            this.method_25419();
            return true;
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
         default:
            return false;
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

   private record Entry(long time, String nick, class_2561 text) {
   }
}
