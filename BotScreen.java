package pl.easybot.gui;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_124;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_5481;
import net.minecraft.class_7919;
import net.minecraft.class_7923;
import net.minecraft.class_1792.class_9635;
import pl.easybot.EasyBotClient;
import pl.easybot.ErrorsClient;
import pl.easybot.bot.Bot;

public class BotScreen extends class_437 {
   private static final int HEADER = 30;
   private static final int LINE_H = 10;
   private static final int SLOT = 18;
   private static final int GRID = 162;
   private static final int TILE_HEAD = 16;
   private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
   private static final List<String> history = new ArrayList<>();
   private static String draft = "";
   private static int scroll;
   private static int tab;
   private static boolean toAll = true;
   private static int mainScroll;
   private final class_437 parent;
   private class_342 address;
   private class_342 countField;
   private final List<class_342> nickFields = new ArrayList<>();
   private class_342 input;
   private PillButton holdButton;
   private PillButton closeButton;
   private int historyPos;
   private int px;
   private int py;
   private int pw;
   private int ph;
   private int ax;
   private int ay;
   private int aw;
   private int ah;
   private int addrW;
   private int countX;
   private int tabsY;
   private int pickerX = -1;
   private final List<BotScreen.Tile> tiles = new ArrayList<>();
   private BotScreen.Tile main;
   private long mainSig = -1L;
   private int mainWidth = -1;
   private List<BotScreen.WLine> mainLines = List.of();
   private final Map<Integer, BotScreen.Cache> caches = new HashMap<>();
   private List<BotScreen.WLine> wrapped = List.of();
   private final Map<BotScreen.WLine, int[]> offsets = new IdentityHashMap<>();
   private int cx;
   private int cy;
   private int cw;
   private int ch;
   private int sx;
   private int sy;
   private int sw;
   private int sh;
   private float ks = 1.0F;
   private int drawStart;
   private int drawEnd;
   private int drawY0;
   private int[] selA;
   private int[] selB;
   private boolean selecting;
   private long lastConsoleClick;
   private long copiedAt;
   private boolean rebuild;
   private final List<BotScreen.Hot> hots = new ArrayList<>();
   private int hoveredSlot = -1;
   private class_1799 hoveredStack = class_1799.field_8037;
   private long lastSlotClick;
   private int lastSlot = -1;
   private int leftX;
   private int rightX;
   private int midX;
   private int midW;

   public BotScreen(class_437 parent) {
      super(class_2561.method_43470("EasyBot"));
      this.parent = parent;
   }

   public boolean method_25421() {
      return false;
   }

   private static Bot bot() {
      return EasyBotClient.bot();
   }

   private static boolean online() {
      return bot() != null && bot().state() == Bot.State.PLAY;
   }

   private static int count() {
      return EasyBotClient.count;
   }

   protected void method_25426() {
      this.pw = Math.min(560, this.field_22789 - 16);
      this.ph = Math.min(320, this.field_22790 - 16);
      this.px = (this.field_22789 - this.pw) / 2;
      this.py = (this.field_22790 - this.ph) / 2;
      int rowY = this.py + 30 + 8;
      int clearX = this.px + this.pw - 8 - 62;
      int stopX = clearX - 6 - 70;
      int sendX = stopX - 6 - 96;
      this.countX = sendX - 6 - 54;
      this.addrW = this.countX - 6 - (this.px + 8);
      this.address = new class_342(this.field_22793, this.px + 14, rowY + 6, this.addrW - 12, 10, class_2561.method_43470("Adres"));
      this.address.method_1858(false);
      this.address.method_1880(128);
      this.address.method_1868(Theme.TEXT);
      this.address.method_1852(EasyBotClient.address);
      this.address.method_1863(v -> EasyBotClient.address = v.strip());
      this.address.method_47404(class_2561.method_43470("Adres serwera, np. localhost:25565").method_54663(Theme.TEXT_FAINT));
      this.method_37063(this.address);
      this.countField = new class_342(this.field_22793, this.countX + 34, rowY + 6, 16, 10, class_2561.method_43470("Ilość botów"));
      this.countField.method_1858(false);
      this.countField.method_1880(2);
      this.countField.method_1868(Theme.TEXT);
      this.countField.method_1890(s -> s.matches("\\d*"));
      this.countField.method_1852(String.valueOf(count()));
      this.countField.method_1863(v -> {
         if (!v.isEmpty()) {
            int n = Math.clamp(Integer.parseInt(v), 1, 50);
            if (n != count()) {
               EasyBotClient.setCount(n);
               this.rebuild = true;
            }
         }
      });
      this.countField.method_47400(class_7919.method_47407(class_2561.method_43470("Ile botów ma wejść (1-50)")));
      this.method_37063(this.countField);
      this.method_37063(new PillButton(sendX, rowY, 96, 20, count() > 1 ? "Wyślij boty" : "Wyślij bota", Theme.ACCENT, EasyBotClient::canLaunch, this::launch));
      this.method_37063(new PillButton(stopX, rowY, 70, 20, "Rozłącz", Theme.RED, EasyBotClient::anyBusy, EasyBotClient::disconnectAll));
      this.method_37063(new PillButton(clearX, rowY, 62, 20, "Wyczyść", Theme.TEXT_DIM, false, () -> tab == 0, () -> {
         for (int i = 0; i < count(); i++) {
            if (EasyBotClient.bots[i] != null) {
               EasyBotClient.bots[i].clear();
            }
         }

         scroll = 0;
         this.selA = this.selB = null;
      }));
      this.tabsY = rowY + 20 + 6;
      int afkX = this.px + this.pw - 8 - 78;
      int loginX = afkX - 4 - 78;
      PillButton loginBtn = new PillButton(
         loginX, this.tabsY, 78, 12, "Auto login: " + (Bot.autoLogin ? "ON" : "OFF"), Theme.ACCENT, Bot.autoLogin, () -> true, () -> {
            Bot.autoLogin = !Bot.autoLogin;
            this.rebuild = true;
         }
      );
      loginBtn.method_47400(class_7919.method_47407(class_2561.method_43470("Wpisuje tylko /register albo /login, gdy serwer o to prosi")));
      this.method_37063(loginBtn);
      PillButton afkBtn = new PillButton(
         afkX, this.tabsY, 78, 12, "Auto AFK: " + (Bot.autoAfk ? "ON" : "OFF"), Theme.ACCENT, Bot.autoAfk, () -> true, () -> {
            Bot.autoAfk = !Bot.autoAfk;
            this.rebuild = true;
         }
      );
      afkBtn.method_47400(class_7919.method_47407(class_2561.method_43470("ON: bot robi wszystko sam (regulamin, warp afk, chodzenie). OFF: stoi i nic sam nie wpisuje")));
      this.method_37063(afkBtn);
      PillButton themeBtn = new PillButton(
         loginX - 4 - 100, this.tabsY, 100, 12, "Motyw: " + Theme.name(), Theme.GOLD, false, () -> true, () -> {
            Theme.next();
            this.rebuild = true;
         }
      );
      themeBtn.method_47400(class_7919.method_47407(class_2561.method_43470("Zmień motyw kolorów (Noc, Granat, Fiolet, Różany, Bursztyn, Lód, AMOLED)")));
      this.method_37063(themeBtn);
      this.ax = this.px + 8;
      this.ay = this.tabsY + 16;
      this.aw = this.pw - 16;
      this.ah = this.py + this.ph - 8 - this.ay;
      this.pickerX = -1;
      if (count() > 1) {
         this.pickerX = this.tabX(1) + this.field_22793.method_1727(this.tabLabel(1)) + 24;
         this.method_37063(new PillButton(this.pickerX, this.tabsY, 14, 12, "<", Theme.TEXT_DIM, false, () -> true, () -> this.pick(-1)));
         this.method_37063(new PillButton(this.pickerX + 18 + 96, this.tabsY, 14, 12, ">", Theme.TEXT_DIM, false, () -> true, () -> this.pick(1)));
      }

      this.nickFields.clear();
      this.tiles.clear();
      if (tab == 0) {
         this.initConsole();
      } else {
         this.initGui();
      }

      this.method_48265(
         (class_364)(tab == 0 && online()
            ? this.input
            : (tab == 0 && !this.nickFields.isEmpty() ? (class_364)this.nickFields.get(EasyBotClient.selected) : this.address))
      );
      this.historyPos = history.size();
   }

