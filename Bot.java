package pl.easybot.bot;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.class_10182;
import net.minecraft.class_10185;
import net.minecraft.class_10264;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_155;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_1934;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2505;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2604;
import net.minecraft.class_2622;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2645;
import net.minecraft.class_2649;
import net.minecraft.class_2653;
import net.minecraft.class_2664;
import net.minecraft.class_2666;
import net.minecraft.class_2668;
import net.minecraft.class_2672;
import net.minecraft.class_2678;
import net.minecraft.class_2693;
import net.minecraft.class_2696;
import net.minecraft.class_2703;
import net.minecraft.class_2708;
import net.minecraft.class_2716;
import net.minecraft.class_2724;
import net.minecraft.class_2735;
import net.minecraft.class_2743;
import net.minecraft.class_2777;
import net.minecraft.class_2781;
import net.minecraft.class_2790;
import net.minecraft.class_2813;
import net.minecraft.class_2815;
import net.minecraft.class_2846;
import net.minecraft.class_2851;
import net.minecraft.class_2855;
import net.minecraft.class_2868;
import net.minecraft.class_2874;
import net.minecraft.class_2877;
import net.minecraft.class_2879;
import net.minecraft.class_2886;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3544;
import net.minecraft.class_3917;
import net.minecraft.class_3944;
import net.minecraft.class_4844;
import net.minecraft.class_5134;
import net.minecraft.class_5250;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_5900;
import net.minecraft.class_5912;
import net.minecraft.class_6368;
import net.minecraft.class_6370;
import net.minecraft.class_639;
import net.minecraft.class_6860;
import net.minecraft.class_6880;
import net.minecraft.class_6903;
import net.minecraft.class_7756;
import net.minecraft.class_7828;
import net.minecraft.class_7887;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_8242;
import net.minecraft.class_8589;
import net.minecraft.class_8734;
import net.minecraft.class_8760;
import net.minecraft.class_8791;
import net.minecraft.class_8824;
import net.minecraft.class_9129;
import net.minecraft.class_9139;
import net.minecraft.class_9173;
import net.minecraft.class_9220;
import net.minecraft.class_9247;
import net.minecraft.class_9250;
import net.minecraft.class_9334;
import net.minecraft.class_9834;
import net.minecraft.class_9835;
import net.minecraft.class_1322.class_1323;
import net.minecraft.class_2703.class_2705;
import net.minecraft.class_2703.class_5893;
import net.minecraft.class_2781.class_2782;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_5455.class_6890;
import net.minecraft.class_7225.class_7874;
import net.minecraft.class_7782.class_9176;

public class Bot implements Runnable {
   public static final String DEFAULT_SERVER = "localhost";
   private static final int MAX_LINES = 1000;
   private static final class_2960 SPRINT_BOOST = class_2960.method_60656("sprinting");
   private static final String PASSWORD = "eloelo";
   public static volatile boolean autoLogin = true;
   public static volatile boolean autoAfk = true;
   private static final Pattern CODE = Pattern.compile("\\d{3,}");
   private static class_7874 lookup;
   public final String name;
   public final String server;
   private final UUID uuid;
   private final List<Bot.Line> lines = new ArrayList<>();
   private final Object writeLock = new Object();
   private final Inflater inflater = new Inflater();
   private final Deflater deflater = new Deflater();
   private final ScheduledExecutorService worker;
   private final List<String> chatTypes = new ArrayList<>();
   private final Map<class_2960, byte[]> cookies = new HashMap<>();
   private final Set<Integer> brokenPackets = new HashSet<>();
   private final Packs packs;
   private volatile Bot.State state = Bot.State.CONNECTING;
   private volatile boolean stopping;
   private volatile int version;
   private volatile int gen;
   private volatile class_639 transfer;
   private Socket socket;
   private DataInputStream in;
   private OutputStream out;
   private volatile int threshold = -1;
   private boolean joined;
   private boolean inWorld;
   private volatile boolean positioned;
   private final Body body = new Body();
   private World world;
   private class_5321<class_1937> dimension;
   private int entityId;
   private int loadStep;
   private boolean loaded;
   private int loadTicks;
   private long tickCount;
   private double lastX;
   private double lastY;
   private double lastZ;
   private float lastYaw;
   private float lastPitch;
   private boolean lastOnGround;
   private boolean lastHorizontal;
   private int sincePosition;
   private class_8760 batch = new class_8760();
   private boolean dead;
   private String actionBar = "";
   private long connectedAt;
   private class_9173 clientRegistries = new class_9173();
   private class_9247 dataPacks;
   private volatile class_6890 registries;
   private final Object invLock = new Object();
   private class_1799[] player = emptySlots(46);
   private Bot.Window window;
   private class_1799 cursor = class_1799.field_8037;
   private int selected;
   private int revision;
   private int sequence;
   private boolean using;
   private class_1799 anvilTracked = class_1799.field_8037;
   private String anvilName;
   private Bot.SignEdit signEdit;
   private ScheduledFuture<?> signTimeout;
   private long lastAuth;
   private int authTries;
   private boolean rulesSeen;
   private boolean rulesClicked;
   private boolean frozen;
   private int rulesTries;
   private final Entities entities = new Entities();
   private final Wander wander = new Wander();
   private class_10185 lastInput = class_10185.field_54098;
   private boolean spectator;
   private long calmUntil;
   private long authHold;
   private boolean rulesDone;
   private boolean trust;
   private int trustClicks;
   private int trustWindow = -1;
   private int trustWait;
   private boolean trustListed;
   private boolean lastSprint;
   private final ArrayDeque<Long> setbacks = new ArrayDeque<>();
   private long walkBanUntil;
   private static BufferedWriter file;

   public Bot(String name, String server) {
      this.name = name;
      this.server = server;
      this.uuid = class_4844.method_43344(name);
      this.packs = new Packs(name, this.uuid, server, this::packStatus);
      this.worker = Executors.newSingleThreadScheduledExecutor(r -> {
         Thread tx = new Thread(r, "EasyBot-tick-" + name);
         tx.setDaemon(true);
         return tx;
      });
      Thread t = new Thread(this, "EasyBot-" + name);
      t.setDaemon(true);
      t.start();
   }

   public Bot.State state() {
      return this.state;
   }

   public boolean active() {
      return this.state != Bot.State.OFFLINE;
   }

   public int version() {
      return this.version;
   }

   public List<Bot.Line> lines() {
      synchronized (this.lines) {
         return new ArrayList<>(this.lines);
      }
   }

   public void clear() {
      synchronized (this.lines) {
         this.lines.clear();
      }

      this.version++;
   }

   public long onlineMillis() {
      return this.state == Bot.State.PLAY && this.connectedAt > 0L ? System.currentTimeMillis() - this.connectedAt : 0L;
   }

   public String position() {
      return this.positioned ? String.format(Locale.ROOT, "%.0f %.0f %.0f", this.body.x, this.body.y, this.body.z) : null;
   }

   @Override
   public void run() {
      this.worker.scheduleAtFixedRate(this::tick, 50L, 50L, TimeUnit.MILLISECONDS);

      try {
         while (true) {
            try {
               this.connect();

               while (!this.stopping) {
                  class_2540 b = this.read();
                  if (this.state == Bot.State.LOGIN) {
                     this.handle(b);
                  } else {
                     int g = this.gen;
                     this.worker.execute(() -> {
                        if (g == this.gen && !this.stopping) {
                           this.safely(b);
                        }
                     });
                  }
               }

               return;
            } catch (Exception e) {
               if (this.transfer == null || this.stopping) {
                  if (!this.stopping) {
                     this.log(class_2561.method_43470("Rozłączono: " + describe(e)).method_27692(class_124.field_1061));
                  }
                  break;
               }
            }
         }
      } finally {
         this.closeSocket();
         this.state = Bot.State.OFFLINE;
         this.worker.shutdownNow();
         this.packs.shutdown();
         this.version++;
      }
   }

   public void disconnect() {
      if (!this.stopping) {
         this.stopping = true;
         this.log(class_2561.method_43470("Rozłączono bota.").method_27692(class_124.field_1080));
         this.closeSocket();
      }
   }

   public void chat(String message) {
      String msg = message.strip();
      if (!msg.isEmpty()) {
         this.worker.execute(() -> {
            if (this.state != Bot.State.PLAY) {
               this.log(class_2561.method_43470("Bot nie jest jeszcze na serwerze.").method_27692(class_124.field_1061));
            } else {
               try {
                  if (msg.startsWith("/")) {
                     this.send(5, b -> b.method_10788(msg.substring(1), 32767));
                  } else {
                     this.send(7, b -> {
                        b.method_10788(msg.length() > 256 ? msg.substring(0, 256) : msg, 256);
                        b.method_44115(Instant.now());
                        b.method_52974(ThreadLocalRandom.current().nextLong());
                        b.method_52964(false);
                        b.method_10804(0);
                        b.method_46252(new BitSet(), 20);
                     });
                  }

                  this.log(class_2561.method_43470("> " + msg).method_54663(9419007));
               } catch (IOException e) {
                  this.log(class_2561.method_43470("Nie wysłano: " + describe(e)).method_27692(class_124.field_1061));
               }
            }
         });
      }
   }

