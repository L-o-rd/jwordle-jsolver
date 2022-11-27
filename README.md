# JWordle 
## Simple Wordle game made in Java
## THE GAME HAS BEEN DEVELOPED FOR A UNIVERSITY PROJECT AND IS OF NON-COMMERCIAL USE
### **Echipa:**
* Musat Fabian Grupa 144
* Pascu Ioan Grupa 144
* Uta Mario Grupa 144
### Descriere
JWordle reprezinta o implementare simpla a cunoscutului joc Wordle. Limbajul Java a fost ales pentru usurinta de a dezvolta aplicatii cross-platform si pentru utilitatile incluse precum Swing si AWT. JSolver a fost implementat utilizand trei "algoritmi" diferiti. 
+ Random alege in mod aleator un cuvant valabil. Numarul de incercari medii este foarte mare.
+ Information Theory este algoritmul principal, si motivul central al proiectului. Acesta a fost implementat folosind metoda "intuitiva" care testeaza toate cuvintele posibile folosind toate pattern-urile posibile care pot reiesi din cele trei culori posibile -> 3^5 = 243. Apoi, entropia Shannon pentru un cuvant este calculata pe baza numerelor de cuvinte specifice tuturor pattern-urilor, atasate acelui cuvant. Momentan cel mai bun cuvant este "TAREI" cu o entropie de 6.41, cel mai optim cuvant putand avea entropia de 7.92. Numarul de incercari medii poate fi vazut in timp real, acesta fiind updatat la fiecare joc. Dar, dupa mai multe jocuri, media este de 4.13.
+ Cummulative Product este un algoritm asemanator, ce foloseste statistica. Acesta a fost implementat deoarece este mult mai rapid, dar putin mai slab. Ideea se bazeaza pe frecventa literelor de pe fiecare pozitie si calculeaza produsul cumulativ dintre "erorile" patratice ale acestor frecvente. Media incercarilor este de 5.97.
#### Running
The release package contains two (tested) options: Windows and Linux (Ubuntu latest LTS).
+ Pentru a rula aplicatia veti avea nevoie de doua terminale sau de rularea simultana a ambelor comenzi.
+ Un terminal ruleaza jocul, iar altul ruleaza solver-ul, deoarece sunt doua programe diferite.
##### Windows
Se poate folosi `command prompt/cmd` sau `powershell`.
* 1. Deschiderea `cmd` in folder-ul ce contine package-ul extras.
* 2. `jre-windows\bin\java.exe -jar jwordle.jar` pentru a juca jocul.
* 3. `jre-windows\bin\java.exe -jar jwordle.jar file.txt` pentru a conecta jocul la mmfile "file.txt"
* 4. `jre-windows\bin\java.exe -jar solver.jar file.txt Y` pentru a conecta solver-ul cu jocul si "Y" pentru a optimiza timpul de rulare.
* 5. Momentan sunt valabili 3 algoritmi de rezolvare, unul alege un cuvant in mod aleator, unul ce foloseste cummulative product si frecventele literelor si cel mai bun, cel ce foloseste Teoria Informatiei, respectiv calcularea entropiei Shannon. Fiecare are un "tab" rezervat in solver.jar.
#### Linux
Se poate folosi in general orice terminal, fie `gnome terminal`, `xterm` etc.
* 1. Deschiderea terminalului ales in folder-ul ce contine package-ul extras.
* 2. `./jre-linux/bin/java -jar jwordle.jar` pentru a juca jocul.
* 3. `./jre-linux/bin/java -jar jwordle.jar file.txt` pentru a conecta jocul la mmfile "file.txt"
* 4. `./jre-linux/bin/java -jar solver.jar file.txt Y` pentru a conecta solver-ul cu jocul si "Y" pentru a optimiza timpul de rulare.
* 5. Momentan sunt valabili 3 algoritmi de rezolvare, unul alege un cuvant in mod aleator, unul ce foloseste cummulative product si frecventele literelor si cel mai bun, cel ce foloseste Teoria Informatiei, respectiv calcularea entropiei Shannon. Fiecare are un "tab" rezervat in solver.jar.
#### Referinte
+ https://www.nytimes.com/games/wordle/index.html
+ https://www.youtube.com/watch?v=v68zYyaEmEA -> 3Blue1Brown's video that describes the exact method
+ https://www.youtube.com/watch?v=fVMlnSfGq0c -> Cummulative Product sample code