   private void initConsole() {
      this.holdButton = null;
      this.closeButton = null;
      int inputY = this.py + this.ph - 8 - 20;
      int toggleW = count() > 1 ? 92 : 0;
      int inputW = this.aw - 80 - (toggleW > 0 ? toggleW + 6 : 0);
      this.input = new class_342(this.field_22793, this.px + 14, inputY + 6, inputW - 12, 10, class_2561.method_43470("Czat"));
      this.input.method_1858(false);
      this.input.method_1880(256);
      this.input.method_1868(Theme.TEXT);
      this.input.method_1852(draft);
      this.input.method_1863(v -> draft = v);
      this.input
         .method_47404(
            class_2561.method_43470(count() > 1 && toAll ? "Komenda lub wiadomość do wszystkich botów" : "Napisz jako bot albo wpisz /komendę i Enter")
               .method_54663(Theme.TEXT_FAINT)
         );
      this.method_37063(this.input);
      if (toggleW > 0) {
         PillButton toggle = new PillButton(
            this.ax + inputW + 6, inputY, toggleW, 20, toAll ? "Do wszystkich" : "Do wybranego", Theme.GOLD, false, () -> true, () -> {
               toAll = !toAll;
               this.rebuild = true;
            }
         );
         toggle.method_47400(class_7919.method_47407(class_2561.method_43470("Czy wiadomość z pola czatu wysyłają wszystkie boty, czy tylko zaznaczony")));
         this.method_37063(toggle);
      }

      this.method_37063(
         new PillButton(
            this.px + this.pw - 8 - 74, inputY, 74, 20, "Wyślij", Theme.ACCENT, () -> anyOnline() && !this.input.method_1882().isBlank(), this::sendChat
         )
      );
      int area = this.ah - 26;
      this.main = null;
      if (count() > 1) {
         int mh = Math.clamp((int)(area * 0.38), 60, 130);
         area -= mh + 4;
         this.main = new BotScreen.Tile(this.ax, this.ay + area + 4, this.aw, mh, mh >= 90 ? 1.0F : 0.5F);
         PillButton all = new PillButton(
            this.ax + this.aw - 4 - 124, this.main.y() + 2, 124, 12, "Wyrzuć eq wszystkim", Theme.RED, false, BotScreen::anyOnline, this::throwAllBots
         );
         all.method_47400(class_7919.method_47407(class_2561.method_43470("Każdy bot na serwerze wyrzuca cały ekwipunek, zbroję i drugą rękę")));
         this.method_37063(all);
      }

      this.layoutTiles(this.ax, this.ay, this.aw, area);

      for (int i = 0; i < count(); i++) {
         BotScreen.Tile t = this.tiles.get(i);
         int fw = Math.max(8, t.w() - 22);
         class_342 f = new class_342(this.field_22793, t.x() + 5, t.y() + 4, fw, 10, class_2561.method_43470("Nick " + (i + 1)));
         f.method_1858(false);
         f.method_1880(16);
         f.method_1868(Theme.TEXT);
         f.method_1860(Theme.TEXT);
         f.method_1890(s -> s.matches("[A-Za-z0-9_]*"));
         f.method_1852(NickSync.initial(f, i));
         int index = i;
         f.method_1863(NickSync::onNick);
         f.method_47404(class_2561.method_43470(count() > 1 ? "Nick " + (i + 1) : "Nick bota").method_54663(Theme.TEXT_FAINT));
         this.nickFields.add(f);
         this.method_37063(f);
      }
   }

   private void throwAllBots() {
      for (int i = 0; i < count(); i++) {
         Bot b = EasyBotClient.bots[i];
         if (b != null && b.state() == Bot.State.PLAY) {
            b.throwAll();
         }
      }
   }

   private void mainWindow(class_332 c, int mx, int my) {
      BotScreen.Tile t = this.main;
      Draw.box(c, t.x(), t.y(), t.w(), t.h(), Theme.FIELD, t.in(mx, my) ? Theme.OUTLINE_HOVER : Theme.OUTLINE);
      c.method_25294(t.x() + 1, t.y() + 16 - 1, t.x() + t.w() - 1, t.y() + 16, Theme.OUTLINE);
      c.method_51433(
         this.field_22793,
         Draw.trim(this.field_22793, "GŁÓWNE OKNO · wszystkie boty (" + EasyBotClient.online() + "/" + count() + " online)", t.w() - 140),
         t.x() + 5,
         t.y() + 4,
         Theme.TEXT_FAINT,
         false
      );
      float s = t.scale();
      int innerW = (int)((t.w() - 2) / s) - 14;
      long sig = count();

      for (int i = 0; i < count(); i++) {
         Bot b = EasyBotClient.bots[i];
         sig = sig * 31L + (b == null ? 0 : System.identityHashCode(b) + b.version());
      }

      if (sig != this.mainSig || innerW != this.mainWidth) {
         int before = this.mainLines.size();
         this.mainSig = sig;
         this.mainWidth = innerW;
         this.mainLines = this.merged(innerW);
         if (mainScroll > 0) {
            mainScroll = Math.max(0, mainScroll + this.mainLines.size() - before);
         }
      }

      int bx = t.x() + 1;
      int by = t.textY();
      int bw = t.w() - 2;
      int bh = t.textH() - 1;
      int lh = (int)(bh / s);
      int visible = Math.max(1, (lh - 6) / 10);
      mainScroll = Math.clamp(mainScroll, 0, Math.max(0, this.mainLines.size() - visible));
      if (this.mainLines.isEmpty()) {
         this.center(c, "Tu widać, co dzieje się ze wszystkimi botami naraz.", "Pole niżej wysyła wiadomość albo /komendę od wszystkich botów.", bx, by, bw, bh);
      } else {
         int end = this.mainLines.size() - mainScroll;
         int start = Math.max(0, end - visible);
         int y = by + 3 + (visible - (end - start)) * 10;
         c.method_44379(bx, by, bx + bw, by + bh);
         c.method_51448().method_22903();
         c.method_51448().method_46416(bx, by, 0.0F);
         c.method_51448().method_22905(s, s, 1.0F);
         c.method_51448().method_46416(-bx, -by, 0.0F);

         for (int k = start; k < end; k++) {
            c.method_51430(this.field_22793, this.mainLines.get(k).text(), bx + 4, y, Theme.TEXT, false);
            y += 10;
         }

         int lw = (int)(bw / s);
         if (this.mainLines.size() > visible) {
            int trackH = lh - 4;
            int thumbH = Math.max(12, trackH * visible / this.mainLines.size());
            int max = Math.max(1, this.mainLines.size() - visible);
            int thumbY = by + 2 + (trackH - thumbH) * (max - mainScroll) / max;
            c.method_25294(bx + lw - 4, thumbY, bx + lw - 2, thumbY + thumbH, mainScroll > 0 ? Theme.TEXT_DIM : Theme.OUTLINE_HOVER);
         }

         c.method_51448().method_22909();
         c.method_44380();
      }
   }