   public Bot.Inventory inventory() {
      synchronized (this.invLock) {
         Bot.Window w = this.window == null
            ? null
            : new Bot.Window(
               this.window.id(), this.window.type(), this.window.title(), this.window.slots() == null ? null : (class_1799[])this.window.slots().clone()
            );
         return new Bot.Inventory((class_1799[])this.player.clone(), w, this.cursor, this.selected, this.using, this.registries);
      }
   }

   public void click(int slot, int button, class_1713 type) {
      this.action(() -> {
         Int2ObjectOpenHashMap<class_1799> changed = new Int2ObjectOpenHashMap();
         int syncId;
         int rev;
         class_1799 carried;
         synchronized (this.invLock) {
            syncId = this.window != null ? this.window.id() : 0;
            rev = this.revision;
            if (type == class_1713.field_7790) {
               this.predictPickup(slot, button, changed);
            }

            carried = this.cursor.method_7972();
         }

         this.version++;
         this.send(16, class_2813.field_48189, new class_2813(syncId, rev, slot, button, type, carried, changed));
      });
   }

   private void predictPickup(int slot, int button, Int2ObjectOpenHashMap<class_1799> changed) {
      if (slot == -999) {
         if (!this.cursor.method_7960()) {
            this.cursor = button == 0 ? class_1799.field_8037 : this.cursor.method_46651(this.cursor.method_7947() - 1);
         }
      } else {
         class_1799[] slots = this.window != null ? this.window.slots() : this.player;
         if (slots != null && slot >= 0 && slot < slots.length) {
            class_1799 s = slots[slot];
            class_1799 c = this.cursor;
            class_1799 newSlot;
            class_1799 newCursor;
            if (c.method_7960()) {
               if (s.method_7960()) {
                  return;
               }

               int take = button == 0 ? s.method_7947() : (s.method_7947() + 1) / 2;
               newCursor = s.method_46651(take);
               newSlot = s.method_7947() - take <= 0 ? class_1799.field_8037 : s.method_46651(s.method_7947() - take);
            } else if (s.method_7960()) {
               int put = button == 0 ? c.method_7947() : 1;
               newSlot = c.method_46651(put);
               newCursor = c.method_7947() - put <= 0 ? class_1799.field_8037 : c.method_46651(c.method_7947() - put);
            } else if (class_1799.method_31577(s, c)) {
               int put = Math.min(s.method_7914() - s.method_7947(), button == 0 ? c.method_7947() : 1);
               if (put <= 0) {
                  return;
               }

               newSlot = s.method_46651(s.method_7947() + put);
               newCursor = c.method_7947() - put <= 0 ? class_1799.field_8037 : c.method_46651(c.method_7947() - put);
            } else {
               newSlot = c;
               newCursor = s;
            }

            slots[slot] = newSlot;
            this.cursor = newCursor;
            changed.put(slot, newSlot.method_7972());
            if (this.window != null && slots.length >= 36 && slot >= slots.length - 36) {
               this.player[9 + slot - (slots.length - 36)] = newSlot;
            }
         }
      }
   }

   public void closeWindow() {
      this.action(() -> {
         int id;
         synchronized (this.invLock) {
            if (this.window == null) {
               return;
            }

            id = this.window.id();
            this.window = null;
         }

         this.version++;
         this.send(17, class_2815.field_48191, new class_2815(id));
         this.log(class_2561.method_43470("[GUI] Zamknięto okno.").method_27692(class_124.field_1063));
      });
   }

   public void select(int slot) {
      this.action(() -> {
         synchronized (this.invLock) {
            if (this.selected == slot) {
               return;
            }

            this.selected = slot;
         }

         this.version++;
         this.send(51, class_2868.field_48216, new class_2868(slot));
      });
   }

   public void useItem() {
      this.action(() -> {
         this.send(61, class_2886.field_48226, new class_2886(class_1268.field_5808, ++this.sequence, this.body.yaw, this.body.pitch));
         this.send(58, class_2879.field_48223, new class_2879(class_1268.field_5808));
         this.log(class_2561.method_43470("[Akcja] PPM: ").method_27692(class_124.field_1063).method_10852(this.heldName()));
      });
   }

   public void swing() {
      this.action(() -> {
         this.send(58, class_2879.field_48223, new class_2879(class_1268.field_5808));
         this.log(class_2561.method_43470("[Akcja] LPM: ").method_27692(class_124.field_1063).method_10852(this.heldName()));
      });
   }

   public void holdUse(boolean hold) {
      this.action(() -> {
         if (hold) {
            this.send(61, class_2886.field_48226, new class_2886(class_1268.field_5808, ++this.sequence, this.body.yaw, this.body.pitch));
            this.log(class_2561.method_43470("[Akcja] Trzyma PPM: ").method_27692(class_124.field_1063).method_10852(this.heldName()));
         } else {
            this.send(39, class_2846.field_48207, new class_2846(class_2847.field_12974, class_2338.field_10980, class_2350.field_11033));
            this.log(class_2561.method_43470("[Akcja] Puścił PPM.").method_27692(class_124.field_1063));
         }

         synchronized (this.invLock) {
            this.using = hold;
         }

         this.version++;
      });
   }

   public void drop(boolean all) {
      this.action(
         () -> {
            class_2561 held = this.heldName();
            this.send(
               39,
               class_2846.field_48207,
               new class_2846(all ? class_2847.field_12970 : class_2847.field_12975, class_2338.field_10980, class_2350.field_11033)
            );
            synchronized (this.invLock) {
               class_1799 stack = this.player[36 + this.selected];
               class_1799 left = !all && stack.method_7947() > 1 ? stack.method_46651(stack.method_7947() - 1) : class_1799.field_8037;
               this.player[36 + this.selected] = left;
               if (this.window != null && this.window.slots() != null && this.window.slots().length >= 36) {
                  this.window.slots()[this.window.slots().length - 9 + this.selected] = left;
               }
            }

            this.version++;
            this.log(class_2561.method_43470("[Akcja] Wyrzucił: ").method_27692(class_124.field_1063).method_10852(held));
         }
      );
   }

   public void throwAll() {
      this.action(() -> {
         int open;
         synchronized (this.invLock) {
            open = this.window != null ? this.window.id() : -1;
            this.window = null;
         }

         if (open >= 0) {
            this.version++;
            this.send(17, class_2815.field_48191, new class_2815(open));
         }

         List<Integer> slots = new ArrayList<>();
         boolean carrying;
         synchronized (this.invLock) {
            for (int i = 5; i < 46; i++) {
               if (!this.player[i].method_7960()) {
                  slots.add(i);
               }
            }

            carrying = !this.cursor.method_7960();
         }

         if (slots.isEmpty() && !carrying) {
            this.log(class_2561.method_43470("[Akcja] Ekwipunek jest pusty.").method_27692(class_124.field_1063));
         } else {
            this.log(class_2561.method_43470("[Akcja] Wyrzucam cały ekwipunek (" + slots.size() + " stacków).").method_27692(class_124.field_1065));
            int g = this.gen;
            long delay = ThreadLocalRandom.current().nextLong(150L, 300L);
            if (carrying) {
               this.worker.schedule(() -> this.throwSlot(g, -999), delay, TimeUnit.MILLISECONDS);
               delay += ThreadLocalRandom.current().nextLong(60L, 140L);
            }

            for (int s : slots) {
               this.worker.schedule(() -> this.throwSlot(g, s), delay, TimeUnit.MILLISECONDS);
               delay += ThreadLocalRandom.current().nextLong(60L, 140L);
            }
         }
      });
   }

   private void throwSlot(int g, int slot) {
      if (g == this.gen && this.state == Bot.State.PLAY) {
         Int2ObjectOpenHashMap<class_1799> changed = new Int2ObjectOpenHashMap();
         int rev;
         class_1799 carried;
         synchronized (this.invLock) {
            if (this.window != null) {
               return;
            }

            if (slot == -999) {
               if (this.cursor.method_7960()) {
                  return;
               }

               this.cursor = class_1799.field_8037;
            } else {
               if (this.player[slot].method_7960()) {
                  return;
               }

               this.player[slot] = class_1799.field_8037;
               changed.put(slot, class_1799.field_8037);
            }

            rev = this.revision;
            carried = this.cursor.method_7972();
         }

         this.version++;

         try {
            this.send(
               16,
               class_2813.field_48189,
               slot == -999
                  ? new class_2813(0, rev, -999, 0, class_1713.field_7790, carried, changed)
                  : new class_2813(0, rev, slot, 1, class_1713.field_7795, carried, changed)
            );
         } catch (IOException var8) {
         }
      }
   }

