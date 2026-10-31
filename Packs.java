package pl.easybot.bot;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.util.UndashedUuid;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.class_1077;
import net.minecraft.class_1078;
import net.minecraft.class_155;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_3262;
import net.minecraft.class_3264;
import net.minecraft.class_3521;
import net.minecraft.class_5352;
import net.minecraft.class_641;
import net.minecraft.class_642;
import net.minecraft.class_6489;
import net.minecraft.class_6861;
import net.minecraft.class_9224;
import net.minecraft.class_3258.class_8615;
import net.minecraft.class_3521.class_9034;
import net.minecraft.class_642.class_643;

class Packs {
   static final int LOADED = 0;
   static final int DECLINED = 1;
   static final int FAILED_DOWNLOAD = 2;
   static final int ACCEPTED = 3;
   static final int DOWNLOADED = 4;
   static final int INVALID_URL = 5;
   private final String name;
   private final UUID uuid;
   private final String server;
   private final BiConsumer<UUID, Integer> status;
   private final Map<UUID, Path> applied = new LinkedHashMap<>();
   private final ExecutorService io;
   private volatile class_2477 language;

   Packs(String name, UUID uuid, String server, BiConsumer<UUID, Integer> status) {
      this.name = name;
      this.uuid = uuid;
      this.server = server;
      this.status = status;
      this.io = Executors.newSingleThreadExecutor(r -> {
         Thread t = new Thread(r, "EasyBot-packs-" + name);
         t.setDaemon(true);
         return t;
      });
   }

   void shutdown() {
      this.io.shutdownNow();
   }

   void push(UUID id, String url, String hash) {
      URL parsed = parse(url);
      if (parsed == null) {
         this.status.accept(id, 5);
      } else {
         this.io.execute(() -> {
            try {
               if (this.prompted()) {
                  Thread.sleep(ThreadLocalRandom.current().nextLong(1400L, 3200L));
               }

               this.status.accept(id, 3);
               Path file = this.download(id, parsed, hash);
               if (file == null) {
                  this.status.accept(id, 2);
                  return;
               }

               this.status.accept(id, 4);
               long start = System.currentTimeMillis();
               synchronized (this.applied) {
                  this.applied.put(id, file);
               }

               this.language = this.build();
               long rest = ThreadLocalRandom.current().nextLong(900L, 2200L) - (System.currentTimeMillis() - start);
               if (rest > 0L) {
                  Thread.sleep(rest);
               }

               this.status.accept(id, 0);
            } catch (InterruptedException var10) {
            }
         });
      }
   }

   void pop(UUID id) {
      this.io.execute(() -> {
         synchronized (this.applied) {
            if (id == null) {
               this.applied.clear();
            } else {
               this.applied.remove(id);
            }
         }

         this.language = this.build();
      });
   }

   String translate(class_2561 text) {
      class_2477 lang = this.language();
      class_310 mc = class_310.method_1551();
      Supplier<String> job = () -> {
         class_2477 old = class_2477.method_10517();
         class_2477.method_29427(lang);

         try {
            return text.getString();
         } finally {
            class_2477.method_29427(old);
         }
      };
      if (mc.method_18854()) {
         return job.get();
      }

      try {
         return (String)mc.method_5385(job).get(5L, TimeUnit.SECONDS);
      } catch (Exception e) {
         return text.getString();
      }
   }

   private class_2477 language() {
      class_2477 l = this.language;
      if (l == null) {
         this.language = l = this.build();
      }

      return l;
   }

   private boolean prompted() {
      try {
         class_641 list = new class_641(class_310.method_1551());
         list.method_2981();

         for (int i = 0; i < list.method_2984(); i++) {
            class_642 info = list.method_2982(i);
            String address = info.field_3761.toLowerCase();
            if (address.equals(this.server) || address.startsWith(this.server + ":")) {
               return info.method_2990() != class_643.field_3768;
            }
         }
      } catch (Exception var5) {
      }

      return true;
   }

   private Path download(UUID id, URL url, String hash) {
      class_310 mc = class_310.method_1551();
      class_6489 v = class_155.method_16673();
      Map<String, String> headers = Map.of(
         "X-Minecraft-Username",
         this.name,
         "X-Minecraft-UUID",
         UndashedUuid.toString(this.uuid),
         "X-Minecraft-Version",
         v.method_48019(),
         "X-Minecraft-Version-ID",
         v.method_48018(),
         "X-Minecraft-Pack-Format",
         String.valueOf(v.method_48017(class_3264.field_14188)),
         "User-Agent",
         "Minecraft Java/" + v.method_48019()
      );
      HashCode code = null;
      if (hash != null && hash.matches("[0-9a-fA-F]{40}")) {
         code = HashCode.fromString(hash.toLowerCase());
      }

      Path dir = mc.field_1697.toPath().resolve("downloads").resolve("easybot-" + UndashedUuid.toString(this.uuid)).resolve(id.toString());

      try {
         return class_3521.method_55496(dir, url, headers, Hashing.sha1(), code, 262144000, mc.method_1487(), new class_9034() {
            public void method_55497() {
            }

            public void method_55499(OptionalLong contentLength) {
            }

            public void method_55498(long writtenBytes) {
            }

            public void method_55500(boolean success) {
            }
         });
      } catch (Exception e) {
         return null;
      }
   }

   private class_2477 build() {
      class_310 mc = class_310.method_1551();
      List<class_3262> packs = new ArrayList<>();
      packs.add(mc.method_45573());
      List<class_3262> opened = new ArrayList<>();
      synchronized (this.applied) {
         for (Entry<UUID, Path> e : this.applied.entrySet()) {
            try {
               class_3262 pack = new class_8615(e.getValue().toFile())
                  .method_52424(new class_9224("server/" + e.getKey(), class_2561.method_43470("server"), class_5352.field_25350, Optional.empty()));
               packs.add(pack);
               opened.add(pack);
            } catch (Exception var17) {
            }
         }
      }

      String code = mc.field_1690.field_1883;
      List<String> defs = new ArrayList<>(List.of("en_us"));
      boolean rtl = false;
      class_1077 def = mc.method_1526().method_4668(code);
      if (!"en_us".equals(code) && def != null) {
         defs.add(code);
         rtl = def.comp_1200();
      }

      try {
         return class_1078.method_4675(new class_6861(class_3264.field_14188, packs), defs, rtl);
      } finally {
         for (class_3262 p : opened) {
            p.close();
         }
      }
   }

   private static URL parse(String url) {
      try {
         URL u = new URL(url);
         return !"http".equals(u.getProtocol()) && !"https".equals(u.getProtocol()) ? null : u;
      } catch (MalformedURLException e) {
         return null;
      }
   }
}