   private List<BotScreen.WLine> merged(int innerW) {
      List<BotScreen.Entry> all = new ArrayList<>();

      for (int i = 0; i < count(); i++) {
         Bot b = EasyBotClient.bots[i];
         if (b != null) {
            for (Bot.Line line : ErrorsClient.lines(b)) {
               all.add(new BotScreen.Entry(line.time(), i, line.text()));
            }
         }
      }

      all.sort((p, q) -> Long.compare(p.time(), q.time()));
      List<BotScreen.Entry> tail = all.subList(Math.max(0, all.size() - 400), all.size());
      List<BotScreen.WLine> out = new ArrayList<>();

      for (BotScreen.Entry e : tail) {
         class_2561 full = class_2561.method_43470(TIME.format(Instant.ofEpochMilli(e.time())) + " ")
            .method_54663(Theme.TEXT_FAINT)
            .method_10852(class_2561.method_43470(EasyBotClient.bots[e.bot()].name + " » ").method_54663(Theme.ACCENT))
            .method_10852(class_2561.method_43473().method_54663(Theme.TEXT).method_10852(e.text()));
         boolean first = true;

         for (class_5481 t : this.field_22793.method_1728(full, Math.max(20, innerW))) {
            out.add(new BotScreen.WLine(t, new int[0], first));
            first = false;
         }
      }

      return out;
   }

   private void layoutTiles(int x, int y, int w, int h) {
      int n = count();
      int gap = 4;
      int best = 1;
      double score = -1.0;

      for (int cols = 1; cols <= n; cols++) {
         int rows = (n + cols - 1) / cols;
         double tw = (double)(w - (cols - 1) * gap) / cols;
         double th = (double)(h - (rows - 1) * gap) / rows;
         double s = Math.min(tw, th * 2.6);
         if (s > score) {
            score = s;
            best = cols;
         }
      }

      int cols = best;
      int rows = (n + cols - 1) / cols;
      int tw = (w - (cols - 1) * gap) / cols;
      int th = (h - (rows - 1) * gap) / rows;
      float scale = tw >= 200 && th >= 70 ? 1.0F : 0.5F;

      for (int i = 0; i < n; i++) {
         int c = i % cols;
         int r = i / cols;
         this.tiles.add(new BotScreen.Tile(x + c * (tw + gap), y + r * (th + gap), tw, th, scale));
      }
   }

   private void initGui() {
      this.input = null;
      this.leftX = this.ax + 4;
      this.rightX = this.ax + this.aw - 4 - 162;
      this.midX = this.leftX + 162 + 10;
      this.midW = this.rightX - 10 - this.midX;
      boolean middle = this.midW >= 84;
      this.closeButton = (PillButton)this.method_37063(
         new PillButton(
            this.leftX + 162 - 64,
            this.ay + 1,
            64,
            14,
            "Zamknij GUI",
            Theme.RED,
            false,
            () -> online() && bot().inventory().window() != null,
            () -> bot().closeWindow()
         )
      );
      int bh = 18;
      List<int[]> spots = new ArrayList<>();
      if (middle) {
         int bw = Math.min(110, this.midW);
         int bx = this.midX + (this.midW - bw) / 2;
         int by = this.ay + 66;

         for (int i = 0; i < 5; i++) {
            spots.add(new int[]{bx, by + i * (bh + 4), bw});
         }
      } else {
         int bw = 38;
         int by = this.ay + 120;

         for (int i = 0; i < 4; i++) {
            spots.add(new int[]{this.rightX + i * (bw + 3), by, bw});
         }

         spots.add(new int[]{this.rightX, by + bh + 4, 162});
      }

      PillButton lpm = new PillButton(spots.get(0)[0], spots.get(0)[1], spots.get(0)[2], bh, "LPM", Theme.ACCENT, false, this::canAct, () -> bot().swing());
      lpm.method_47400(class_7919.method_47407(class_2561.method_43470("Bot macha ręką / klika lewym przyciskiem trzymając przedmiot")));
      PillButton ppm = new PillButton(spots.get(1)[0], spots.get(1)[1], spots.get(1)[2], bh, "PPM", Theme.ACCENT, false, this::canAct, () -> bot().useItem());
      ppm.method_47400(class_7919.method_47407(class_2561.method_43470("Bot klika prawym przyciskiem trzymanym przedmiotem (np. otwiera menu z itemu)")));
      this.holdButton = new PillButton(
         spots.get(2)[0],
         spots.get(2)[1],
         spots.get(2)[2],
         bh,
         middle ? "Trzymaj PPM" : "Trzymaj",
         Theme.GOLD,
         false,
         this::canAct,
         () -> bot().holdUse(!bot().inventory().using())
      );
      this.holdButton
         .method_47400(class_7919.method_47407(class_2561.method_43470("Przytrzymuje prawy przycisk (jedzenie, łuk, tarcza) aż klikniesz ponownie")));
      PillButton drop = new PillButton(
         spots.get(3)[0], spots.get(3)[1], spots.get(3)[2], bh, middle ? "Wyrzuć (Q)" : "Q", Theme.RED, false, this::canAct, () -> bot().drop(method_25441())
      );
      drop.method_47400(class_7919.method_47407(class_2561.method_43470("Wyrzuca 1 sztukę trzymanego przedmiotu, z Ctrl cały stack")));
      this.method_37063(lpm);
      this.method_37063(ppm);
      this.method_37063(this.holdButton);
      this.method_37063(drop);
      PillButton dropAll = new PillButton(
         spots.get(4)[0], spots.get(4)[1], spots.get(4)[2], bh, "Wyrzuć całe eq", Theme.RED, this::canAct, () -> bot().throwAll()
      );
      dropAll.method_47400(class_7919.method_47407(class_2561.method_43470("Wyrzuca po kolei wszystkie przedmioty z ekwipunku, zbroję i drugą rękę")));
      this.method_37063(dropAll);
   }

   public void method_25393() {
      if (this.rebuild) {
         this.rebuild = false;
         boolean countFocused = this.method_25399() == this.countField;
         int cursor = this.countField.method_1881();
         this.method_41843();
         if (countFocused) {
            this.method_25395(this.countField);
            this.countField.method_1883(Math.min(cursor, this.countField.method_1882().length()), false);
         }
      }
   }

   private boolean canAct() {
      return online() && bot().inventory().registries() != null;
   }

   private static boolean anyOnline() {
      for (int i = 0; i < count(); i++) {
         Bot b = EasyBotClient.bots[i];
         if (b != null && b.state() == Bot.State.PLAY) {
            return true;
         }
      }

      return false;
   }