   private class_2561 heldName() {
      synchronized (this.invLock) {
         class_1799 stack = this.player[36 + this.selected];
         return stack.method_7960()
            ? class_2561.method_43470("pusta ręka")
            : class_2561.method_43473().method_10852(stack.method_7964()).method_27693(stack.method_7947() > 1 ? " x" + stack.method_7947() : "");
      }
   }

   private void action(Bot.Action a) {
      this.worker.execute(() -> {
         if (this.state == Bot.State.PLAY && this.registries != null) {
            try {
               a.run();
            } catch (IOException e) {
               this.log(class_2561.method_43470("Nie wysłano: " + describe(e)).method_27692(class_124.field_1061));
            }
         }
      });
   }

   private static class_1799[] emptySlots(int n) {
      class_1799[] a = new class_1799[n];
      Arrays.fill(a, class_1799.field_8037);
      return a;
   }

   private void resetInventory() {
      synchronized (this.invLock) {
         this.player = emptySlots(46);
         this.window = null;
         this.cursor = class_1799.field_8037;
         this.using = false;
      }

      this.version++;
   }

   private void startConfig() {
      this.clientRegistries = new class_9173();
      this.dataPacks = null;
   }

   private void buildRegistries() {
      class_6890 base = class_7756.method_45738().method_45926();

      try {
         if (this.dataPacks != null) {
            class_6860 resources = this.dataPacks.method_57046();

            try {
               this.registries = this.clientRegistries.method_56585(resources, base, false);
            } catch (Throwable var6) {
               if (resources != null) {
                  try {
                     resources.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (resources != null) {
               resources.close();
            }
         } else {
            this.registries = this.clientRegistries.method_56585(class_5912.field_49043, base, false);
         }
      } catch (Exception e) {
         this.registries = null;
         this.log(
            class_2561.method_43470("Nie udało się wczytać rejestrów serwera - podgląd ekwipunku będzie niedostępny (" + describe(e) + ")")
               .method_27692(class_124.field_1065)
         );
      }
   }

   private void connect() throws IOException {
      class_639 address = this.transfer != null ? this.transfer : class_639.method_2950(this.server);
      int intent = this.transfer != null ? 3 : 2;
      this.transfer = null;
      this.gen++;
      this.closeSocket();
      this.threshold = -1;
      this.inflater.reset();
      this.deflater.reset();
      this.state = Bot.State.CONNECTING;
      this.inWorld = false;
      this.world = null;
      this.positioned = false;
      this.registries = null;
      this.resetInventory();
      this.version++;
      this.log(
         class_2561.method_43470(
               (intent == 3 ? "Serwer przenosi bota na " : "Szukam adresu ")
                  + address.method_2952()
                  + (address.method_2954() != 25565 ? ":" + address.method_2954() : "")
                  + "..."
            )
            .method_27692(class_124.field_1080)
      );
      ops();
      Optional<class_6368> resolved = class_6370.field_33745.method_36907(address);
      if (resolved.isEmpty()) {
         throw new IOException("nie znaleziono serwera " + address.method_2952());
      }

      InetSocketAddress target = resolved.get().method_36902();
      this.log(
         class_2561.method_43470("Łączę z " + target.getHostString() + ":" + target.getPort() + " (" + resolved.get().method_36900() + ")")
            .method_27692(class_124.field_1080)
      );
      Socket s = new Socket();
      s.setTcpNoDelay(true);
      s.connect(target, 10000);
      s.setSoTimeout(30000);
      synchronized (this.writeLock) {
         this.socket = s;
         this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
         this.out = new BufferedOutputStream(s.getOutputStream());
      }

      if (this.stopping) {
         throw new SocketException("zatrzymano");
      }

      this.state = Bot.State.LOGIN;
      this.version++;
      this.send(0, b -> {
         b.method_10804(class_155.method_31372());
         b.method_10788(target.getHostName(), 255);
         b.method_52998(target.getPort());
         b.method_10804(intent);
      });
      this.send(0, b -> {
         b.method_10788(this.name, 16);
         b.method_10797(this.uuid);
      });
      this.log(class_2561.method_43470("Połączono, loguję jako " + this.name + "...").method_27692(class_124.field_1080));
   }

   private void safely(class_2540 b) {
      try {
         this.handle(b);
      } catch (EOFException e) {
         this.closeSocket();
      } catch (Exception e) {
         b.method_52988(0);
         int id = b.method_10816();
         if (this.brokenPackets.add(id)) {
            this.log(class_2561.method_43470("Nie odczytano pakietu 0x" + Integer.toHexString(id) + ": " + describe(e)).method_27692(class_124.field_1063));
         }
      }
   }

   private void handle(class_2540 b) throws IOException {
      int id = b.method_10816();
      switch (this.state) {
         case LOGIN:
            this.login(id, b);
            break;
         case CONFIG:
            this.config(id, b);
            break;
         case PLAY:
            this.play(id, b);
      }
   }

   private void login(int id, class_2540 b) throws IOException {
      switch (id) {
         case 0:
            this.kicked(this.jsonText(b.method_19772()));
            break;
         case 1:
            this.log(
               class_2561.method_43470("Serwer wymaga konta premium (szyfrowanie) - bot z dowolnym nickiem tu nie wejdzie.").method_27692(class_124.field_1061)
            );
            this.stopping = true;
            throw new SocketException("serwer w trybie online-mode");
         case 2:
            UUID accepted = b.method_10790();
            String nick = b.method_10800(16);
            this.log(class_2561.method_43470("Zalogowano jako " + nick + " (" + accepted + ")").method_27692(class_124.field_1060));
            this.send(3, x -> {});
            this.startConfig();
            this.state = Bot.State.CONFIG;
            this.version++;
            this.sendClientInfo();
            break;
         case 3:
            this.threshold = b.method_10816();
            this.log(class_2561.method_43470("Kompresja włączona (" + this.threshold + ")").method_27692(class_124.field_1063));
            break;
         case 4:
            int query = b.method_10816();
            this.send(2, x -> {
               x.method_10804(query);
               x.method_52964(false);
            });
            break;
         case 5:
            this.cookie(4, b);
      }
   }

   private void config(int id, class_2540 b) throws IOException {
      switch (id) {
         case 0:
            this.cookie(1, b);
         case 1:
         case 6:
         case 12:
         default:
            break;
         case 2:
            this.kicked(this.nbtText(b));
            break;
         case 3:
            this.buildRegistries();
            this.send(3, x -> {});
            this.state = Bot.State.PLAY;
            this.version++;
            this.log(class_2561.method_43470("Konfiguracja zakończona, wchodzę do świata...").method_27692(class_124.field_1080));
            break;
         case 4: {
            long key = b.readLong();
            this.send(4, x -> x.method_52974(key));
            break;
         }
         case 5: {
            int key = b.readInt();
            this.send(5, x -> x.method_53002(key));
            break;
         }
         case 7: {
            class_8734 p = (class_8734)class_8734.field_48692.decode(b);
            this.clientRegistries.method_56583(p.comp_2258(), p.comp_2259());
            if (p.comp_2258().equals(class_7924.field_41237)) {
               this.chatTypes.clear();

               for (class_9176 e : p.comp_2259()) {
                  this.chatTypes.add(e.comp_2256().toString());
               }
            }
            break;
         }
         case 8:
            this.packs.pop(b.readBoolean() ? b.method_10790() : null);
            break;
         case 9:
            this.resourcePack(b);
            break;
         case 10:
            this.storeCookie(b);
            break;
         case 11:
            this.transfer(b);
            break;
         case 13:
            this.clientRegistries.method_56586(((class_2790)class_2790.field_48630.decode(b)).method_12000());
            break;
         case 14: {
            class_9250 p = (class_9250)class_9250.field_49141.decode(b);
            this.dataPacks = new class_9247();
            this.send(7, class_9220.field_49020, new class_9220(this.dataPacks.method_57049(p.comp_2356())));
         }
      }
   }

   private class_9129 reg(class_2540 b) {
      return new class_9129(b, this.registries);
   }

   private void play(int id, class_2540 b) throws IOException {
      switch (id) {
         case 1: {
            if (this.registries == null) {
               return;
            }

            class_2604 p = (class_2604)class_2604.field_47896.decode(this.reg(b));
            this.entities.spawn(p.method_11167(), p.method_11164(), p.method_11169(), p.method_11175(), p.method_11174(), p.method_11176());
         }
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         case 10:
         case 11:
         case 14:
         case 15:
         case 16:
         case 17:
         case 20:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 31:
         case 36:
         case 37:
         case 38:
         case 41:
         case 42:
         case 43:
         case 45:
         case 46:
         case 49:
         case 50:
         case 51:
         case 52:
         case 56:
         case 57:
         case 60:
         case 61:
         case 62:
         case 65:
         case 68:
         case 69:
         case 70:
         case 72:
         case 73:
         case 77:
         case 79:
         case 80:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 91:
         case 92:
         case 93:
         case 94:
         case 96:
         case 97:
         case 100:
         case 101:
         case 104:
         case 105:
         case 107:
         case 109:
         case 110:
         case 111:
         case 113:
         case 116:
         case 117:
         case 118:
         case 120:
         case 121:
         case 123:
         default:
            break;
         case 7: {
            if (this.world == null || this.registries == null) {
               return;
            }

            class_2622 p = (class_2622)class_2622.field_47903.decode(this.reg(b));
            this.world.blockEntity(p.method_11293(), p.method_11291(), p.method_11290());
            break;
         }
         case 9: {
            if (this.world == null || this.registries == null) {
               return;
            }

            class_2626 p = (class_2626)class_2626.field_47905.decode(this.reg(b));
            this.world.set(p.method_11309(), p.method_11308());
            break;
         }
         case 12:
            this.batch.method_53484(b.method_10816());
            float desired = this.batch.method_53485();
            this.send(9, x -> x.method_52941(desired));
            break;
         case 13:
            this.batch.method_53483();
            break;
         case 18:
            class_2645.field_47915.decode(b);
            this.finishSign();
            boolean had;
            synchronized (this.invLock) {
               had = this.window != null;
               this.window = null;
            }

            this.version++;
            if (had) {
               this.log(class_2561.method_43470("[GUI] Serwer zamknął okno.").method_27692(class_124.field_1063));
            }

            this.calm(20, 50);
            if (this.rulesClicked && !this.rulesDone) {
               this.rulesDone = true;
               this.log(class_2561.method_43470("[Bot] Regulamin zaakceptowany.").method_27692(class_124.field_1060));
            }
            break;
         case 19:
            if (this.registries == null) {
               return;
            }

            class_2649 px = (class_2649)class_2649.field_47916.decode(this.reg(b));
            class_1799[] slots = px.method_11441().toArray(new class_1799[0]);
            synchronized (this.invLock) {
               this.revision = px.method_37438();
               this.cursor = px.method_37437();
               if (px.method_11440() == 0) {
                  if (slots.length == 46) {
                     this.player = slots;
                  }
               } else if (this.window != null && this.window.id() == px.method_11440()) {
                  this.window = new Bot.Window(this.window.id(), this.window.type(), this.window.title(), slots);
                  if (slots.length >= 36) {
                     System.arraycopy(slots, slots.length - 36, this.player, 9, 36);
                  }
               }
            }

            this.version++;
            this.checkAnvil();
            if (this.trustSlot()) {
               return;
            }

            this.checkRules();
            break;
         case 21:
            if (this.registries == null) {
               return;
            }

            class_2653 px = (class_2653)class_2653.field_47918.decode(this.reg(b));
            synchronized (this.invLock) {
               this.revision = px.method_37439();
               int slot = px.method_11450();
               if (px.method_11452() == -1 && slot == -1) {
                  this.cursor = px.method_11449();
               } else if (px.method_11452() == 0 && slot >= 0 && slot < 46) {
                  this.player[slot] = px.method_11449();
               } else if (this.window != null
                  && this.window.id() == px.method_11452()
                  && this.window.slots() != null
                  && slot >= 0
                  && slot < this.window.slots().length) {
                  class_1799[] slots = this.window.slots();
                  slots[slot] = px.method_11449();
                  int base = slots.length - 36;
                  if (slots.length >= 36 && slot >= base) {
                     this.player[9 + slot - base] = px.method_11449();
                  }
               }
            }

            this.version++;
            this.checkAnvil();
            if (this.trustSlot()) {
               return;
            }

            this.checkRules();
            break;
         case 22:
            this.cookie(19, b);
            break;
         case 29:
            this.kicked(this.nbtText(b));
            break;
         case 30:
            class_2561 message = this.nbtText(b);
            int type = b.method_10816();
            if (type == 0) {
               this.log(message);
               AutoFlow.watch(this, message);
               return;
            }

            class_2561 sender = this.nbtText(b);
            class_2561 target = b.readBoolean() ? this.nbtText(b) : null;
            this.log(this.decorate(type, sender, target, message));
            break;
         case 32: {
            class_10264 p = (class_10264)class_10264.field_54515.decode(b);
            class_243 v = p.comp_3224().comp_3148();
            this.entities.moveTo(p.comp_3223(), v.field_1352, v.field_1351, v.field_1350);
            break;
         }
         case 33: {
            if (this.registries == null) {
               return;
            }

            class_2664 p = (class_2664)class_2664.field_47925.decode(this.reg(b));
            p.comp_2884().ifPresent(vx -> {
               this.body.vx = this.body.vx + vx.field_1352;
               this.body.vy = this.body.vy + vx.field_1351;
               this.body.vz = this.body.vz + vx.field_1350;
            });
            break;
         }
         case 34: {
            if (this.world == null) {
               return;
            }

            class_2666 p = (class_2666)class_2666.field_47926.decode(b);
            this.world.unload(p.comp_1726().field_9181, p.comp_1726().field_9180);
            break;
         }
         case 35: {
            class_2668 p = (class_2668)class_2668.field_47927.decode(b);
            if (p.method_11491() == class_2668.field_46637 && this.loadStep == 0) {
               this.loadStep = 1;
            }

            if (p.method_11491() == class_2668.field_25648) {
               this.spectator = class_1934.method_8384(class_3532.method_15375(p.method_11492() + 0.5F)) == class_1934.field_9219;
            }
            break;
         }
         case 39: {
            long key = b.readLong();
            this.send(26, x -> x.method_52974(key));
            break;
         }
         case 40: {
            if (this.world == null || this.registries == null) {
               return;
            }

            class_2672 p = (class_2672)class_2672.field_47933.decode(this.reg(b));
            this.world.load(p.method_11523(), p.method_11524(), p.method_38598().method_38586(), this.tickCount);
            p.method_38598().method_38587(p.method_11523(), p.method_11524()).accept(this.world::blockEntity);
            break;
         }
         case 44: {
            if (this.registries == null) {
               return;
            }

            class_2678 p = (class_2678)class_2678.field_47937.decode(this.reg(b));
            this.entityId = p.comp_88();
            this.enterWorld(p.comp_1727(), true);
            this.inWorld = true;
            if (!this.joined) {
               this.joined = true;
               this.connectedAt = System.currentTimeMillis();
               this.log(
                  class_2561.method_43470("Bot " + this.name + " wszedł na serwer!").method_27695(new class_124[]{class_124.field_1060, class_124.field_1067})
               );
            }
            break;
         }
         case 47:
         case 48:
            int e = b.method_10816();
            this.entities.moveBy(e, b.readShort() / 4096.0, b.readShort() / 4096.0, b.readShort() / 4096.0);
            break;
         case 53:
            if (this.registries == null) {
               return;
            }

            class_3944 px = (class_3944)class_3944.field_47945.decode(this.reg(b));
            this.finishSign();
            synchronized (this.invLock) {
               this.window = new Bot.Window(px.method_17592(), px.method_17593(), px.method_17594(), null);
               this.using = false;
               this.anvilTracked = class_1799.field_8037;
               this.anvilName = null;
            }

            this.version++;
            this.log(class_2561.method_43470("[GUI] Otwarto: ").method_27692(class_124.field_1075).method_10852(px.method_17594()));
            if (this.trust) {
               this.trustWindow = px.method_17592();
               this.trustWait = ThreadLocalRandom.current().nextInt(10, 16);
            }
            break;
         case 54:
            this.openSign((class_2693)class_2693.field_47946.decode(b));
            break;
         case 55: {
            int key = b.readInt();
            this.send(43, x -> x.method_53002(key));
            break;
         }
         case 58: {
            class_2696 p = (class_2696)class_2696.field_47948.decode(b);
            this.body.flying = p.method_11698();
            this.body.flySpeed = p.method_11690();
            break;
         }
         case 59:
            this.playerChat(b);
            break;
         case 63:
            this.entities.playerRemove(((class_7828)class_7828.field_47954.decode(b)).comp_1105());
            break;
         case 64: {
            if (this.registries == null) {
               return;
            }

            class_2703 p = (class_2703)class_2703.field_47955.decode(this.reg(b));
            this.entities.playerList(p);
            if (p.method_46327().contains(class_5893.field_29137)) {
               for (class_2705 ex : p.method_46329()) {
                  if (ex.comp_1106().equals(this.uuid)) {
                     this.spectator = ex.comp_1110() == class_1934.field_9219;
                  }
               }
            }
            break;
         }
         case 66:
            this.teleport((class_2708)class_2708.field_47957.decode(b));
            break;
         case 67:
            this.body.yaw = b.readFloat();
            this.body.pitch = b.readFloat();
            this.sendLook(false);
            break;
         case 71:
            ((class_2716)class_2716.field_47959.decode(b)).method_36548().forEach(this.entities::remove);
            break;
         case 74:
            this.packs.pop(b.readBoolean() ? b.method_10790() : null);
            break;
         case 75:
            this.resourcePack(b);
            break;
         case 76:
            if (this.registries == null) {
               return;
            }

            class_2724 px = (class_2724)class_2724.field_47962.decode(this.reg(b));
            this.enterWorld(px.comp_1728(), false);
            this.dead = false;
            this.finishSign();
            synchronized (this.invLock) {
               this.window = null;
               this.using = false;
            }

            this.version++;
            break;
         case 78:
            if (this.world == null) {
               return;
            }

            ((class_2637)class_2637.field_47964.decode(b)).method_30621(this.world::set);
            break;
         case 81:
            class_2561 textx = this.nbtText(b);
            String plain = textx.getString();
            if (!plain.isBlank() && !plain.equals(this.actionBar)) {
               this.log(class_2561.method_43470("[Action bar] ").method_27692(class_124.field_1063).method_10852(textx));
               AutoFlow.watch(this, textx);
            }

            this.actionBar = plain;
            break;
         case 90:
            if (this.registries == null) {
               return;
            }

            class_1799 stack = ((class_9834)class_9834.field_52326.decode(this.reg(b))).comp_2890();
            synchronized (this.invLock) {
               this.cursor = stack;
            }

            this.version++;
            break;
         case 95: {
            class_2743 p = (class_2743)class_2743.field_47981.decode(b);
            if (p.method_11818() != this.entityId) {
               return;
            }

            this.body.vx = p.method_11815();
            this.body.vy = p.method_11816();
            this.body.vz = p.method_11819();
            break;
         }
         case 98:
            float health = b.readFloat();
            this.body.food = b.method_10816();
            if (health <= 0.0F && !this.dead) {
               this.dead = true;
               this.log(class_2561.method_43470("Bot zginął - odradzam.").method_27692(class_124.field_1061));
               int g = this.gen;
               this.worker.schedule(() -> {
                  if (g == this.gen && this.dead && this.state == Bot.State.PLAY) {
                     try {
                        this.send(10, x -> x.method_10804(0));
                     } catch (IOException var3x) {
                     }
                  }
               }, ThreadLocalRandom.current().nextLong(1200L, 2600L), TimeUnit.MILLISECONDS);
            } else if (health > 0.0F) {
               this.dead = false;
            }
            break;
         case 99:
            int slot = ((class_2735)class_2735.field_47974.decode(b)).comp_3325();
            synchronized (this.invLock) {
               if (slot >= 0 && slot < 9) {
                  this.selected = slot;
               }
            }

            this.version++;
            break;
         case 102:
            if (this.registries == null) {
               return;
            }

            class_9835 px = (class_9835)class_9835.field_52327.decode(this.reg(b));
            int i = px.comp_2891();
            int slot = i < 9 ? 36 + i : (i < 36 ? i : (i < 40 ? 8 - (i - 36) : (i == 40 ? 45 : -1)));
            synchronized (this.invLock) {
               if (slot >= 0) {
                  this.player[slot] = px.comp_2892();
               }
            }

            this.version++;
            break;
         case 103:
            if (this.registries == null) {
               return;
            }

            this.entities.team((class_5900)class_5900.field_47987.decode(this.reg(b)));
            break;
         case 106:
            this.title("Podtytuł", this.nbtText(b));
            break;
         case 108:
            this.title("Tytuł", this.nbtText(b));
            break;
         case 112:
            this.send(14, x -> {});
            this.state = Bot.State.CONFIG;
            this.inWorld = false;
            this.world = null;
            this.positioned = false;
            this.authHold = 0L;
            this.entities.clearAll();
            this.finishSign();
            this.startConfig();
            this.resetInventory();
            this.log(class_2561.method_43470("Serwer przełącza bota (rekonfiguracja)...").method_27692(class_124.field_1080));
            break;
         case 114:
            this.storeCookie(b);
            break;
         case 115:
            class_2561 text = this.nbtText(b);
            if (!b.readBoolean()) {
               this.log(text);
               AutoFlow.watch(this, text);
            }
            break;
         case 119: {
            class_2777 p = (class_2777)class_2777.field_48004.decode(b);
            Entities.Mob m = this.entities.get(p.comp_3237());
            if (m == null) {
               return;
            }

            class_10182 next = class_10182.method_63639(
               new class_10182(new class_243(m.x, m.y, m.z), class_243.field_1353, 0.0F, 0.0F), p.comp_3238(), p.comp_3239()
            );
            this.entities.moveTo(p.comp_3237(), next.comp_3148().field_1352, next.comp_3148().field_1351, next.comp_3148().field_1350);
            break;
         }
         case 122:
            this.transfer(b);
            break;
         case 124: {
            if (this.registries == null) {
               return;
            }

            class_2781 p = (class_2781)class_2781.field_48008.decode(this.reg(b));
            if (p.method_11937() == this.entityId) {
               for (class_2782 ex : p.method_11938()) {
                  this.attribute(ex);
               }
            }
         }
      }
   }

   private void enterWorld(class_8589 info, boolean join) {
      class_2874 type = (class_2874)info.comp_1553().comp_349();
      if (join || this.world == null || !info.comp_1554().equals(this.dimension)) {
         this.world = new World(this.registries, type.comp_651(), type.comp_652());
      }

      this.dimension = info.comp_1554();
      this.spectator = info.comp_1556() == class_1934.field_9219;
      this.entities.clearWorld();
      this.wander.reset();
      Wander.release(this.body);
      this.lastInput = class_10185.field_54098;
      this.lastSprint = false;
      this.calm(40, 90);
      this.body.vx = this.body.vy = this.body.vz = 0.0;
      this.body.onGround = false;
      this.body.horizontalCollision = false;
      if (join) {
         this.body.x = this.body.y = this.body.z = 0.0;
         this.body.yaw = this.body.pitch = 0.0F;
         this.positioned = false;
         this.batch = new class_8760();
      }

      this.lastX = this.body.x;
      this.lastY = this.body.y;
      this.lastZ = this.body.z;
      this.lastYaw = this.body.yaw;
      this.lastPitch = this.body.pitch;
      this.lastOnGround = false;
      this.lastHorizontal = false;
      this.sincePosition = 0;
      this.loadStep = 0;
      this.loaded = false;
      this.loadTicks = 60;
   }

   private void teleport(class_2708 p) throws IOException {
      boolean first = !this.positioned;
      class_10182 current = new class_10182(
         new class_243(this.body.x, this.body.y, this.body.z), new class_243(this.body.vx, this.body.vy, this.body.vz), this.body.yaw, this.body.pitch
      );
      class_10182 next = class_10182.method_63639(current, p.comp_3228(), p.comp_3229());
      this.body.x = next.comp_3148().field_1352;
      this.body.y = next.comp_3148().field_1351;
      this.body.z = next.comp_3148().field_1350;
      this.body.vx = next.comp_3149().field_1352;
      this.body.vy = next.comp_3149().field_1351;
      this.body.vz = next.comp_3149().field_1350;
      this.body.yaw = next.comp_3150();
      this.body.pitch = next.comp_3151();
      this.send(0, x -> x.method_10804(p.comp_3133()));
      this.send(29, x -> {
         x.method_52940(this.body.x);
         x.method_52940(this.body.y);
         x.method_52940(this.body.z);
         x.method_52941(this.body.yaw);
         x.method_52941(this.body.pitch);
         x.method_52997(0);
      });
      this.positioned = true;
      if (first) {
         this.log(class_2561.method_43470("Pozycja: " + this.position()).method_27692(class_124.field_1063));
      } else if (this.body.forward != 0.0F || this.body.jumping) {
         this.wander.interrupt(this.body);
         this.calm(30, 60);
         long now = System.currentTimeMillis();
         this.setbacks.addLast(now);

         while (!this.setbacks.isEmpty() && now - this.setbacks.peekFirst() > 30000L) {
            this.setbacks.pollFirst();
         }

         if (this.setbacks.size() >= 4) {
            this.walkBanUntil = this.tickCount + 1200L;
            this.setbacks.clear();
            this.log(class_2561.method_43470("[Bot] Serwer kilka razy cofnął pozycję - przez minutę tylko się rozglądam.").method_27692(class_124.field_1065));
         }
      }
   }

   private void tick() {
      if (this.state == Bot.State.PLAY && this.inWorld && this.out != null && this.world != null) {
         try {
            this.tickCount++;
            if (!this.loaded) {
               this.loadTicks--;
            }

            if ((this.loaded || this.loadTicks <= 0) && this.positioned) {
               if (autoAfk && this.trust) {
                  this.trustClick();
               }

               if (autoAfk && this.free()) {
                  this.wander.tick(this.body, this.world);
               } else {
                  Wander.release(this.body);
               }

               if (this.tickCount < this.walkBanUntil) {
                  this.body.forward = 0.0F;
                  this.body.jumping = false;
                  this.body.sprinting = false;
               }

               if (!this.spectator) {
                  if (!this.dead && !this.body.climbing(this.world)) {
                     this.entities.pushes(this.body, this.name, this.spectator);
                  }

                  this.body.tick(this.world);
               }

               this.sendInput();
               this.sendMovement();
            }

            if (this.loadStep == 1) {
               int bx = class_3532.method_15357(this.body.x);
               int by = class_3532.method_15357(this.body.y);
               int bz = class_3532.method_15357(this.body.z);
               if (this.world.method_31601(by) || this.dead || this.tickCount - this.world.arrivedAt(bx, bz) >= 3L) {
                  this.loadStep = 2;
               }
            }

            if (this.loadStep == 2 && !this.loaded) {
               this.loaded = true;
               this.send(42, x -> {});
            }

            this.send(11, x -> {});
         } catch (IOException e) {
            this.closeSocket();
         }
      }
   }

   private boolean free() {
      synchronized (this.invLock) {
         if (this.window != null) {
            return false;
         }
      }

      return !this.frozen && !this.dead && !this.trust && this.signEdit == null && this.tickCount >= this.calmUntil && this.tickCount >= this.authHold;
   }

   private void trustClick() throws IOException {
      synchronized (this.invLock) {
         if (this.window != null || this.registries == null) {
            return;
         }
      }

      if (--this.trustWait <= 0) {
         this.trustWait = ThreadLocalRandom.current().nextInt(5, 9);
         if (++this.trustClicks > 40) {
            this.trust = false;
            this.log(class_2561.method_43470("[Bot] Po 20 s klikania PPM nie otworzyło się żadne GUI - przestaję.").method_27692(class_124.field_1065));
         } else {
            this.send(61, class_2886.field_48226, new class_2886(class_1268.field_5808, ++this.sequence, this.body.yaw, this.body.pitch));
         }
      }
   }

   private boolean trustSlot() {
      int slot = -1;
      String items = null;
      synchronized (this.invLock) {
         if (!this.trust || this.window == null || this.window.id() != this.trustWindow || this.window.slots() == null || this.window.slots().length == 0) {
            return false;
         }

         class_1799[] slots = this.window.slots();
         int end = slots.length >= 36 ? slots.length - 36 : slots.length;

         for (int i = 0; i < end && slot < 0; i++) {
            if (slots[i].method_31574(class_1802.field_8264)) {
               slot = i;
            }
         }

         if (slot >= 0) {
            this.trust = false;
         } else {
            if (this.trustListed) {
               return false;
            }

            this.trustListed = true;
            StringBuilder s = new StringBuilder();

            for (int i = 0; i < end; i++) {
               if (!slots[i].method_7960()) {
                  s.append(s.isEmpty() ? "" : ", ").append(i).append("=").append(class_7923.field_41178.method_10221(slots[i].method_7909()).method_12832());
               }
            }

            items = s.toString();
         }
      }

      if (slot < 0) {
         this.log(class_2561.method_43470("[Bot] W GUI nie ma czerwonego barwnika, czekam: " + items).method_27692(class_124.field_1065));
         return false;
      } else {
         this.click(slot, 0, class_1713.field_7790);
         this.log(class_2561.method_43470("[Bot] Klikam czerwony barwnik (slot " + slot + ").").method_27692(class_124.field_1060));
         return true;
      }
   }

   private void calm(int min, int max) {
      this.calmUntil = Math.max(this.calmUntil, this.tickCount + ThreadLocalRandom.current().nextInt(min, max));
   }

   private void sendInput() throws IOException {
      class_10185 input = new class_10185(
         this.body.forward > 0.0F,
         this.body.forward < 0.0F,
         this.body.sideways > 0.0F,
         this.body.sideways < 0.0F,
         this.body.jumping,
         false,
         this.body.sprinting
      );
      if (!input.equals(this.lastInput)) {
         this.lastInput = input;
         this.send(41, class_2851.field_48209, new class_2851(input));
      }
   }

   private void attribute(class_2782 e) {
      double d = e.comp_2178();
      List<class_1322> mods = e.comp_2179().stream().filter(mx -> !mx.comp_2447().equals(SPRINT_BOOST)).toList();

      for (class_1322 m : mods) {
         if (m.comp_2450() == class_1323.field_6328) {
            d += m.comp_2449();
         }
      }

      double v = d;

      for (class_1322 m : mods) {
         if (m.comp_2450() == class_1323.field_6330) {
            v += d * m.comp_2449();
         }
      }

      for (class_1322 m : mods) {
         if (m.comp_2450() == class_1323.field_6331) {
            v *= 1.0 + m.comp_2449();
         }
      }

      class_6880<class_1320> a = e.comp_2177();
      v = ((class_1320)a.comp_349()).method_6165(v);
      if (a.method_40230().equals(class_5134.field_23719.method_40230())) {
         this.body.speed = v;
      } else if (a.method_40230().equals(class_5134.field_23728.method_40230())) {
         this.body.jumpStrength = v;
      } else if (a.method_40230().equals(class_5134.field_47761.method_40230())) {
         this.body.stepHeight = v;
      } else if (a.method_40230().equals(class_5134.field_49078.method_40230())) {
         this.body.gravity = v;
      }
   }

   private void sendMovement() throws IOException {
      if (this.body.sprinting != this.lastSprint) {
         this.lastSprint = this.body.sprinting;
         this.send(40, x -> {
            x.method_10804(this.entityId);
            x.method_10804(this.lastSprint ? 3 : 4);
            x.method_10804(0);
         });
      }

      double dx = this.body.x - this.lastX;
      double dy = this.body.y - this.lastY;
      double dz = this.body.z - this.lastZ;
      double dyaw = this.body.yaw - this.lastYaw;
      double dpitch = this.body.pitch - this.lastPitch;
      this.sincePosition++;
      boolean moved = dx * dx + dy * dy + dz * dz > 4.0E-8 || this.sincePosition >= 20;
      boolean rotated = dyaw != 0.0 || dpitch != 0.0;
      int flags = (this.body.onGround ? 1 : 0) | (this.body.horizontalCollision ? 2 : 0);
      if (moved && rotated) {
         this.send(29, x -> {
            x.method_52940(this.body.x);
            x.method_52940(this.body.y);
            x.method_52940(this.body.z);
            x.method_52941(this.body.yaw);
            x.method_52941(this.body.pitch);
            x.method_52997(flags);
         });
      } else if (moved) {
         this.send(28, x -> {
            x.method_52940(this.body.x);
            x.method_52940(this.body.y);
            x.method_52940(this.body.z);
            x.method_52997(flags);
         });
      } else if (rotated) {
         this.sendLook(true);
      } else if (this.lastOnGround != this.body.onGround || this.lastHorizontal != this.body.horizontalCollision) {
         this.send(31, x -> x.method_52997(flags));
      }

      if (moved) {
         this.lastX = this.body.x;
         this.lastY = this.body.y;
         this.lastZ = this.body.z;
         this.sincePosition = 0;
      }

      if (rotated) {
         this.lastYaw = this.body.yaw;
         this.lastPitch = this.body.pitch;
      }

      this.lastOnGround = this.body.onGround;
      this.lastHorizontal = this.body.horizontalCollision;
   }

   private void sendLook(boolean real) throws IOException {
      int flags = real ? (this.body.onGround ? 1 : 0) | (this.body.horizontalCollision ? 2 : 0) : 0;
      this.send(30, x -> {
         x.method_52941(this.body.yaw);
         x.method_52941(this.body.pitch);
         x.method_52997(flags);
      });
   }

   private void checkAnvil() throws IOException {
      class_1799 stack;
      synchronized (this.invLock) {
         if (this.window == null || this.window.type() != class_3917.field_17329 || this.window.slots() == null || this.window.slots().length == 0) {
            return;
         }

         stack = this.window.slots()[0];
         if (stack.method_7960() || !class_1890.method_57529(stack) || class_1799.method_7973(stack, this.anvilTracked)) {
            return;
         }

         this.anvilTracked = stack.method_7972();
      }

      String shown = this.packs.translate(stack.method_7964());
      String name = stack.method_57826(class_9334.field_49631) ? shown : "";
      String clean = class_3544.method_57180(name);
      if (clean.length() <= 50 && !clean.equals(this.anvilName)) {
         this.anvilName = clean;
         this.send(46, class_2855.field_48212, new class_2855(name));
      }
   }

   private void openSign(class_2693 p) {
      World.Sign sign = this.world == null ? null : this.world.sign(p.method_11677());
      if (sign != null) {
         this.finishSign();
         boolean filtered = class_310.method_1551().method_33883();
         class_8242 text = p.method_49995() ? sign.front() : sign.back();
         String[] lines = new String[4];

         for (int i = 0; i < 4; i++) {
            lines[i] = this.packs.translate(text.method_49859(i, filtered));
         }

         this.signEdit = new Bot.SignEdit(p.method_11677(), p.method_49995(), lines);
         this.log(class_2561.method_43470("[GUI] Otwarto edycję tabliczki.").method_27692(class_124.field_1075));
         int g = this.gen;
         this.signTimeout = this.worker.schedule(() -> {
            if (g == this.gen) {
               this.finishSign();
            }
         }, ThreadLocalRandom.current().nextLong(3000L, 7000L), TimeUnit.MILLISECONDS);
      }
   }

   private void finishSign() {
      Bot.SignEdit edit = this.signEdit;
      if (edit != null) {
         this.signEdit = null;
         if (this.signTimeout != null) {
            this.signTimeout.cancel(false);
         }

         if (this.state == Bot.State.PLAY) {
            try {
               this.send(
                  57, class_2877.field_48222, new class_2877(edit.pos(), edit.front(), edit.lines()[0], edit.lines()[1], edit.lines()[2], edit.lines()[3])
               );
            } catch (IOException var3) {
            }
         }
      }
   }

   private void playerChat(class_2540 b) {
      b.method_10790();
      b.method_10816();
      if (b.readBoolean()) {
         b.method_52994(256);
      }

      String content = b.method_10800(256);
      b.readLong();
      b.readLong();
      int seen = b.method_10816();

      for (int i = 0; i < seen; i++) {
         if (b.method_10816() == 0) {
            b.method_52994(256);
         }
      }

      class_2561 unsigned = b.readBoolean() ? this.nbtText(b) : null;
      if (b.method_10816() == 2) {
         b.method_33558();
      }

      class_2561 message = (class_2561)(unsigned != null ? unsigned : class_2561.method_43470(content));
      int type = b.method_10816();
      if (type == 0) {
         this.log(message);
      } else {
         class_2561 sender = this.nbtText(b);
         class_2561 target = b.readBoolean() ? this.nbtText(b) : null;
         this.log(this.decorate(type, sender, target, message));
      }
   }

   private class_2561 decorate(int type, class_2561 sender, class_2561 target, class_2561 message) {
      String kind = type - 1 < this.chatTypes.size() ? this.chatTypes.get(type - 1) : "minecraft:chat";
      class_5250 t = class_2561.method_43473();
      switch (kind) {
         case "minecraft:say_command":
            t.method_27693("[").method_10852(sender).method_27693("] ").method_10852(message);
            break;
         case "minecraft:emote_command":
            t.method_27693("* ").method_10852(sender).method_27693(" ").method_10852(message);
            break;
         case "minecraft:msg_command_incoming":
            t.method_10852(
                  class_2561.method_43473()
                     .method_10852(sender)
                     .method_27693(" szepcze do bota: ")
                     .method_27695(new class_124[]{class_124.field_1080, class_124.field_1056})
               )
               .method_10852(message);
            break;
         case "minecraft:msg_command_outgoing":
            t.method_10852(
                  class_2561.method_43470("Bot szepcze do ")
                     .method_10852((class_2561)(target == null ? class_2561.method_43473() : target))
                     .method_27693(": ")
                     .method_27695(new class_124[]{class_124.field_1080, class_124.field_1056})
               )
               .method_10852(message);
            break;
         case "minecraft:team_msg_command_incoming":
         case "minecraft:team_msg_command_outgoing":
            t.method_10852((class_2561)(target == null ? class_2561.method_43473() : target))
               .method_27693(" <")
               .method_10852(sender)
               .method_27693("> ")
               .method_10852(message);
            break;
         default:
            t.method_27693("<").method_10852(sender).method_27693("> ").method_10852(message);
      }

      return t;
   }

   private void title(String label, class_2561 text) {
      if (!text.getString().isBlank()) {
         this.log(class_2561.method_43470("[" + label + "] ").method_27692(class_124.field_1065).method_10852(text));
         AutoFlow.watch(this, text);
      }
   }

   public void watch(class_2561 text) {
      String plain = class_124.method_539(text.getString());
      if (plain != null) {
         String lower = plain.toLowerCase(Locale.ROOT);
         if (lower.contains("regulamin")) {
            this.rulesSeen = true;
         }

         if (lower.contains("trwa przenoszenie") && !this.frozen) {
            this.frozen = true;
            this.log(class_2561.method_43470("[Bot] Przenoszenie - od teraz bot stoi w miejscu.").method_27692(class_124.field_1065));
         }

         if (lower.contains("zweryfikowano czynnik zaufania") && !this.trust) {
            this.trust = true;
            this.trustClicks = 0;
            this.trustWindow = -1;
            this.trustWait = 0;
            this.trustListed = false;
            this.log(class_2561.method_43470("[Bot] Zweryfikowano - klikam PPM aż otworzy się GUI.").method_27692(class_124.field_1060));
         }

         if (lower.contains("pomyślnie") || lower.contains("zalogowan") || lower.contains("zarejestrowan")) {
            this.authHold = 0L;
         }

         if (!this.rulesClicked) {
            boolean register = lower.contains("/register");
            if (register || lower.contains("/login")) {
               this.authHold = Math.max(this.authHold, this.tickCount + 300L);
               long now = System.currentTimeMillis();
               if (autoLogin && this.authTries < 3 && now - this.lastAuth >= 10000L) {
                  this.lastAuth = now;
                  this.authTries++;
                  String code = null;
                  Matcher m = CODE.matcher(plain);

                  while (m.find()) {
                     code = m.group();
                  }

                  String cmd = register ? "/register eloelo" + (code != null ? " " + code : "") : "/login eloelo";
                  this.worker.schedule(() -> this.chat(cmd), 1800 + ThreadLocalRandom.current().nextInt(1600), TimeUnit.MILLISECONDS);
               }
            }
         }
      }
   }

   private void checkRules() {
      if (!autoAfk) {
         return;
      }

      int slot = -1;
      int id;
      synchronized (this.invLock) {
         if (this.rulesClicked || this.window == null || this.window.slots() == null) {
            return;
         }

         String title = class_124.method_539(this.window.title().getString());
         if (!this.rulesSeen && (title == null || !title.toLowerCase(Locale.ROOT).contains("regulamin"))) {
            return;
         }

         class_1799[] slots = this.window.slots();
         int end = slots.length >= 36 ? slots.length - 36 : slots.length;

         for (int i = 0; i < end && slot < 0; i++) {
            if (slots[i].method_31574(class_1802.field_8120)) {
               slot = i;
            }
         }

         for (int i = 0; i < end && slot < 0; i++) {
            if (slots[i].method_31574(class_1802.field_8839)) {
               slot = i;
            }
         }

         if (slot < 0) {
            return;
         }

         id = this.window.id();
         this.rulesClicked = true;
      }

      int s = slot;
      this.log(class_2561.method_43470("[Bot] Akceptuję regulamin...").method_27692(class_124.field_1060));
      this.worker.schedule(() -> this.clickRules(id, s), 1100 + ThreadLocalRandom.current().nextInt(1200), TimeUnit.MILLISECONDS);
   }

   private void clickRules(int id, int slot) {
      synchronized (this.invLock) {
         if (this.window == null || this.window.id() != id) {
            return;
         }
      }

      this.click(slot, 0, class_1713.field_7790);
      this.worker.schedule(() -> {
         synchronized (this.invLock) {
            if (this.window == null || this.window.id() != id || ++this.rulesTries >= 3) {
               return;
            }
         }

         this.clickRules(id, slot);
      }, 3000L, TimeUnit.MILLISECONDS);
   }

   private void resourcePack(class_2540 b) {
      UUID id = b.method_10790();
      String url = b.method_19772();
      String hash = b.method_10800(40);
      boolean required = b.readBoolean();
      this.log(class_2561.method_43470("Serwer wysłał paczkę zasobów" + (required ? " (wymaganą)" : "") + " - pobieram ją.").method_27692(class_124.field_1063));
      this.packs.push(id, url, hash);
   }

   private void packStatus(UUID id, int status) {
      int g = this.gen;
      this.worker.execute(() -> {
         if (g == this.gen) {
            int packet = this.state == Bot.State.CONFIG ? 6 : (this.state == Bot.State.PLAY ? 47 : -1);
            if (packet >= 0) {
               try {
                  this.send(packet, x -> {
                     x.method_10797(id);
                     x.method_10804(status);
                  });
               } catch (IOException var6) {
               }

               if (status == 0) {
                  this.log(class_2561.method_43470("Paczka zasobów wczytana.").method_27692(class_124.field_1063));
               }

               if (status == 2) {
                  this.log(class_2561.method_43470("Nie udało się pobrać paczki zasobów.").method_27692(class_124.field_1061));
               }
            }
         }
      });
   }

   private void cookie(int reply, class_2540 b) throws IOException {
      class_2960 key = b.method_10810();
      byte[] value = this.cookies.get(key);
      this.send(reply, x -> {
         x.method_10812(key);
         x.method_52964(value != null);
         if (value != null) {
            x.method_10813(value);
         }
      });
   }

   private void storeCookie(class_2540 b) {
      class_2960 key = b.method_10810();
      this.cookies.put(key, b.method_10803(5120));
   }

   private void transfer(class_2540 b) {
      String host = b.method_19772();
      int port = b.method_10816();
      this.transfer = new class_639(host, port);
      this.gen++;
      this.closeSocket();
   }

   private void sendClientInfo() throws IOException {
      this.send(2, x -> {
         x.method_10812(class_2960.method_60656("brand"));
         x.method_10814("vanilla");
      });

      class_8791 options;
      try {
         options = class_310.method_1551().field_1690.method_53842();
      } catch (Exception e) {
         options = class_8791.method_53821();
      }

      class_8791 o = options;
      this.send(0, o::method_53822);
   }

   private void kicked(class_2561 reason) throws IOException {
      this.log(class_2561.method_43470("Wyrzucono z serwera: ").method_27692(class_124.field_1061).method_10852(reason));
      this.stopping = true;
      throw new EOFException("kick");
   }

   private class_2561 nbtText(class_2540 b) {
      class_2520 nbt = b.method_30616(class_2505.method_53898());
      return (class_2561)(nbt == null
         ? class_2561.method_43473()
         : class_8824.field_46597
            .parse(class_6903.method_46632(class_2509.field_11560, this.lookup()), nbt)
            .result()
            .orElseGet(() -> class_2561.method_43470(nbt.method_10714())));
   }

   private class_2561 jsonText(String json) {
      try {
         return class_8824.field_46597
            .parse(class_6903.method_46632(JsonOps.INSTANCE, this.lookup()), JsonParser.parseString(json))
            .result()
            .orElseGet(() -> class_2561.method_43470(json));
      } catch (Exception e) {
         return class_2561.method_43470(json);
      }
   }

   private class_7874 lookup() {
      class_5455 r = this.registries;
      return (class_7874)(r != null ? r : ops());
   }

   private static synchronized class_7874 ops() {
      if (lookup == null) {
         lookup = class_7887.method_46817();
      }

      return lookup;
   }

   private class_2540 read() throws IOException {
      int length = this.readVarInt();
      byte[] data = new byte[length];
      this.in.readFully(data);
      class_2540 b = new class_2540(Unpooled.wrappedBuffer(data));
      if (this.threshold < 0) {
         return b;
      }

      int size = b.method_10816();
      if (size == 0) {
         return new class_2540(Unpooled.wrappedBuffer(data, b.readerIndex(), data.length - b.readerIndex()));
      }

      byte[] raw = new byte[size];
      this.inflater.setInput(data, b.readerIndex(), data.length - b.readerIndex());

      try {
         int off = 0;

         while (off < size) {
            int n = this.inflater.inflate(raw, off, size - off);
            if (n == 0 && (this.inflater.needsInput() || this.inflater.finished())) {
               throw new IOException("uszkodzony pakiet");
            }

            off += n;
         }
      } catch (DataFormatException e) {
         throw new IOException("uszkodzony pakiet", e);
      } finally {
         this.inflater.reset();
      }

      return new class_2540(Unpooled.wrappedBuffer(raw));
   }

   private int readVarInt() throws IOException {
      int value = 0;

      for (int i = 0; i < 5; i++) {
         int k = this.in.readUnsignedByte();
         value |= (k & 127) << i * 7;
         if ((k & 128) == 0) {
            return value;
         }
      }

      throw new IOException("za długi VarInt");
   }

   private void send(int id, Consumer<class_2540> writer) throws IOException {
      class_2540 b = new class_2540(Unpooled.buffer());
      b.method_10804(id);
      writer.accept(b);
      byte[] body = new byte[b.readableBytes()];
      b.method_52979(body);
      synchronized (this.writeLock) {
         if (this.out == null) {
            throw new SocketException("brak połączenia");
         }

         class_2540 frame = new class_2540(Unpooled.buffer());
         if (this.threshold < 0) {
            frame.method_52983(body);
         } else if (body.length < this.threshold) {
            frame.method_10804(0);
            frame.method_52983(body);
         } else {
            frame.method_10804(body.length);
            frame.method_52983(this.deflate(body));
         }

         class_2540 full = new class_2540(Unpooled.buffer());
         full.method_10804(frame.readableBytes());
         full.method_52975(frame);
         byte[] bytes = new byte[full.readableBytes()];
         full.method_52979(bytes);
         this.out.write(bytes);
         this.out.flush();
      }
   }

   private <T> void send(int id, class_9139<? super class_9129, T> codec, T packet) throws IOException {
      class_5455 r = this.registries != null ? this.registries : class_7756.method_45738().method_45926();
      this.send(id, b -> codec.encode(new class_9129(b, r), packet));
   }

   private byte[] deflate(byte[] body) {
      this.deflater.setInput(body);
      this.deflater.finish();
      ByteArrayOutputStream result = new ByteArrayOutputStream(body.length / 2 + 16);
      byte[] buffer = new byte[8192];

      while (!this.deflater.finished()) {
         result.write(buffer, 0, this.deflater.deflate(buffer));
      }

      this.deflater.reset();
      return result.toByteArray();
   }

   private void closeSocket() {
      Socket s;
      synchronized (this.writeLock) {
         this.out = null;
         s = this.socket;
      }

      try {
         if (s != null) {
            s.close();
         }
      } catch (IOException var4) {
      }
   }

   private static String describe(Exception e) {
      if (e instanceof SocketTimeoutException) {
         return "przekroczono czas oczekiwania";
      }

      if (e instanceof EOFException) {
         return "serwer zamknął połączenie";
      }

      if (e instanceof UnknownHostException) {
         return "nieznany host";
      }

      if (e instanceof ConnectException) {
         return "nie można połączyć (" + e.getMessage() + ")";
      }

      String msg = e.getMessage();
      return msg == null ? e.getClass().getSimpleName() : msg;
   }

   public void log(class_2561 text) {
      long now = System.currentTimeMillis();
      synchronized (this.lines) {
         this.lines.add(new Bot.Line(now, text));
         if (this.lines.size() > 1000) {
            this.lines.subList(0, this.lines.size() - 1000).clear();
         }
      }

      this.version++;
      this.writeFile(now, text.getString());
   }

   private void writeFile(long time, String line) {
      synchronized (Bot.class) {
         try {
            if (file == null) {
               Path path = class_310.method_1551().field_1697.toPath().resolve("logs").resolve("easybot.log");
               Files.createDirectories(path.getParent());
               file = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }

            file.write("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time)) + "] [" + this.name + "] " + line.replace("\n", "\n    "));
            file.newLine();
            file.flush();
         } catch (IOException var7) {
         }
      }
   }

   private interface Action {
      void run() throws IOException;
   }

   public record Inventory(class_1799[] player, Bot.Window window, class_1799 cursor, int selected, boolean using, class_5455 registries) {
   }

   public record Line(long time, class_2561 text) {
   }

   private record SignEdit(class_2338 pos, boolean front, String[] lines) {
   }

   public enum State {
      CONNECTING("Łączenie", -865972),
      LOGIN("Logowanie", -865972),
      CONFIG("Konfiguracja", -878004),
      PLAY("Na serwerze", -13643391),
      OFFLINE("Offline", -1090725);

      public final String label;
      public final int color;

      State(String label, int color) {
         this.label = label;
         this.color = color;
      }
   }

   public record Window(int id, class_3917<?> type, class_2561 title, class_1799[] slots) {
   }
}
