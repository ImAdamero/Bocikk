# EasyBot - odzyskane zrodla (dekompilacja z jara, Vineflower 1.12.0)

Uwaga: nazwy Minecrafta sa w INTERMEDIARY (class_304, method_1417...), bo jar byl
zremapowany. Zeby to zbudowac pod 1.21.11 trzeba przemapowac na nazwy z Yarn
(albo Mojmap) - Loom z `mappings` Yarn dla 1.21.11.

Dekompilator moze zostawic drobne bledy skladni/rzutowan do reczniejszej poprawki.

Lista miejsc, ktore w 1.21.11 sie zmienily: PORTING_NOTES_raw.txt
(MISSING MEMBERS i DESC CHANGED). Kluczowe:
- Screen: mouseClicked/mouseDragged/mouseReleased/keyPressed - nowe sygnatury (Click, KeyInput)
- KeyBinding.matchesKey - nowa sygnatura
- DrawContext: drawText zwraca void, getMatrices() -> Matrix3x2fStack
- MathHelper.sin/cos przyjmuja double
- pakiety: akcesory inventory/velocity, NbtElement.asString, info o wersji gry

## Zmiany: Auto login / Auto AFK
- Bot.autoLogin: tylko /register lub /login, gdy serwer o to prosi
- Bot.autoAfk: regulamin, trust-click, /warp afk i chodzenie; OFF = bot stoi i nic sam nie wpisuje
- Dwa przyciski w BotScreen (rzad z zakladkami, prawa strona) - ewentualnie przesunac, jesli nachodza na zakladki
- Flagi nie sa zapisywane do pliku (reset po restarcie gry)

## Motywy GUI
- Theme.java: 7 motywow (Noc, Granat, Fiolet, Rozany, Bursztyn, Lod, AMOLED), pola zamiast stalych
- Wszystkie zahardkodowane kolory z palety zamienione na Theme.X (122 miejsc w gui/*)
- Przycisk "Motyw: ..." w BotScreen (obok Auto login/Auto AFK), wybor zapisywany w config/easybot-theme.txt
- Kolory spoza palety (np. niebieski -10696449, polprzezroczyste nakladki) zostaly bez zmian