   private void switchTab(int t) {
      if (tab != t) {
         tab = t;
         this.selA = this.selB = null;
         this.selecting = false;
         this.method_41843();
      }
   }

   private void select(int i) {
      if (i != EasyBotClient.selected) {
         EasyBotClient.selected = i;
         scroll = 0;
         this.selA = this.selB = null;
         this.selecting = false;
      }
   }

   private void pick(int dir) {
      this.select(Math.floorMod(EasyBotClient.selected + dir, count()));
   }

   private void launch() {
      if (EasyBotClient.canLaunch()) {
         EasyBotClient.launch();
         scroll = 0;
         this.selA = this.selB = null;
         if (this.input != null) {
            this.method_25395(this.input);
         }
      }
   }

   private void sendChat() {
      String text = this.input.method_1882();
      if (!text.isBlank()) {
         boolean sent = false;
         if (count() > 1 && toAll) {
            for (int i = 0; i < count(); i++) {
               Bot b = EasyBotClient.bots[i];
               if (b != null && b.state() == Bot.State.PLAY) {
                  b.chat(text);
                  sent = true;
               }
            }
         } else if (online()) {
            bot().chat(text);
            sent = true;
         }

         if (sent) {
            if (history.isEmpty() || !history.getLast().equals(text)) {
               history.add(text);
            }

            this.historyPos = history.size();
            this.input.method_1852("");
            scroll = 0;
         }
      }
   }

   public boolean method_25404(int key, int scancode, int modifiers) {
      if (method_25438(key) && this.hasSelection()) {
         boolean fieldSelection = this.method_25399() instanceof class_342 f && !f.method_1866().isEmpty();
         if (!fieldSelection) {
            this.copySelection();
            return true;
         }
      }

      if (tab == 1 && online() && this.guiKey(key, scancode)) {
         return true;
      } else {
         boolean enter = key == 257 || key == 335;
         if (enter && this.input != null && this.method_25399() == this.input) {
            this.sendChat();
            return true;
         } else if (enter && this.method_25399() instanceof class_342) {
            this.launch();
            return true;
         } else if (this.input != null && this.method_25399() == this.input && (key == 265 || key == 264) && !history.isEmpty()) {
            this.historyPos = Math.clamp(this.historyPos + (key == 265 ? -1 : 1), 0, history.size());
            this.input.method_1852(this.historyPos < history.size() ? history.get(this.historyPos) : "");
            return true;
         } else {
            return super.method_25404(key, scancode, modifiers);
         }
      }
   }

   private boolean guiKey(int key, int scancode) {
      boolean typing = this.method_25399() instanceof class_342;
      int digit = key >= 49 && key <= 57 ? key - 49 : -1;
      if (this.hoveredSlot >= 0) {
         if (digit >= 0) {
            bot().click(this.hoveredSlot, digit, class_1713.field_7791);
            return true;
         }

         if (this.field_22787.field_1690.field_1831.method_1417(key, scancode)) {
            bot().click(this.hoveredSlot, 40, class_1713.field_7791);
            return true;
         }

         if (this.field_22787.field_1690.field_1869.method_1417(key, scancode) && !typing) {
            bot().click(this.hoveredSlot, method_25441() ? 1 : 0, class_1713.field_7795);
            return true;
         }
      } else if (digit >= 0 && !typing) {
         bot().select(digit);
         return true;
      }

      return false;
   }

   private int tileAt(double mx, double my) {
      for (int i = 0; i < this.tiles.size(); i++) {
         if (this.tiles.get(i).in(mx, my)) {
            return i;
         }
      }

      return -1;
   }

   private double localX(double mx) {
      return this.sx + (mx - this.sx) / this.ks;
   }

   private double localY(double my) {
      return this.sy + (my - this.sy) / this.ks;
   }

   public boolean method_25402(double mx, double my, int button) {
      for (int i = 0; i < 2; i++) {
         if (this.inTab(i, mx, my)) {
            this.switchTab(i);
            return true;
         }
      }

      if (tab == 0) {
         int t = this.tileAt(mx, my);
         if (t >= 0) {
            boolean was = t == EasyBotClient.selected;
            this.select(t);
            BotScreen.Tile tile = this.tiles.get(t);
            if (my < tile.textY() && ConsoleOpener.headerClick(this, this.field_22787, t)) {
               return true;
            }

            if (my >= tile.textY()) {
               this.method_25395(null);
               if (button == 1 && this.hasSelection()) {
                  this.copySelection();
                  return true;
               }

               if (button == 0 && was && !this.wrapped.isEmpty()) {
                  this.setBounds(tile);
                  int[] hit = this.hit(this.localX(mx), this.localY(my));
                  long now = System.currentTimeMillis();
                  if (now - this.lastConsoleClick < 250L && this.selA != null && hit[0] == this.selA[0]) {
                     this.selectWord(hit);
                     this.selecting = false;
                  } else {
                     this.selA = hit;
                     this.selB = hit;
                     this.selecting = true;
                  }

                  this.lastConsoleClick = now;
               }

               return true;
            }
         }
      }

      if (tab == 1 && online()) {
         for (BotScreen.Hot h : this.hots) {
            if (h.in(mx, my)) {
               if (h.kind() == 1) {
                  bot().select(h.slot());
                  return true;
               }

               this.slotClick(h.slot(), button);
               return true;
            }
         }
      }

      if (super.method_25402(mx, my, button)) {
         return true;
      } else if (tab == 1
         && online()
         && mx >= this.ax
         && mx < this.ax + this.aw
         && my >= this.ay
         && my < this.ay + this.ah
         && !bot().inventory().cursor().method_7960()
         && button <= 1) {
         bot().click(-999, button, class_1713.field_7790);
         return true;
      } else {
         return false;
      }
   }

   private void slotClick(int slot, int button) {
      long now = System.currentTimeMillis();
      boolean dbl = button == 0 && slot == this.lastSlot && now - this.lastSlotClick < 250L;
      this.lastSlot = slot;
      this.lastSlotClick = now;
      if (button == 2) {
         bot().click(slot, 2, class_1713.field_7796);
      } else if (method_25442()) {
         bot().click(slot, button, class_1713.field_7794);
      } else if (dbl && !bot().inventory().cursor().method_7960()) {
         bot().click(slot, 0, class_1713.field_7793);
      } else if (button <= 1) {
         bot().click(slot, button, class_1713.field_7790);
      }
   }

   public boolean method_25403(double mx, double my, int button, double dx, double dy) {
      if (this.selecting && tab == 0) {
         if (my < this.sy) {
            scroll = Math.clamp(scroll + 1, 0, this.maxScroll());
         } else if (my > this.sy + this.sh) {
            scroll = Math.clamp(scroll - 1, 0, this.maxScroll());
         }

         this.selB = this.hit(this.localX(mx), this.localY(my));
         return true;
      } else {
         return super.method_25403(mx, my, button, dx, dy);
      }
   }

   public boolean method_25406(double mx, double my, int button) {
      if (this.selecting) {
         this.selecting = false;
         if (this.selA != null && this.selB != null && this.selA[0] == this.selB[0] && this.selA[1] == this.selB[1]) {
            this.selA = this.selB = null;
         }

         return true;
      } else {
         return super.method_25406(mx, my, button);
      }
   }

