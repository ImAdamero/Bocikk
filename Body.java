package pl.easybot.bot;

import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import java.util.List;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2399;
import net.minecraft.class_243;
import net.minecraft.class_2533;
import net.minecraft.class_2541;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3612;
import net.minecraft.class_3736;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_2350.class_2351;

class Body {
   double x;
   double y;
   double z;
   double vx;
   double vy;
   double vz;
   float yaw;
   float pitch;
   boolean onGround;
   boolean horizontalCollision;
   boolean flying;
   float flySpeed = 0.05F;
   float forward;
   float sideways;
   boolean jumping;
   boolean sprinting;
   int food = 20;
   double speed = 0.1;
   double jumpStrength = 0.42;
   double stepHeight = 0.6;
   double gravity = 0.08;
   private int jumpCooldown;
   private double waterHeight;
   private double lavaHeight;
   private boolean inWater;
   private boolean inLava;

   class_238 box() {
      return new class_238(this.x - 0.3, this.y, this.z - 0.3, this.x + 0.3, this.y + 1.8, this.z + 0.3);
   }

   void push(double ox, double oz) {
      double d = this.x - ox;
      double e = this.z - oz;
      double f = class_3532.method_15391(d, e);
      if (!(f < 0.01F)) {
         f = Math.sqrt(f);
         d /= f;
         e /= f;
         double g = Math.min(1.0, 1.0 / f);
         this.vx += d * g * 0.05F;
         this.vz += e * g * 0.05F;
      }
   }

   boolean climbing(World world) {
      class_2338 pos = class_2338.method_49637(this.x, this.y, this.z);
      class_2680 s = world.method_8320(pos);
      if (s.method_26164(class_3481.field_22414)
         || s.method_26204() instanceof class_2399
         || s.method_26204() instanceof class_2541
         || s.method_26204() instanceof class_3736) {
         return true;
      } else if (s.method_26204() instanceof class_2533 && (Boolean)s.method_11654(class_2533.field_11631)) {
         class_2680 below = world.method_8320(pos.method_10074());
         return below.method_27852(class_2246.field_9983) && below.method_11654(class_2399.field_11253) == s.method_11654(class_2533.field_11177);
      } else {
         return false;
      }
   }

   void tick(World world) {
      if (this.jumpCooldown > 0) {
         this.jumpCooldown--;
      }

      if (Math.abs(this.vx) < 0.003) {
         this.vx = 0.0;
      }

      if (Math.abs(this.vy) < 0.003) {
         this.vy = 0.0;
      }

      if (Math.abs(this.vz) < 0.003) {
         this.vz = 0.0;
      }

      this.fluids(world);
      if (this.jumping && !this.flying) {
         double g = this.inLava ? this.lavaHeight : this.waterHeight;
         boolean swim = this.inWater && g > 0.0;
         double h = 0.4;
         if (!swim || this.onGround && !(g > h)) {
            if (!this.inLava || this.onGround && !(g > h)) {
               if ((this.onGround || swim && g <= h) && this.jumpCooldown == 0) {
                  this.jump(world);
                  this.jumpCooldown = 10;
               }
            } else {
               this.vy += 0.04F;
            }
         } else {
            this.vy += 0.04F;
         }
      } else {
         this.jumpCooldown = 0;
      }

      if (this.flying && this.jumping) {
         this.vy = this.vy + this.flySpeed * 3.0F;
      }

      class_243 input = new class_243(this.sideways * 0.98F, 0.0, this.forward * 0.98F);
      double startVy = this.vy;
      if ((this.inWater || this.inLava) && !this.flying) {
         this.travelFluid(world, input);
      } else {
         this.travelAir(world, input);
      }

      if (this.flying) {
         this.vy = startVy * 0.6;
      }
   }

   private void jump(World world) {
      float f1 = world.method_8320(class_2338.method_49637(this.x, this.y, this.z)).method_26204().method_23350();
      float f2 = world.method_8320(this.below()).method_26204().method_23350();
      double v = this.jumpStrength * (f1 == 1.0F ? f2 : f1);
      if (!(v <= 1.0E-5)) {
         this.vy = Math.max(v, this.vy);
         if (this.sprinting) {
            float g = this.yaw * (float) (Math.PI / 180.0);
            this.vx = this.vx + -class_3532.method_15374(g) * 0.2;
            this.vz = this.vz + class_3532.method_15362(g) * 0.2;
         }
      }
   }

   private class_2338 below() {
      return class_2338.method_49637(this.x, this.y - 0.5000001, this.z);
   }

