package pl.easybot.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1922;
import net.minecraft.class_1923;
import net.minecraft.class_1959;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_238;
import net.minecraft.class_2478;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_2540;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2826;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_5455;
import net.minecraft.class_6903;
import net.minecraft.class_7924;
import net.minecraft.class_8242;
import net.minecraft.class_2338.class_2339;
import org.jetbrains.annotations.Nullable;

class World implements class_1922 {
   private final Map<Long, class_2826[]> chunks = new HashMap<>();
   private final Map<Long, Long> arrived = new HashMap<>();
   private final Map<class_2338, World.Sign> signs = new HashMap<>();
   private final class_5455 registries;
   private final class_2378<class_1959> biomes;
   final int bottom;
   final int height;

   World(class_5455 registries, int bottom, int height) {
      this.registries = registries;
      this.biomes = registries.method_30530(class_7924.field_41236);
      this.bottom = bottom;
      this.height = height;
   }

   void load(int cx, int cz, class_2540 data, long tick) {
      class_2826[] sections = new class_2826[this.height >> 4];

      for (int i = 0; i < sections.length; i++) {
         sections[i] = new class_2826(this.biomes);
         sections[i].method_12258(data);
      }

      long key = class_1923.method_8331(cx, cz);
      this.chunks.put(key, sections);
      this.arrived.put(key, tick);
      this.signs.keySet().removeIf(p -> p.method_10263() >> 4 == cx && p.method_10260() >> 4 == cz);
   }

   void unload(int cx, int cz) {
      long key = class_1923.method_8331(cx, cz);
      this.chunks.remove(key);
      this.arrived.remove(key);
      this.signs.keySet().removeIf(p -> p.method_10263() >> 4 == cx && p.method_10260() >> 4 == cz);
   }

   boolean loaded(int bx, int bz) {
      return this.chunks.containsKey(class_1923.method_8331(bx >> 4, bz >> 4));
   }

   long arrivedAt(int bx, int bz) {
      return this.arrived.getOrDefault(class_1923.method_8331(bx >> 4, bz >> 4), Long.MAX_VALUE);
   }

   void set(class_2338 pos, class_2680 state) {
      class_2826[] sections = this.chunks.get(class_1923.method_8331(pos.method_10263() >> 4, pos.method_10260() >> 4));
      int i = pos.method_10264() - this.bottom >> 4;
      if (sections != null && i >= 0 && i < sections.length) {
         sections[i].method_12256(pos.method_10263() & 15, pos.method_10264() & 15, pos.method_10260() & 15, state, false);
         if (!(state.method_26204() instanceof class_2478)) {
            this.signs.remove(pos);
         }
      }
   }

   void blockEntity(class_2338 pos, class_2591<?> type, @Nullable class_2487 nbt) {
      if (type == class_2591.field_11911 || type == class_2591.field_40330) {
         if (nbt != null && !nbt.method_33133()) {
            class_6903<class_2520> ops = class_6903.method_46632(class_2509.field_11560, this.registries);
            class_8242 front = nbt.method_10545("front_text")
               ? class_8242.field_43298.parse(ops, nbt.method_10580("front_text")).result().orElse(new class_8242())
               : new class_8242();
            class_8242 back = nbt.method_10545("back_text")
               ? class_8242.field_43298.parse(ops, nbt.method_10580("back_text")).result().orElse(new class_8242())
               : new class_8242();
            this.signs.put(pos.method_10062(), new World.Sign(front, back));
         }
      }
   }

   @Nullable
   World.Sign sign(class_2338 pos) {
      return this.signs.get(pos);
   }

   public class_2680 method_8320(class_2338 pos) {
      class_2826[] sections = this.chunks.get(class_1923.method_8331(pos.method_10263() >> 4, pos.method_10260() >> 4));
      int i = pos.method_10264() - this.bottom >> 4;
      return sections != null && i >= 0 && i < sections.length
         ? sections[i].method_12254(pos.method_10263() & 15, pos.method_10264() & 15, pos.method_10260() & 15)
         : class_2246.field_10124.method_9564();
   }

   public class_3610 method_8316(class_2338 pos) {
      return this.method_8320(pos).method_26227();
   }

   @Nullable
   public class_2586 method_8321(class_2338 pos) {
      return null;
   }

   public int method_31605() {
      return this.height;
   }

   public int method_31607() {
      return this.bottom;
   }

   List<class_265> collisions(class_238 box) {
      List<class_265> list = new ArrayList<>();
      int x0 = class_3532.method_15357(box.field_1323 - 1.0E-7) - 1;
      int x1 = class_3532.method_15357(box.field_1320 + 1.0E-7) + 1;
      int y0 = class_3532.method_15357(box.field_1322 - 1.0E-7) - 1;
      int y1 = class_3532.method_15357(box.field_1325 + 1.0E-7) + 1;
      int z0 = class_3532.method_15357(box.field_1321 - 1.0E-7) - 1;
      int z1 = class_3532.method_15357(box.field_1324 + 1.0E-7) + 1;
      class_2339 pos = new class_2339();

      for (int x = x0; x <= x1; x++) {
         for (int z = z0; z <= z1; z++) {
            for (int y = y0; y <= y1; y++) {
               int edges = (x != x0 && x != x1 ? 0 : 1) + (y != y0 && y != y1 ? 0 : 1) + (z != z0 && z != z1 ? 0 : 1);
               if (edges != 3) {
                  class_2680 state = this.method_8320(pos.method_10103(x, y, z));
                  if (!state.method_26215() && (edges != 1 || state.method_26209()) && (edges != 2 || state.method_27852(class_2246.field_10008))) {
                     class_265 shape = state.method_26220(this, pos);
                     if (!shape.method_1110()) {
                        class_265 moved = shape.method_1096(x, y, z);
                        if (moved.method_1107().method_994(box)) {
                           list.add(moved);
                        }
                     }
                  }
               }
            }
         }
      }

      return list;
   }

   static class_2680 state(int id) {
      class_2680 s = (class_2680)class_2248.field_10651.method_10200(id);
      return s == null ? class_2246.field_10124.method_9564() : s;
   }

   record Sign(class_8242 front, class_8242 back) {
   }
}