   public boolean method_25401(double mx, double my, double horizontal, double vertical) {
      if (tab == 0 && this.main != null && this.main.in(mx, my)) {
         mainScroll = Math.max(0, mainScroll + (int)Math.signum(vertical) * 3);
         return true;
      }

      if (tab == 0) {
         int t = this.tileAt(mx, my);
         if (t >= 0 && my >= this.tiles.get(t).textY()) {
            this.select(t);
            scroll = Math.clamp(scroll + (int)Math.signum(vertical) * 3, 0, this.maxScroll());
            return true;
         }
      }

      if (tab == 1 && online() && mx >= this.rightX && mx < this.rightX + 162 && my >= this.ay && my < this.ay + this.ah && vertical != 0.0) {
         int sel = bot().inventory().selected();
         bot().select(Math.floorMod(sel - (int)Math.signum(vertical), 9));
         return true;
      } else {
         return super.method_25401(mx, my, horizontal, vertical);
      }
   }

   private int tabX(int i) {
      return i == 0 ? this.ax : this.ax + this.field_22793.method_1727("Konsola") + 16;
   }

   private String tabLabel(int i) {
      return i == 0 ? "Konsola" : "GUI bota";
   }

   private boolean inTab(int i, double mx, double my) {
      int x = this.tabX(i);
      return mx >= x - 2 && mx < x + this.field_22793.method_1727(this.tabLabel(i)) + 12 && my >= this.tabsY && my < this.tabsY + 14;
   }

   private int visibleLines() {
      return Math.max(1, (this.ch - (this.ks < 1.0F ? 6 : 8)) / 10);
   }

   private int maxScroll() {
      return Math.max(0, this.wrapped.size() - this.visibleLines());
   }

   public void method_25419() {
      EasyBotClient.save();
      this.field_22787.method_1507(this.parent);
   }

   public void method_25394(class_332 c, int mx, int my, float delta) {
      for (int i = 0; i < this.nickFields.size(); i++) {
         this.nickFields.get(i).method_1888(!EasyBotClient.busy(i));
      }

      if (this.closeButton != null) {
         this.closeButton.field_22764 = online() && bot().inventory().window() != null;
      }

      if (this.holdButton != null && bot() != null) {
         boolean using = bot().inventory().using();
         boolean wide = this.midW >= 84;
         this.holdButton.method_25355(class_2561.method_43470(using ? (wide ? "Puść PPM" : "Puść") : (wide ? "Trzymaj PPM" : "Trzymaj")));
      }

      super.method_25394(c, mx, my, delta);
      if (tab == 1 && bot() != null) {
         Bot.Inventory inv = bot().inventory();
         if (!inv.cursor().method_7960()) {
            c.method_51448().method_22903();
            c.method_51448().method_46416(0.0F, 0.0F, 300.0F);
            c.method_51427(inv.cursor(), mx - 8, my - 8);
            c.method_51431(this.field_22793, inv.cursor(), mx - 8, my - 8);
            c.method_51448().method_22909();
         } else if (!this.hoveredStack.method_7960()) {
            try {
               List<class_2561> lines = this.hoveredStack
                  .method_7950(
                     class_9635.method_59530(inv.registries()), null, this.field_22787.field_1690.field_1827 ? class_1836.field_41071 : class_1836.field_41070
                  );
               c.method_64038(this.field_22793, lines, this.hoveredStack.method_32347(), mx, my);
            } catch (Exception e) {
               c.method_51438(this.field_22793, this.hoveredStack.method_7964(), mx, my);
            }
         }
      }
   }

   public void method_25420(class_332 c, int mx, int my, float delta) {
      super.method_25420(c, mx, my, delta);
      Draw.shadow(c, this.px, this.py, this.pw, this.ph);
      Draw.box(c, this.px, this.py, this.pw, this.ph, Theme.PANEL, Theme.OUTLINE);
      c.method_25296(this.px + 1, this.py + 1, this.px + this.pw - 1, this.py + 30, Theme.HEAD_TOP, Theme.HEAD_BOTTOM);
      c.method_25294(this.px + 1, this.py + 30, this.px + this.pw - 1, this.py + 30 + 1, Theme.OUTLINE);
      class_2561 title = class_2561.method_43470("EasyBot").method_27692(class_124.field_1067);
      c.method_51439(this.field_22793, title, this.px + 10, this.py + 11, Theme.TEXT, false);
      int titleEnd = this.px + 10 + this.field_22793.method_27525(title) + 6;
      int statusX = this.status(c);
      String server = EasyBotClient.address.isBlank() ? "brak adresu" : EasyBotClient.address;
      c.method_51433(this.field_22793, Draw.trim(this.field_22793, "· " + server, statusX - 16 - titleEnd), titleEnd, this.py + 11, Theme.TEXT_DIM, false);
      int rowY = this.py + 30 + 8;
      Draw.box(c, this.px + 8, rowY, this.addrW, 20, Theme.FIELD, this.address.method_25370() ? Theme.ACCENT_DARK : Theme.OUTLINE);
      Draw.box(c, this.countX, rowY, 54, 20, Theme.FIELD, this.countField.method_25370() ? Theme.ACCENT_DARK : Theme.OUTLINE);
      c.method_51433(this.field_22793, "Boty:", this.countX + 6, rowY + 6, Theme.TEXT_DIM, false);
      this.tabs(c, mx, my);
      if (tab == 0) {
         for (int i = 0; i < this.tiles.size(); i++) {
            this.tile(c, i, mx, my);
         }

         if (this.main != null) {
            this.mainWindow(c, mx, my);
         }

         int inputY = this.py + this.ph - 8 - 20;
         int inputW = this.aw - 80 - (count() > 1 ? 98 : 0);
         Draw.box(c, this.px + 8, inputY, inputW, 20, Theme.FIELD, this.input.method_25370() ? Theme.ACCENT_DARK : Theme.OUTLINE);
      } else {
         this.gui(c, mx, my);
      }
   }

   private void tabs(class_332 c, int mx, int my) {
      Bot bot = bot();
      boolean windowOpen = bot != null && bot.inventory().window() != null;

      for (int i = 0; i < 2; i++) {
         int x = this.tabX(i);
         boolean on = tab == i;
         boolean hover = this.inTab(i, mx, my);
         c.method_51433(this.field_22793, this.tabLabel(i), x, this.tabsY + 3, on ? Theme.TEXT : (hover ? Draw.lighten(Theme.TEXT_DIM) : Theme.TEXT_DIM), false);
         int w = this.field_22793.method_1727(this.tabLabel(i));
         if (i == 1 && windowOpen) {
            Draw.rect(c, x + w + 4, this.tabsY + 4, 5, 6, -10696449);
         }

         if (on) {
            c.method_25294(x, this.tabsY + 13, x + w, this.tabsY + 15, Theme.ACCENT);
         }
      }

      c.method_25294(this.ax, this.tabsY + 15, this.ax + this.aw, this.tabsY + 16, Theme.OUTLINE);
      int leftEnd = this.tabX(1) + this.field_22793.method_1727(this.tabLabel(1)) + 12;
      if (this.pickerX >= 0) {
         int i = EasyBotClient.selected;
         String n = EasyBotClient.nicks[i] != null && !EasyBotClient.nicks[i].isEmpty() ? EasyBotClient.nicks[i] : "Bot " + (i + 1);
         n = Draw.trim(this.field_22793, i + 1 + ". " + n, 90);
         c.method_51433(this.field_22793, n, this.pickerX + 18 + (92 - this.field_22793.method_1727(n)) / 2, this.tabsY + 2, Theme.TEXT, false);
         leftEnd = this.pickerX + 18 + 96 + 14 + 10;
      }

      String right = null;
      int color = Theme.TEXT_FAINT;
      String problem = EasyBotClient.problem();
      boolean typed = EasyBotClient.nicks[0] != null && !EasyBotClient.nicks[0].isEmpty() || count() > 1;
      if (problem != null && typed && EasyBotClient.hasIdle()) {
         right = problem;
         color = Theme.ORANGE;
      } else if (System.currentTimeMillis() - this.copiedAt < 1500L) {
         right = "Skopiowano do schowka";
         color = Theme.ACCENT;
      } else if (tab == 0 && this.hasSelection()) {
         right = "Ctrl+C lub PPM - kopiuj";
      } else if (tab == 1 && online()) {
         right = "Shift - szybkie przenoszenie · 1-9 - zamiana · Q - wyrzuć";
      } else if (tab == 0 && count() > 1) {
         right = "Kliknij konsolę, żeby wybrać bota";
      }

      if (right != null) {
         right = Draw.trim(this.field_22793, right, this.ax + this.aw - leftEnd);
         c.method_51433(this.field_22793, right, this.ax + this.aw - this.field_22793.method_1727(right), this.tabsY + 3, color, false);
      }
   }