   private void travelAir(World world, class_243 input) {
      float slip = this.onGround ? world.method_8320(this.below()).method_26204().method_9499() : 1.0F;
      float friction = slip * 0.91F;
      float moveSpeed = this.onGround
         ? (float)(this.sprinting ? this.speed * 1.3 : this.speed) * (0.21600002F / (slip * slip * slip))
         : (this.flying ? (this.sprinting ? this.flySpeed * 2.0F : this.flySpeed) : (this.sprinting ? 0.025999999F : 0.02F));
      this.accelerate(moveSpeed, input);
      boolean climb = this.climbing(world);
      if (climb) {
         this.vx = class_3532.method_15350(this.vx, -0.15F, 0.15F);
         this.vz = class_3532.method_15350(this.vz, -0.15F, 0.15F);
         this.vy = Math.max(this.vy, -0.15F);
      }

      this.move(world);
      if ((this.horizontalCollision || this.jumping) && this.climbing(world)) {
         this.vy = 0.2;
      }

      double d = this.vy;
      if (world.loaded(class_3532.method_15357(this.x), class_3532.method_15357(this.z))) {
         d -= this.gravity;
      } else {
         d = this.y > world.bottom ? -0.1 : 0.0;
      }

      this.vx *= friction;
      this.vy = d * 0.98F;
      this.vz *= friction;
   }

   private void travelFluid(World world, class_243 input) {
      boolean falling = this.vy <= 0.0;
      double startY = this.y;
      if (this.inWater) {
         this.accelerate(0.02F, input);
         this.move(world);
         if (this.horizontalCollision && this.climbing(world)) {
            this.vy = 0.2;
         }

         this.vx *= 0.8F;
         this.vy *= 0.8F;
         this.vz *= 0.8F;
         this.vy = this.fluidFall(this.vy, falling);
      } else {
         this.accelerate(0.02F, input);
         this.move(world);
         if (this.lavaHeight <= 0.4) {
            this.vx *= 0.5;
            this.vy *= 0.8F;
            this.vz *= 0.5;
            this.vy = this.fluidFall(this.vy, falling);
         } else {
            this.vx *= 0.5;
            this.vy *= 0.5;
            this.vz *= 0.5;
         }

         this.vy = this.vy - this.gravity / 4.0;
      }

      if (this.horizontalCollision) {
         class_238 moved = this.box().method_989(this.vx, this.vy + 0.6F - this.y + startY, this.vz);
         if (world.collisions(moved).isEmpty() && !fluidIn(world, moved)) {
            this.vy = 0.3F;
         }
      }
   }

   private double fluidFall(double v, boolean falling) {
      if (this.gravity == 0.0) {
         return v;
      } else {
         return falling && Math.abs(v - 0.005) >= 0.003 && Math.abs(v - this.gravity / 16.0) < 0.003 ? -0.003 : v - this.gravity / 16.0;
      }
   }

   private void accelerate(float moveSpeed, class_243 input) {
      double len = input.method_1027();
      if (!(len < 1.0E-7)) {
         class_243 v = (len > 1.0 ? input.method_1029() : input).method_1021(moveSpeed);
         float sin = class_3532.method_15374(this.yaw * (float) (Math.PI / 180.0));
         float cos = class_3532.method_15362(this.yaw * (float) (Math.PI / 180.0));
         this.vx = this.vx + (v.field_1352 * cos - v.field_1350 * sin);
         this.vz = this.vz + (v.field_1350 * cos + v.field_1352 * sin);
      }
   }

   private void move(World world) {
      double mx = this.vx;
      double my = this.vy;
      double mz = this.vz;
      class_2680 inside = world.method_8320(class_2338.method_49637(this.x, this.y, this.z));
      if (inside.method_27852(class_2246.field_10343)) {
         mx *= 0.25;
         my *= 0.05F;
         mz *= 0.25;
         this.vx = this.vy = this.vz = 0.0;
      } else if (inside.method_27852(class_2246.field_16999)) {
         mx *= 0.8F;
         my *= 0.75;
         mz *= 0.8F;
         this.vx = this.vy = this.vz = 0.0;
      }

      class_243 moved = this.collide(world, new class_243(mx, my, mz));
      this.x = this.x + moved.field_1352;
      this.y = this.y + moved.field_1351;
      this.z = this.z + moved.field_1350;
      boolean hx = !class_3532.method_20390(mx, moved.field_1352);
      boolean hz = !class_3532.method_20390(mz, moved.field_1350);
      this.horizontalCollision = hx || hz;
      boolean vertical = my != moved.field_1351;
      this.onGround = vertical && my < 0.0;
      if (hx) {
         this.vx = 0.0;
      }

      if (hz) {
         this.vz = 0.0;
      }

      if (vertical) {
         this.vy = 0.0;
      }

      class_2680 at = world.method_8320(class_2338.method_49637(this.x, this.y, this.z));
      float m = at.method_26204().method_23349();
      if (!at.method_27852(class_2246.field_10382) && !at.method_27852(class_2246.field_10422) && m == 1.0F) {
         m = world.method_8320(this.below()).method_26204().method_23349();
      }

      this.vx *= m;
      this.vz *= m;
   }

