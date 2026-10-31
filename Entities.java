package pl.easybot.bot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1299;
import net.minecraft.class_1934;
import net.minecraft.class_238;
import net.minecraft.class_2703;
import net.minecraft.class_5135;
import net.minecraft.class_5900;
import net.minecraft.class_2703.class_2705;
import net.minecraft.class_2703.class_5893;
import net.minecraft.class_5900.class_5901;

class Entities {
   private final Map<Integer, Entities.Mob> mobs = new HashMap<>();
   private final Map<UUID, String> names = new HashMap<>();
   private final Map<UUID, class_1934> modes = new HashMap<>();
   private final Map<String, String> members = new HashMap<>();
   private final Map<String, String> rules = new HashMap<>();

   void clearWorld() {
      this.mobs.clear();
   }

   void clearAll() {
      this.mobs.clear();
      this.names.clear();
      this.modes.clear();
      this.members.clear();
      this.rules.clear();
   }

   void spawn(int id, UUID uuid, class_1299<?> type, double x, double y, double z) {
      if (type != class_1299.field_6131 && (type == class_1299.field_6097 || class_5135.method_26875(type))) {
         this.mobs.put(id, new Entities.Mob(uuid, type, x, y, z));
      } else {
         this.mobs.remove(id);
      }
   }

   void moveBy(int id, double dx, double dy, double dz) {
      Entities.Mob m = this.mobs.get(id);
      if (m != null) {
         m.x += dx;
         m.y += dy;
         m.z += dz;
      }
   }

   void moveTo(int id, double x, double y, double z) {
      Entities.Mob m = this.mobs.get(id);
      if (m != null) {
         m.x = x;
         m.y = y;
         m.z = z;
      }
   }

   Entities.Mob get(int id) {
      return this.mobs.get(id);
   }

   void remove(int id) {
      this.mobs.remove(id);
   }

   void playerList(class_2703 p) {
      for (class_2705 e : p.method_46329()) {
         if (p.method_46327().contains(class_5893.field_29136) && e.comp_1107() != null) {
            this.names.put(e.comp_1106(), e.comp_1107().getName());
         }

         if (p.method_46327().contains(class_5893.field_29137)) {
            this.modes.put(e.comp_1106(), e.comp_1110());
         }
      }
   }

   void playerRemove(Iterable<UUID> ids) {
      for (UUID id : ids) {
         this.names.remove(id);
         this.modes.remove(id);
      }
   }

   void team(class_5900 p) {
      String team = p.method_34177();
      if (p.method_34176() == class_5901.field_29156) {
         this.rules.remove(team);
         this.members.values().removeIf(team::equals);
      } else {
         p.method_34179().ifPresent(t -> this.rules.put(team, t.method_34186()));
         if (p.method_34174() == class_5901.field_29155) {
            for (String name : p.method_34178()) {
               this.members.put(name, team);
            }
         } else if (p.method_34174() == class_5901.field_29156) {
            for (String name : p.method_34178()) {
               this.members.remove(name, team);
            }
         }
      }
   }

   private String teamOf(Entities.Mob m) {
      String key = m.type == class_1299.field_6097 ? this.names.get(m.uuid) : m.uuid.toString();
      return key == null ? null : this.members.get(key);
   }

   private String rule(String team) {
      return team == null ? "always" : this.rules.getOrDefault(team, "always");
   }

   void pushes(Body body, String self, boolean spectator) {
      if (!spectator && !body.flying) {
         class_238 me = body.box();
         String myTeam = this.members.get(self);
         String myRule = this.rule(myTeam);
         if (!myRule.equals("never")) {
            for (Entities.Mob m : this.mobs.values()) {
               if ((m.type != class_1299.field_6097 || this.modes.get(m.uuid) != class_1934.field_9219) && m.box().method_994(me)) {
                  String team = this.teamOf(m);
                  String rule = this.rule(team);
                  if (!rule.equals("never")) {
                     boolean same = team != null && team.equals(myTeam);
                     if ((!rule.equals("pushOwnTeam") && !myRule.equals("pushOwnTeam") || !same)
                        && (!rule.equals("pushOtherTeams") && !myRule.equals("pushOtherTeams") || same)) {
                        body.push(m.x, m.z);
                     }
                  }
               }
            }
         }
      }
   }

   static final class Mob {
      final UUID uuid;
      final class_1299<?> type;
      double x;
      double y;
      double z;

      Mob(UUID uuid, class_1299<?> type, double x, double y, double z) {
         this.uuid = uuid;
         this.type = type;
         this.x = x;
         this.y = y;
         this.z = z;
      }

      class_238 box() {
         float w = this.type.method_17685() / 2.0F;
         float h = this.type.method_17686();
         return new class_238(this.x - w, this.y, this.z - w, this.x + w, this.y + h, this.z + w);
      }
   }
}