   private int status(class_332 c) {
      Bot bot = bot();
      Bot.State state = bot == null ? Bot.State.OFFLINE : bot.state();
      String label = bot == null ? (EasyBotClient.queued(EasyBotClient.selected) ? "W kolejce" : "Brak bota") : state.label;
      if (bot != null && state == Bot.State.PLAY) {
         long s = bot.onlineMillis() / 1000L;
         label = label + String.format(" · %d:%02d", s / 60L, s % 60L);
         String pos = bot.position();
         if (pos != null) {
            label = label + " · " + pos;
         }
      }

      if (count() > 1) {
         label = "Online " + EasyBotClient.online() + "/" + count() + " · " + (EasyBotClient.selected + 1) + ": " + label;
      }

      int w = this.field_22793.method_1727(label);
      int x = this.px + this.pw - 10 - w;
      c.method_51433(this.field_22793, label, x, this.py + 11, state == Bot.State.OFFLINE ? Theme.TEXT_DIM : Theme.TEXT, false);
      Draw.rect(c, x - 10, this.py + 11, 6, 7, bot == null ? Theme.OFF : state.color);
      return x - 10;
   }

   private void center(class_332 c, String a, String b, int x, int y, int w, int h) {
      c.method_51433(this.field_22793, a, x + (w - this.field_22793.method_1727(a)) / 2, y + h / 2 - 10, Theme.TEXT_DIM, false);
      c.method_51433(this.field_22793, b, x + (w - this.field_22793.method_1727(b)) / 2, y + h / 2 + 2, Theme.TEXT_FAINT, false);
   }

   private void setBounds(BotScreen.Tile t) {
      this.ks = t.scale();
      this.sx = t.x() + 1;
      this.sy = t.textY();
      this.sw = t.w() - 2;
      this.sh = t.textH() - 1;
      this.cx = this.sx;
      this.cy = this.sy;
      this.cw = (int)(this.sw / this.ks);
      this.ch = (int)(this.sh / this.ks);
   }

   private void tile(class_332 c, int i, int mx, int my) {
      BotScreen.Tile t = this.tiles.get(i);
      boolean sel = i == EasyBotClient.selected;
      boolean hover = t.in(mx, my);
      Bot bot = EasyBotClient.bots[i];
      class_342 field = this.nickFields.get(i);
      Draw.box(c, t.x(), t.y(), t.w(), t.h(), Theme.FIELD, sel && count() > 1 ? Theme.ACCENT_DARK : (field.method_25370() ? Theme.ACCENT_DARK : (hover ? Theme.OUTLINE_HOVER : Theme.OUTLINE)));
      c.method_25294(t.x() + 1, t.y() + 16 - 1, t.x() + t.w() - 1, t.y() + 16, Theme.OUTLINE);
      int dot = EasyBotClient.queued(i) ? Theme.GOLD : (bot == null ? Theme.OFF : bot.state().color);
      Draw.rect(c, t.x() + t.w() - 12, t.y() + 5, 6, 7, dot);
      BotScreen.Cache cache = this.caches.computeIfAbsent(i, k -> new BotScreen.Cache());
      int innerW = (int)((t.w() - 2) / t.scale()) - (t.scale() < 1.0F ? 8 : 14);
      if (bot != cache.bot || bot != null && bot.version() != cache.version || innerW != cache.width) {
         int before = cache.lines.size();
         if (sel && (bot != cache.bot || innerW != cache.width)) {
            this.selA = this.selB = null;
         }

         cache.bot = bot;
         cache.version = bot == null ? -1 : bot.version();
         cache.width = innerW;
         cache.lines = bot == null ? List.of() : this.wrap(bot, innerW);
         if (sel) {
            this.offsets.clear();
            if (cache.lines.size() < before) {
               this.selA = this.selB = null;
            }

            if (scroll > 0 && !this.selecting) {
               scroll = Math.max(0, scroll + cache.lines.size() - before);
            }
         }
      }

      if (sel) {
         this.wrapped = cache.lines;
         this.setBounds(t);
         scroll = Math.clamp(scroll, 0, this.maxScroll());
      }

      int bx = t.x() + 1;
      int by = t.textY();
      int bw = t.w() - 2;
      int bh = t.textH() - 1;
      if (bot == null) {
         if (count() == 1) {
            this.center(
               c,
               "Wpisz nick i kliknij „Wyślij bota”.",
               "Bot wejdzie na "
                  + (EasyBotClient.address.isBlank() ? "podany serwer" : EasyBotClient.address)
                  + ", a tutaj zobaczysz wszystko, co się z nim dzieje.",
               bx,
               by,
               bw,
               bh
            );
         } else {
            String s = EasyBotClient.queued(i) ? "W kolejce..." : "Nie wysłany";
            c.method_51448().method_22903();
            c.method_51448().method_46416(bx + 4, by + 4, 0.0F);
            c.method_51448().method_22905(t.scale(), t.scale(), 1.0F);
            c.method_51433(this.field_22793, s, 0, 0, Theme.TEXT_FAINT, false);
            c.method_51448().method_22909();
         }
      } else {
         float s = t.scale();
         int lw = (int)(bw / s);
         int lh = (int)(bh / s);
         int pad = s < 1.0F ? 3 : 4;
         int visible = Math.max(1, (lh - pad * 2) / 10);
         List<BotScreen.WLine> lines = cache.lines;
         int off = sel ? Math.min(scroll, Math.max(0, lines.size() - visible)) : 0;
         int end = lines.size() - off;
         int start = Math.max(0, end - visible);
         c.method_44379(bx, by, bx + bw, by + bh);
         c.method_51448().method_22903();
         c.method_51448().method_46416(bx, by, 0.0F);
         c.method_51448().method_22905(s, s, 1.0F);
         c.method_51448().method_46416(-bx, -by, 0.0F);
         int y = by + pad + (visible - (end - start)) * 10;
         if (sel) {
            this.drawStart = start;
            this.drawEnd = end;
            this.drawY0 = y;
         }

         int[] a = null;
         int[] b = null;
         if (sel && this.hasSelection()) {
            boolean swap = this.selA[0] > this.selB[0] || this.selA[0] == this.selB[0] && this.selA[1] > this.selB[1];
            a = swap ? this.selB : this.selA;
            b = swap ? this.selA : this.selB;
         }

         for (int k = start; k < end; k++) {
            BotScreen.WLine line = lines.get(k);
            if (a != null && k >= a[0] && k <= b[0]) {
               int[] o = this.offsets(line);
               int from = k == a[0] ? a[1] : 0;
               int to = k == b[0] ? b[1] : line.cps().length;
               int x0 = bx + 4 + o[Math.min(from, o.length - 1)];
               int x1 = bx + 4 + o[Math.min(to, o.length - 1)] + (k < b[0] ? 3 : 0);
               if (x1 > x0) {
                  c.method_25294(x0, y - 1, x1, y + 10 - 1, -1439868002);
               }
            }

            c.method_51430(this.field_22793, line.text(), bx + 4, y, Theme.TEXT, false);
            y += 10;
         }

         if (lines.size() > visible) {
            int trackH = lh - 4;
            int thumbH = Math.max(12, trackH * visible / lines.size());
            int max = Math.max(1, lines.size() - visible);
            int thumbY = by + 2 + (trackH - thumbH) * (max - off) / max;
            c.method_25294(bx + lw - 4, thumbY, bx + lw - 2, thumbY + thumbH, off > 0 ? Theme.TEXT_DIM : Theme.OUTLINE_HOVER);
         }

         c.method_51448().method_22909();
         c.method_44380();
      }
   }