   private class_243 collide(World world, class_243 movement) {
      class_238 box = this.box();
      List<class_265> shapes = world.collisions(box.method_18804(movement));
      class_243 result = movement.method_1027() == 0.0 ? movement : adjust(movement, box, shapes);
      boolean bx = movement.field_1352 != result.field_1352;
      boolean by = movement.field_1351 != result.field_1351;
      boolean bz = movement.field_1350 != result.field_1350;
      boolean landed = by && movement.field_1351 < 0.0;
      if (this.stepHeight > 0.0 && (landed || this.onGround) && (bx || bz)) {
         class_238 base = landed ? box.method_989(0.0, result.field_1351, 0.0) : box;
         class_238 reach = base.method_1012(movement.field_1352, this.stepHeight, movement.field_1350);
         if (!landed) {
            reach = reach.method_1012(0.0, -1.0E-5F, 0.0);
         }

         List<class_265> around = world.collisions(reach);
         float[] heights = stepHeights(base, around, (float)this.stepHeight, (float)result.field_1351);

         for (float g : heights) {
            class_243 step = adjust(new class_243(movement.field_1352, g, movement.field_1350), base, around);
            if (step.method_37268() > result.method_37268()) {
               return step.method_1031(0.0, -(box.field_1322 - base.field_1322), 0.0);
            }
         }
      }

      return result;
   }

   private static float[] stepHeights(class_238 box, List<class_265> shapes, float max, float current) {
      FloatSet set = new FloatArraySet(4);

      for (class_265 shape : shapes) {
         DoubleListIterator var7 = shape.method_1109(class_2351.field_11052).iterator();

         while (var7.hasNext()) {
            double d = (Double)var7.next();
            float g = (float)(d - box.field_1322);
            if (!(g < 0.0F) && g != current) {
               if (g > max) {
                  break;
               }

               set.add(g);
            }
         }
      }

      float[] a = set.toFloatArray();
      FloatArrays.unstableSort(a);
      return a;
   }

   private static class_243 adjust(class_243 m, class_238 box, List<class_265> shapes) {
      double d = m.field_1352;
      double e = m.field_1351;
      double f = m.field_1350;
      if (shapes.isEmpty()) {
         return m;
      }

      if (e != 0.0) {
         e = class_259.method_1085(class_2351.field_11052, box, shapes, e);
         if (e != 0.0) {
            box = box.method_989(0.0, e, 0.0);
         }
      }

      boolean zFirst = Math.abs(d) < Math.abs(f);
      if (zFirst && f != 0.0) {
         f = class_259.method_1085(class_2351.field_11051, box, shapes, f);
         if (f != 0.0) {
            box = box.method_989(0.0, 0.0, f);
         }
      }

      if (d != 0.0) {
         d = class_259.method_1085(class_2351.field_11048, box, shapes, d);
         if (!zFirst && d != 0.0) {
            box = box.method_989(d, 0.0, 0.0);
         }
      }

      if (!zFirst && f != 0.0) {
         f = class_259.method_1085(class_2351.field_11051, box, shapes, f);
      }

      return new class_243(d, e, f);
   }

   private void fluids(World world) {
      class_238 b = this.box().method_1011(0.001);
      this.waterHeight = this.lavaHeight = 0.0;
      this.inWater = this.inLava = false;
      class_2339 pos = new class_2339();

      for (int bx = class_3532.method_15357(b.field_1323); bx < class_3532.method_15384(b.field_1320); bx++) {
         for (int by = class_3532.method_15357(b.field_1322); by < class_3532.method_15384(b.field_1325); by++) {
            for (int bz = class_3532.method_15357(b.field_1321); bz < class_3532.method_15384(b.field_1324); bz++) {
               class_3610 fluid = world.method_8316(pos.method_10103(bx, by, bz));
               if (!fluid.method_15769()) {
                  double top = by + fluid.method_15763(world, pos);
                  if (!(top < b.field_1322)) {
                     if (fluid.method_15772().method_15780(class_3612.field_15910)) {
                        this.inWater = true;
                        this.waterHeight = Math.max(this.waterHeight, top - b.field_1322);
                     } else {
                        this.inLava = true;
                        this.lavaHeight = Math.max(this.lavaHeight, top - b.field_1322);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean fluidIn(World world, class_238 b) {
      class_2339 pos = new class_2339();

      for (int bx = class_3532.method_15357(b.field_1323); bx < class_3532.method_15384(b.field_1320); bx++) {
         for (int by = class_3532.method_15357(b.field_1322); by < class_3532.method_15384(b.field_1325); by++) {
            for (int bz = class_3532.method_15357(b.field_1321); bz < class_3532.method_15384(b.field_1324); bz++) {
               if (!world.method_8316(pos.method_10103(bx, by, bz)).method_15769()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   boolean inWater() {
      return this.inWater;
   }
}
