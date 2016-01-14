# Algoritmer och datastrukturer - Projekt

Projektet som valdes för AoD-kursen var att skapa en Lisp-intrepretator i Java.

Som hjälp hänvisades man till två sidor:

	http://www.michaelnielsen.org/ddi/lisp-as-the-maxwells-equations-of-software/
	http://norvig.com/lispy.html

Vilka tar upp hur man kan göra en lisp-intrepretator i python

Dock så är det stor skilnad på python och Java, bland annat att Java har striktare typer, vilket gör det svårare att implementera det i Java.

Då de knapt implementerade några funktioner så rådslog jag lite dokumentation för lisp:
	
	http://www.lispworks.com/documentation/HyperSpec/Front/Contents.htm
	http://www.gnu.org/software/emacs/manual/html_node/elisp/index.html#SEC_Contents
	https://www.cs.cmu.edu/Groups/AI/util/html/cltl/clm/index.html
	
Vilket jag har försökt att följa, att notera är dock att det kan skillja sig lite, tex man kan inte ha dokumentation i defun.
	
Strukturen är baserad på klassen PartContainer, som i princip är en pekare.
Detta skilljer sig ganska mycket mot den kod på de sidor vi hänvisades till, och gav mig bland annat möjlighet att implementera "setf".

"PartContainer" har ett objekt av typen "Part" i sig.
"Part" ärvs av två klasser:
	PartCons
	PartAtom
	
De klasserna symboliserar "cons" och "atom" i lisp.
Dock så är "nil" bara "PartCons" när den igentligen också skall vara en atom.
Det skulle kunna lösas med att göra om "PartAtom" till ett interface och skapa "PartNil extends PartCons implements PartAtom".
Dock så skulle det inte ge min lösning några större fördelar

"PartAtom" ärvs av:
	PartLambda
	PartSymbol
	PartValue

Där "PartValue" ärvs av:
	PartNumber
	PartString
	
"PartLambda" används för att lagra funktioner i form av lambdauttryck.
"PartSymbol" representerar ett variabelnamn.
"PartNumber" representerar heltal och flyttal (representeras med en boolean)

"PartNumber" är implementerat med "BigDecimal" vilket ger väldigt stora tal med full precision.
Att notera är dock att mattematiska operationer bortsett från det simplaste (+ - * \ abs max min) minskar precisionen till double, pga användandet av Javas "Math" klass.


Listor är uppbyggda av cons enligt lisps specifikation (är det sista cdr nil så är det en lista),ex: (1 . (2 . (3 . nil))) = [1 2 3]
Är det sista cdr inte nil så visas den korrekt som en cons med punk notation, ex: (1 . 2)

Inmatning kan ske med både "(1 . (2 . (3 . nil)))" och "(1 2 3)", vilka båda resulterar i samma resultat.

Min intrepretator tillåter (som jag tidigare nämt) text, ex: '(print "text")'
Den klarar även av citat-tecken i texten, ex: '(print "Med \"citat\" tecken")'

Den hanterar också "quote" tecknet korrekt, ex: "'() = (quote ())"

Radkommentarer är implementerade, ex: "(print 1) ; skriv ut 1"
Även fler-rad kommentarer, ex: "(print 1) #| (print 0)  |# (print 2)"

Enligt specifikationen av lisp så skall inte radbrytningar och flertal mellanrum spela någon roll, och min intrepretator följer den regeln.

En REPL är implementerad (se.hig.aod.projekt.Lisp_REPL).
Det är bara att skriva in lisputtryck i konsolen så skriver den ut resultatet (modifieringar av "environment" behålls).

Funktionerna som är implementerade är:

	(+ num...)
	(- num...)
	(* num...)
	(/ num...)
	(> num...)
	(< num...)
	(>= num...)
	(<= num...)
	(= num...)
	(eq obj...)
	(max num...)
	(min num...)
	(abs num)
	(sin num)
	(cos num)
	(tan num)
	(asin num)
	(acos num)
	(atan num)
	(atan2 num)
	(ceil num)
	(floor num)
	(round num)
	(sqrt num)
	(exp num)

	nil       -> NIL , () , false
	t         -> true
	pi        -> pi
	e         -> e

	'x        -> (quote x)
	(a . b)   -> (cons a b)


	(nil)
	(quote expr)
	(print [expr...])
	(text expr)
	(if expr expr [expr])  // if cond then else

	(cond [cons...])
	(let ({var | (var [init])}*) {form}*)
	(let* ({var | (var [init])}*) {form}*)
	(set [{var expr}*])
	(setq [{var expr}*])
	(setf [{var expr}*])
	(lambda ([var...]) expr)
	(defun var ([var...]) expr)
	(progn [expr...])
	(eval expr)
	(list [expr...])
	(while cond expr)
	(append list list [list...])
	(first list)
	(second list)
	(third list)
	(fourth list)
	(fifth list)
	(sixth list)
	(seventh list)
	(eighth list)
	(ninth list)
	(tenth list)
	(nth uint list)
	(cons expr expr)
	(car cons)
	(cdr cons)
	(c{a,d}...r cons)
	(typep expr type)
	
	
Det finns dock mer funktionalitet som borde läggas till (tidsbrist).
Lite av de är:

*	Skyddandet av systemvariabler, ex "nil" och "t" (nu går de att ändra)
*	Korrekt tolkande av # (ej i kommentar)
*	"block" och "return", både självständigt och i tex "defun"
*	Korrekt "asString" för lambdauttryck
*	Bättre meddelanden vid fel + catch

Mycket kan man dock göra själv med lisp, tex for-loopar