   private List<BotScreen.WLine> wrap(Bot bot, int innerW) {
      List<BotScreen.WLine> out = new ArrayList<>();

      for (Bot.Line line : ErrorsClient.lines(bot)) {
         class_2561 full = class_2561.method_43470(TIME.format(Instant.ofEpochMilli(line.time())) + " ")
            .method_54663(Theme.TEXT_FAINT)
            .method_10852(class_2561.method_43473().method_54663(Theme.TEXT).method_10852(line.text()));
         boolean first = true;

         for (class_5481 t : this.field_22793.method_1728(full, Math.max(20, innerW))) {
            List<Integer> cps = new ArrayList<>();
            t.accept((index, style, cp) -> {
               cps.add(cp);
               return true;
            });
            out.add(new BotScreen.WLine(t, cps.stream().mapToInt(Integer::intValue).toArray(), first));
            first = false;
         }
      }

      return out;
   }

   private int[] offsets(BotScreen.WLine line) {
      return this.offsets.computeIfAbsent(line, l -> {
         int[] off = new int[l.cps().length + 1];
         int[] i = new int[]{0};
         l.text().accept((index, style, cp) -> {
            off[i[0] + 1] = off[i[0]] + this.field_22793.method_30880(class_5481.method_30741(cp, style));
            i[0]++;
            return true;
         });
         return off;
      });
   }

   private int[] hit(double mx, double my) {
      if (this.wrapped.isEmpty()) {
         return new int[]{0, 0};
      }

      int line = this.drawStart + (int)Math.floor((my - this.drawY0) / 10.0);
      if (my < this.cy) {
         line = this.drawStart;
      }

      if (line < this.drawStart) {
         line = this.drawStart;
      }

      if (line >= this.drawEnd) {
         line = Math.max(this.drawStart, this.drawEnd - 1);
         if (my > this.cy + this.ch || my >= this.drawY0 + (this.drawEnd - this.drawStart) * 10) {
            return new int[]{line, this.wrapped.get(line).cps().length};
         }
      }

      int[] off = this.offsets(this.wrapped.get(line));
      double x = mx - (this.cx + 4);
      int col = off.length - 1;

      for (int i = 0; i < off.length - 1; i++) {
         if (x < (off[i] + off[i + 1]) / 2.0) {
            col = i;
            break;
         }
      }

      return new int[]{line, col};
   }

   private void selectWord(int[] hit) {
      int[] cps = this.wrapped.get(hit[0]).cps();
      int s = Math.min(hit[1], cps.length);
      int e = s;

      while (s > 0 && !Character.isWhitespace(cps[s - 1])) {
         s--;
      }

      while (e < cps.length && !Character.isWhitespace(cps[e])) {
         e++;
      }

      this.selA = new int[]{hit[0], s};
      this.selB = new int[]{hit[0], e};
   }

   private boolean hasSelection() {
      return tab == 0
         && this.selA != null
         && this.selB != null
         && (this.selA[0] != this.selB[0] || this.selA[1] != this.selB[1])
         && this.selA[0] < this.wrapped.size()
         && this.selB[0] < this.wrapped.size();
   }

   private void copySelection() {
      boolean swap = this.selA[0] > this.selB[0] || this.selA[0] == this.selB[0] && this.selA[1] > this.selB[1];
      int[] a = swap ? this.selB : this.selA;
      int[] b = swap ? this.selA : this.selB;
      StringBuilder sb = new StringBuilder();

      for (int i = a[0]; i <= b[0]; i++) {
         int[] cps = this.wrapped.get(i).cps();
         int from = Math.min(i == a[0] ? a[1] : 0, cps.length);
         int to = Math.min(i == b[0] ? b[1] : cps.length, cps.length);
         if (to > from) {
            sb.append(new String(cps, from, to - from));
         }

         if (i < b[0]) {
            sb.append(this.wrapped.get(i + 1).start() ? "\n" : " ");
         }
      }

      this.field_22787.field_1774.method_1455(sb.toString());
      this.copiedAt = System.currentTimeMillis();
   }

   private void gui(class_332 c, int mx, int my) {
      this.hots.clear();
      this.hoveredSlot = -1;
      this.hoveredStack = class_1799.field_8037;
      Draw.box(c, this.ax, this.ay, this.aw, this.ah, Theme.FIELD, Theme.OUTLINE);
      Bot bot = bot();
      if (!online()) {
         this.center(c, "Bot nie jest na serwerze.", "Wyślij bota, a tutaj zobaczysz jego ekwipunek i otwarte GUI.", this.ax, this.ay, this.aw, this.ah);
      } else {
         Bot.Inventory inv = bot.inventory();
         if (inv.registries() == null) {
            this.center(
               c, "Podgląd ekwipunku niedostępny.", "Nie udało się wczytać rejestrów serwera - szczegóły w konsoli.", this.ax, this.ay, this.aw, this.ah
            );
         } else {
            c.method_44379(this.ax + 1, this.ay + 1, this.ax + this.aw - 1, this.ay + this.ah - 1);
            Bot.Window w = inv.window();
            c.method_51433(this.field_22793, w != null ? "OTWARTE GUI" : "BRAK OTWARTEGO GUI", this.leftX + 1, this.ay + 4, Theme.TEXT_FAINT, false);
            class_1799[] shown = w != null && w.slots() != null ? w.slots() : inv.player();
            int playerBase = w != null && w.slots() != null ? w.slots().length - 36 : 9;
            if (w != null) {
               class_2960 typeId = class_7923.field_41187.method_10221(w.type());
               class_2561 wt = class_2561.method_43473().method_10852(w.title());
               if (wt.getString().isBlank()) {
                  c.method_51433(this.field_22793, "(bez tytułu)", this.leftX + 1, this.ay + 17, Theme.TEXT_DIM, false);
               } else {
                  c.method_51430(this.field_22793, (class_5481)this.field_22793.method_1728(wt, 162).getFirst(), this.leftX + 1, this.ay + 17, Theme.TEXT, false);
               }

               if (typeId != null) {
                  c.method_51433(this.field_22793, typeId.method_12832(), this.leftX + 1, this.ay + 28, Theme.TEXT_FAINT, false);
               }

               if (w.slots() == null) {
                  c.method_51433(this.field_22793, "Wczytywanie zawartości...", this.leftX + 1, this.ay + 44, Theme.TEXT_DIM, false);
               } else {
                  int n = Math.max(0, w.slots().length - 36);
                  int cols = n % 9 == 0 ? 9 : Math.min(n, 9);

                  for (int i = 0; i < n; i++) {
                     this.slot(c, mx, my, this.leftX + i % cols * 18, this.ay + 40 + i / cols * 18, i, w.slots()[i], false);
                  }
               }
            } else {
               c.method_51433(this.field_22793, "Bot nie ma otwartego okna.", this.leftX + 1, this.ay + 17, Theme.TEXT_DIM, false);
               c.method_51433(this.field_22793, "Zbroja i druga ręka", this.leftX + 1, this.ay + 34, Theme.TEXT_FAINT, false);

               for (int i = 0; i < 4; i++) {
                  this.slot(c, mx, my, this.leftX + i * 18, this.ay + 45, 5 + i, inv.player()[5 + i], false);
               }

               this.slot(c, mx, my, this.leftX + 72 + 8, this.ay + 45, 45, inv.player()[45], false);
               c.method_51433(this.field_22793, "Craftowanie 2x2", this.leftX + 1, this.ay + 70, Theme.TEXT_FAINT, false);

               for (int i = 0; i < 4; i++) {
                  this.slot(c, mx, my, this.leftX + i % 2 * 18, this.ay + 81 + i / 2 * 18, 1 + i, inv.player()[1 + i], false);
               }

               c.method_51433(this.field_22793, "→", this.leftX + 36 + 6, this.ay + 81 + 13, Theme.TEXT_DIM, false);
               this.slot(c, mx, my, this.leftX + 36 + 20, this.ay + 81 + 9, 0, inv.player()[0], false);
            }

            c.method_51433(this.field_22793, "EKWIPUNEK BOTA", this.rightX + 1, this.ay + 4, Theme.TEXT_FAINT, false);

            for (int i = 0; i < 27; i++) {
               this.slot(c, mx, my, this.rightX + i % 9 * 18, this.ay + 16 + i / 9 * 18, playerBase + i, shown[playerBase + i], false);
            }

            int hotY = this.ay + 16 + 54 + 4;

            for (int i = 0; i < 9; i++) {
               this.slot(c, mx, my, this.rightX + i * 18, hotY, playerBase + 27 + i, shown[playerBase + 27 + i], i == inv.selected());
            }

            int selY = hotY + 18 + 2;

            for (int i = 0; i < 9; i++) {
               int x = this.rightX + i * 18 + 1;
               boolean on = i == inv.selected();
               boolean hover = mx >= x && mx < x + 18 - 2 && my >= selY && my < selY + 10;
               Draw.box(c, x, selY, 16, 10, on ? Theme.ACCENT : (hover ? Theme.ROW_HOVER : Theme.SUB), on ? Theme.ACCENT : Theme.OUTLINE);
               String d = String.valueOf(i + 1);
               c.method_51433(this.field_22793, d, x + (16 - this.field_22793.method_1727(d)) / 2, selY + 1, on ? Theme.INK : Theme.TEXT_DIM, false);
               this.hots.add(new BotScreen.Hot(x, selY, 16, 10, i, 1));
            }

            class_1799 held = inv.player()[36 + inv.selected()];
            if (this.midW >= 84) {
               c.method_51433(this.field_22793, "W RĘCE", this.midX + (this.midW - this.field_22793.method_1727("W RĘCE")) / 2, this.ay + 4, Theme.TEXT_FAINT, false);
               int bx = this.midX + (this.midW - 36) / 2;
               Draw.box(c, bx, this.ay + 16, 36, 36, Theme.SUB, inv.using() ? Theme.GOLD : Theme.OUTLINE);
               if (!held.method_7960()) {
                  c.method_51448().method_22903();
                  c.method_51448().method_46416(bx + 2, this.ay + 18, 0.0F);
                  c.method_51448().method_22905(2.0F, 2.0F, 1.0F);
                  c.method_51427(held, 0, 0);
                  c.method_51431(this.field_22793, held, 0, 0);
                  c.method_51448().method_22909();
               }

               String name = held.method_7960() ? "pusta ręka" : held.method_7964().getString();
               name = Draw.trim(this.field_22793, name, this.midW);
               c.method_51433(
                  this.field_22793,
                  name,
                  this.midX + (this.midW - this.field_22793.method_1727(name)) / 2,
                  this.ay + 55,
                  held.method_7960() ? Theme.TEXT_FAINT : Theme.TEXT,
                  false
               );
            } else {
               String name = "W ręce: " + (held.method_7960() ? "nic" : held.method_7964().getString());
               c.method_51433(this.field_22793, Draw.trim(this.field_22793, name, 162), this.rightX + 1, selY + 13, Theme.TEXT_DIM, false);
            }

            if (!inv.cursor().method_7960()) {
               String s = "Na kursorze: " + inv.cursor().method_7964().getString() + " (kliknij obok, by wyrzucić)";
               c.method_51433(this.field_22793, Draw.trim(this.field_22793, s, this.aw - 12), this.ax + 5, this.ay + this.ah - 12, Theme.GOLD, false);
            }

            c.method_44380();
         }
      }
   }

   private void slot(class_332 c, int mx, int my, int x, int y, int index, class_1799 stack, boolean selected) {
      boolean hover = mx >= x && mx < x + 18 && my >= y && my < y + 18;
      Draw.box(c, x, y, 18, 18, hover ? Theme.ROW_HOVER : Theme.SUB, selected ? Theme.ACCENT : Theme.OUTLINE);
      if (!stack.method_7960()) {
         c.method_51427(stack, x + 1, y + 1);
         c.method_51431(this.field_22793, stack, x + 1, y + 1);
      }

      if (hover) {
         this.hoveredSlot = index;
         this.hoveredStack = stack;
      }

      this.hots.add(new BotScreen.Hot(x, y, 18, 18, index, 0));
   }

   private static final class Cache {
      Bot bot;
      int version = -1;
      int width = -1;
      List<BotScreen.WLine> lines = List.of();
   }

   private record Entry(long time, int bot, class_2561 text) {
   }

   private record Hot(int x, int y, int w, int h, int slot, int kind) {
      boolean in(double mx, double my) {
         return mx >= this.x && mx < this.x + this.w && my >= this.y && my < this.y + this.h;
      }
   }

   private record Tile(int x, int y, int w, int h, float scale) {
      boolean in(double mx, double my) {
         return mx >= this.x && mx < this.x + this.w && my >= this.y && my < this.y + this.h;
      }

      int textY() {
         return this.y + 16;
      }

      int textH() {
         return this.h - 16;
      }
   }

   private record WLine(class_5481 text, int[] cps, boolean start) {
   }
}
