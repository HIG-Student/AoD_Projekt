package se.hig.aod.projekt;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestLisp
{
    interface ReturningLispCode
    {
        Part run(Lisp lisp);
    }

    interface LispCode
    {
        void run(Lisp lisp);
    }

    public void runLisp(LispCode code)
    {
        code.run(new Lisp());
    }

    public Part runLisp(ReturningLispCode code)
    {
        return code.run(new Lisp());
    }

    public String runLisp(String code)
    {
        return new Lisp().run(code);
    }

    public Part runLispAndReturnPart(String code)
    {
        return new Lisp().runAndReturnPart(code);
    }

    @Test
    public void testMultiple()
    {
        assertEquals("Can't add int", "14", runLisp("(+ 4 6) (+ 5 6) (+ 6 6) (+ 7 6) (+ 8 6)"));
        assertEquals("Can't add int", "5", runLisp("(setq foo 4) (+ foo 1)"));
    }

    @Test
    public void testAdd()
    {
        assertEquals("Can't add int", "10", runLisp("(+ 4 6)"));
        assertEquals("Can't add neg int", "0", runLisp("(+ 4 -4)"));
        assertEquals("Can't mix", "10.0", runLisp("(+ 4f 6)"));
        assertEquals("Can't mix", "10.0", runLisp("(+ 4 6f)"));
        assertEquals("Can't add float", "10.0", runLisp("(+ 4f 6f)"));
        assertEquals("Can't add neg float", "0.0", runLisp("(+ 4f -4f)"));
        assertEquals("Can't add multiple", "10", runLisp("(+ 2 2 3 3)"));
    }

    @Test
    public void testSub()
    {
        assertEquals("Can't subtract int", "20", runLisp("(- 25 5)"));
        assertEquals("Can't subtract neg int", "30", runLisp("(- 25 -5)"));
        assertEquals("Can't mix", "20.0", runLisp("(- 25f 5)"));
        assertEquals("Can't mix", "20.0", runLisp("(- 25 5f)"));
        assertEquals("Can't subtract float", "20.0", runLisp("(- 25f 5f)"));
        assertEquals("Can't subtract neg float", "30.0", runLisp("(- 25f -5f)"));
        assertEquals("Can't subtract multiple", "20", runLisp("(- 25 1 2 2)"));
    }

    @Test
    public void testMul()
    {
        assertEquals("Can't multiply int", "10", runLisp("(* 2 5)"));
        assertEquals("Can't multiply neg int", "-10", runLisp("(* 2 -5)"));
        assertEquals("Can't mix", "10.0", runLisp("(* 2f 5)"));
        assertEquals("Can't mix", "10.0", runLisp("(* 2 5f)"));
        assertEquals("Can't multiply float", "10.0", runLisp("(* 2f 5f)"));
        assertEquals("Can't multiply neg float", "-10.0", runLisp("(* 2f -5f)"));
        assertEquals("Can't multiply multiple", "10", runLisp("(* 1 2 5 1)"));
    }

    @Test
    public void testDiv()
    {
        assertEquals("Can't divide int", "20", runLisp("(/ 80 4)"));
        assertEquals("Can't divide neg int", "-20", runLisp("(/ 80 -4)"));
        assertEquals("Can't mix", "20.0", runLisp("(/ 80f 4)"));
        assertEquals("Can't mix", "20.0", runLisp("(/ 80 4f)"));
        assertEquals("Can't divide float", "20.0", runLisp("(/ 80f 4f)"));
        assertEquals("Can't divide neg float", "-20.0", runLisp("(/ 80f -4f)"));
        assertEquals("Can't divide multiple", "20", runLisp("(/ 80 2 2 1)"));
    }

    @Test
    public void testGT()
    {
        assertEquals("Can't compare int", "t", runLisp("(> 80 4)"));
        assertEquals("Can't compare neg int", "t", runLisp("(> 4 -4)"));
        assertEquals("Can't compare int", "NIL", runLisp("(> 4 80)"));
        assertEquals("Can't compare neg int", "NIL", runLisp("(> -4 4)"));

        assertEquals("Can't mix", "t", runLisp("(> 80f 4)"));
        assertEquals("Can't mix", "NIL", runLisp("(> 4f 80)"));

        assertEquals("Can't mix", "t", runLisp("(> 80 4f)"));
        assertEquals("Can't mix", "NIL", runLisp("(> 4 80f)"));

        assertEquals("Can't compare float", "t", runLisp("(> 80f 4f)"));
        assertEquals("Can't compare neg float", "t", runLisp("(> 4f -4f)"));
        assertEquals("Can't compare float", "NIL", runLisp("(> 4f 80f)"));
        assertEquals("Can't compare neg float", "NIL", runLisp("(> -4f 4f)"));

        assertEquals("Can't compare multiple", "t", runLisp("(> 80 40 20 10 0)"));
        assertEquals("Can't compare multiple", "NIL", runLisp("(> 0 10 20 40 80)"));
        assertEquals("Can't compare multiple", "NIL", runLisp("(> 80 40 10 20 0)"));
        assertEquals("Can't compare multiple", "NIL", runLisp("(> 80 40 20 20 10)"));
    }

    @Test
    public void testLT()
    {
        assertEquals("Can't compare int", "NIL", runLisp("(< 80 4)"));
        assertEquals("Can't compare int", "t", runLisp("(< 4 80)"));

        assertEquals("Can't mix", "NIL", runLisp("(< 80f 4)"));
        assertEquals("Can't mix", "t", runLisp("(< 4f 80)"));

        assertEquals("Can't mix", "NIL", runLisp("(< 80 4f)"));
        assertEquals("Can't mix", "t", runLisp("(< 4 80f)"));

        assertEquals("Can't compare float", "NIL", runLisp("(< 80f 4f)"));
        assertEquals("Can't compare float", "t", runLisp("(< 4f 80f)"));

        assertEquals("Can't compare multiple", "NIL", runLisp("(< 80 40 20 10 0)"));
        assertEquals("Can't compare multiple", "t", runLisp("(< 0 10 20 40 80)"));
        assertEquals("Can't compare multiple", "NIL", runLisp("(< 80 40 10 20 0)"));
        assertEquals("Can't compare multiple", "NIL", runLisp("(< 10 20 20 40 80)"));
    }

    @Test
    public void testGTE()
    {
        assertEquals("Can't compare int", "t", runLisp("(>= 80 4)"));
        assertEquals("Can't compare int", "NIL", runLisp("(>= 4 80)"));

        assertEquals("Can't mix", "t", runLisp("(>= 80f 4)"));
        assertEquals("Can't mix", "NIL", runLisp("(>= 4f 80)"));

        assertEquals("Can't mix", "t", runLisp("(>= 80 4f)"));
        assertEquals("Can't mix", "NIL", runLisp("(>= 4 80f)"));

        assertEquals("Can't compare float", "t", runLisp("(>= 80f 4f)"));
        assertEquals("Can't compare float", "NIL", runLisp("(>= 4f 80f)"));

        assertEquals("Can't compare multiple", "t", runLisp("(>= 80 40 20 10 0)"));
        assertEquals("Can't compare multiple", "NIL", runLisp("(>= 0 10 20 40 80)"));
        assertEquals("Can't compare multiple", "NIL", runLisp("(>= 80 40 10 20 0)"));
        assertEquals("Can't compare multiple", "t", runLisp("(>= 80 40 20 20 10)"));
    }

    @Test
    public void testLTE()
    {
        assertEquals("Can't compare int", "NIL", runLisp("(<= 80 4)"));
        assertEquals("Can't compare int", "t", runLisp("(<= 4 80)"));

        assertEquals("Can't mix", "NIL", runLisp("(<= 80f 4)"));
        assertEquals("Can't mix", "t", runLisp("(<= 4f 80)"));

        assertEquals("Can't mix", "NIL", runLisp("(<= 80 4f)"));
        assertEquals("Can't mix", "t", runLisp("(<= 4 80f)"));

        assertEquals("Can't compare float", "NIL", runLisp("(<= 80f 4f)"));
        assertEquals("Can't compare float", "t", runLisp("(<= 4f 80f)"));

        assertEquals("Can't compare multiple", "NIL", runLisp("(<= 80 40 20 10 0)"));
        assertEquals("Can't compare multiple", "t", runLisp("(<= 0 10 20 40 80)"));
        assertEquals("Can't compare multiple", "NIL", runLisp("(<= 80 40 10 20 0)"));
        assertEquals("Can't compare multiple", "t", runLisp("(<= 10 20 20 40 80)"));
    }

    @Test
    public void testEquals()
    {
        assertEquals("Can't compare int", "t", runLisp("(= 80 80)"));
        assertEquals("Can't compare int", "NIL", runLisp("(= 80 40)"));
        assertEquals("Can't compare float", "t", runLisp("(= 80f 80f)"));
        assertEquals("Can't compare float", "NIL", runLisp("(= 80f 40f)"));
        assertEquals("Can't compare mix", "t", runLisp("(= 80 80f)"));
        assertEquals("Can't compare mix", "t", runLisp("(= 80f 80)"));

        assertEquals("Can't compare multiple int ", "t", runLisp("(= 80 80 80 80 80 80)"));
        assertEquals("Can't compare multiple int ", "NIL", runLisp("(= 80 80 80 80 40 80)"));
        assertEquals("Can't compare multiple float", "t", runLisp("(= 80f 80f 80f 80f 80f)"));
        assertEquals("Can't compare multiple float", "NIL", runLisp("(= 80f 80f 80f 40f 80f)"));
        assertEquals("Can't compare multiple mix", "t", runLisp("(= 80 80 80 80f 80)"));
    }

    // abs eq max min

    @Test
    public void testAbs()
    {
        assertEquals("Can't abs int", "50", runLisp("(abs 50)"));
        assertEquals("Can't abs int", "50", runLisp("(abs -50)"));

        assertEquals("Can't abs float", "50.0", runLisp("(abs 50f)"));
        assertEquals("Can't abs float", "50.0", runLisp("(abs -50f)"));
    }

    @Test
    public void testEq()
    {
        assertEquals("Incorrect eq for int", "NIL", runLisp("(eq 50 50)"));
        assertEquals("Incorrect eq for float", "NIL", runLisp("(eq 50f 50f)"));
        assertEquals("Incorrect eq for string", "NIL", runLisp("(eq \"hi\" \"hi\")"));

        // TODO: Test for trues !
    }

    @Test
    public void testMax()
    {
        assertEquals("Incorrect max for int", "9", runLisp("(max 1 9)"));
        assertEquals("Incorrect max for int", "9", runLisp("(max 9 1)"));
        assertEquals("Incorrect max for neg int", "-1", runLisp("(max -9 -1)"));

        assertEquals("Incorrect max for float", "6.0", runLisp("(max 1f 6f)"));
        assertEquals("Incorrect max for float", "6.0", runLisp("(max 6f 1f)"));
        assertEquals("Incorrect max for neg float", "-1.0", runLisp("(max -6f -1f)"));

        assertEquals("Incorrect max for mix", "9.0", runLisp("(max 9f 1)"));
        assertEquals("Incorrect max for mix", "9.0", runLisp("(max 9 1f)"));

        assertEquals("Incorrect max for int", "6", runLisp("(max 1 2 3 4 5 6)"));
        assertEquals("Incorrect max for int", "9", runLisp("(max 1 2 9 4 5 6)"));

        assertEquals("Incorrect max for float", "6.0", runLisp("(max 1f 2f 3f 4f 5f 6f)"));
        assertEquals("Incorrect max for float", "9.0", runLisp("(max 1f 2f 9f 4f 5f 6f)"));
    }

    @Test
    public void testMin()
    {
        assertEquals("Incorrect min for int", "1", runLisp("(min 1 9)"));
        assertEquals("Incorrect min for int", "1", runLisp("(min 9 1)"));
        assertEquals("Incorrect min for neg int", "-9", runLisp("(min -9 -1)"));

        assertEquals("Incorrect min for float", "1.0", runLisp("(min 1f 6f)"));
        assertEquals("Incorrect min for float", "1.0", runLisp("(min 6f 1f)"));
        assertEquals("Incorrect min for neg float", "-6.0", runLisp("(min -6f -1f)"));

        assertEquals("Incorrect min for mix", "1.0", runLisp("(min 9f 1)"));
        assertEquals("Incorrect min for mix", "1.0", runLisp("(min 9 1f)"));

        assertEquals("Incorrect min for int", "1", runLisp("(min 1 2 3 4 5 6)"));
        assertEquals("Incorrect min for int", "-1", runLisp("(min 1 2 -1 4 5 6)"));

        assertEquals("Incorrect min for float", "1.0", runLisp("(min 1f 2f 3f 4f 5f 6f)"));
        assertEquals("Incorrect min for float", "-1.0", runLisp("(min 1f 2f -1f 4f 5f 6f)"));
    }

    @Test
    public void testList()
    {
        // https://www.cs.cmu.edu/Groups/AI/util/html/cltl/clm/node149.html
        assertEquals("Incorrect empty list", "NIL", runLisp("(list)"));
        assertEquals("Incorrect list", "((a b) (c d e))", runLisp("(list (list 'a 'b) (list 'c 'd 'e))"));
        assertEquals("Incorrect car list", "(3 4 a b 4)", runLisp("(list 3 4 'a (car '(b . c)) (+ 6 -2))"));

        assertEquals("Incorrect car list", "(3 4 a b 4)", runLisp("(list 3 4 'a (car '(b . c)) (+ 6 -2))"));
        assertEquals("Incorrect car list", "((a b) (c d e))", runLisp("(list (list 'a 'b) (list 'c 'd 'e))"));
    }

    @Test
    public void testCar()
    {
        // https://www.cs.cmu.edu/Groups/AI/util/html/cltl/clm/node148.html
        assertEquals("Incorrect empty list", "NIL", runLisp("(car ())"));
        assertEquals("Incorrect empty list", "a", runLisp("(car '(a b c))"));
    }

    @Test
    public void testCdr()
    {
        // https://www.cs.cmu.edu/Groups/AI/util/html/cltl/clm/node148.html
        assertEquals("Incorrect empty list", "NIL", runLisp("(cdr ())"));
        assertEquals("Incorrect list", "(b c)", runLisp("(cdr '(a b c))"));
    }

    @Test
    public void testCadaddadr()
    {
        assertEquals("Incorrect empty list", "NIL", runLisp("(cadadadadadadaddr ())"));
        assertEquals("Incorrect empty list", "NIL", runLisp("(cdadadadadadadar ())"));
        assertEquals("Incorrect empty list", "NIL", runLisp("(cadr ())"));
        assertEquals("Incorrect empty list", "NIL", runLisp("(cdar ())"));
        assertEquals("Incorrect empty list", "NIL", runLisp("(caaar ())"));
        assertEquals("Incorrect empty list", "NIL", runLisp("(cdddr ())"));

        assertEquals("Incorrect list", "2", runLisp("(cadr '(1 2 3 4 5 6 7 8 9))"));
        assertEquals("Incorrect list", "(5 6 7 8 9)", runLisp("(cddddr '(1 2 3 4 5 6 7 8 9))"));
        assertEquals("Incorrect list", "5", runLisp("(caddddr '(1 2 3 4 5 6 7 8 9))"));
        assertEquals("Incorrect list", "(7 8 9)", runLisp("(cddddddr '(1 2 3 4 5 6 7 8 9))"));
        assertEquals("Incorrect list", "7", runLisp("(caddddddr '(1 2 3 4 5 6 7 8 9))"));
        assertEquals("Incorrect list", "NIL", runLisp("(cddddddddddddddddr '(1 2 3 4 5 6 7 8 9))"));
    }

    @Test
    public void testCons()
    {
        assertEquals("incoeeect cons", "(a)", runLisp("(cons 'a '())"));
        assertEquals("incorrect cons", "(a)", runLisp("(cons 'a nil)"));
        assertEquals("incorrect cons", "(a b)", runLisp("(cons 'a '(b))"));
        assertEquals("Incorrect cons", "b", runLisp("(car '(b . c))"));
        assertEquals("Incorrect cons", "b", runLisp("(car (cons 'b 'c))"));
        assertEquals("Incorrect cons", "c", runLisp("(cdr '(b . c))"));
        assertEquals("Incorrect cons", "c", runLisp("(cdr (cons 'b 'c))"));
        assertEquals("Incorrect cons", "(b . c)", runLisp("(cons 'b 'c)"));
        assertEquals("Incorrect cons", "(a b c . d)", runLisp("(cons 'a (cons 'b (cons 'c 'd)))"));
        assertEquals("Incorrect cons", "(a b c)", runLisp("(cons 'a (cons 'b (cons 'c nil)))"));
        assertEquals("Incorrect cons", "(1 2 3)", runLisp("(cons 1 '(2 3))"));
    }

    @Test
    public void testLambda()
    {
        assertEquals("Incorrect lambda", "19", runLisp("((lambda (a b) (+ a (* b 3))) 4 5)"));
    }

    @Test
    public void testDefun()
    {
        assertEquals("Incorrect defun", "19", runLisp("(progn (defun foo (a b) (+ a (* b 3))) (foo 4 5))"));
    }

    @Test
    public void testProgn()
    {
        assertEquals("Incorrect progn", "6", runLisp("(progn (+ 1 1) (+ 2 2) (+ 3 3))"));
    }

    @Test
    public void testAppend()
    {
        assertEquals("Incorrect append", "(1 2 3 4)", runLisp("(append '(1 2) '(3 4))"));
        assertEquals("Incorrect append", "(1 2 3 a 5 6)", runLisp("(append '(1 2 3) '() '(a) '(5 6))"));
        assertEquals("Incorrect append", "(a b c . d)", runLisp("(append '(a b c) 'd)"));
    }

    @Test
    public void testNTH()
    {
        assertEquals("Incorrect append", "foo", runLisp("(nth 0 '(foo bar gack))"));
        assertEquals("Incorrect append", "bar", runLisp("(nth 1 '(foo bar gack))"));
        assertEquals("Incorrect append", "NIL", runLisp("(nth 3 '(foo bar gack))"));

        assertEquals("Incorrect append", "1", runLisp("(first '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "2", runLisp("(second '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "3", runLisp("(third '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "4", runLisp("(fourth '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "5", runLisp("(fifth '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "6", runLisp("(sixth '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "7", runLisp("(seventh '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "8", runLisp("(eighth '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "9", runLisp("(ninth '(1 2 3 4 5 6 7 8 9 10))"));
        assertEquals("Incorrect append", "10", runLisp("(tenth '(1 2 3 4 5 6 7 8 9 10))"));
    }

    @Test
    public void testCond()
    {
        assertEquals("Incorrect cond", "NIL", runLisp("(cond)"));
        assertEquals("Incorrect cond", "NIL", runLisp("(cond ())"));
        assertEquals("Incorrect cond", "NIL", runLisp("(cond () ())"));
        assertEquals("Incorrect cond", "a", runLisp("(cond ('a) ())"));
        assertEquals("Incorrect cond", "a", runLisp("(cond () ('a))"));
        assertEquals("Incorrect cond", "b", runLisp("(cond ('a 'b) ())"));
        assertEquals("Incorrect cond", "b", runLisp("(cond () ('a 'b))"));
    }

    @Test
    public void testRecursive()
    {
        runLisp(lisp ->
        {
            lisp.run("(defun facult (c) (if (= c 0) 1 (* c (facult (- c 1))))    )");

            assertEquals("Incorrect cond", "1", lisp.run("(facult 0)"));
            assertEquals("Incorrect cond", "1", lisp.run("(facult 1)"));
            assertEquals("Incorrect cond", "2", lisp.run("(facult 2)"));
            assertEquals("Incorrect cond", "6", lisp.run("(facult 3)"));
            assertEquals("Incorrect cond", "3628800", lisp.run("(facult 10)"));
        });

        runLisp(lisp ->
        {
            lisp.run("(defun facult (c) (cond ((= c 0) 1) ((* c (facult (- c 1))))))");

            assertEquals("Incorrect cond", "1", lisp.run("(facult 0)"));
            assertEquals("Incorrect cond", "1", lisp.run("(facult 1)"));
            assertEquals("Incorrect cond", "2", lisp.run("(facult 2)"));
            assertEquals("Incorrect cond", "6", lisp.run("(facult 3)"));
            assertEquals("Incorrect cond", "3628800", lisp.run("(facult 10)"));
        });
    }

    @Test
    public void testSharedStructures()
    {
        runLisp(lisp ->
        {
            lisp.run("(setf foo (list 'a 'b 'c)) (setf bar (cons 'x (cdr foo))) (setf (third foo) 'goose) foo");

            assertEquals("Incorrect append", "(a b goose)", lisp.run("foo"));
            assertEquals("Incorrect append", "(x b goose)", lisp.run("bar"));
        });

        assertEquals("Incorrect append", "(1 2 3 4)", runLisp("(append '(1 2) '(3 4))"));
        assertEquals("Incorrect append", "(1 2 3 a 5 6)", runLisp("(append '(1 2 3) '() '(a) '(5 6))"));
        assertEquals("Incorrect append", "(a b c . d)", runLisp("(append '(a b c) 'd)"));
    }

    @Test
    public void testSet()
    {
        assertEquals("Incorrect set", "NIL", runLisp("(set)"));
        assertEquals("Incorrect set", "(6)", runLisp("(set 'x (+ 3 2 1) 'y (cons x nil))"));
    }

    @Test
    public void testSetq()
    {
        assertEquals("Incorrect setq", "NIL", runLisp("(setq)"));
        assertEquals("Incorrect setq", "(6)", runLisp("(setq x (+ 3 2 1) y (cons x nil))"));
    }

    @Test
    public void testSetf()
    {
        assertEquals("Incorrect setf", "NIL", runLisp("(setf)"));
        assertEquals("Incorrect setf", "(6)", runLisp("(setf x (+ 3 2 1) y (cons x nil))"));

        runLisp(lisp ->
        {
            assertEquals("Incorrect let", "(1 2 3)", lisp.run("(setq x (cons 'a 'b) y (list 1 2 3))"));
            assertEquals("Incorrect let", "(1 x 3)", lisp.run("(setf (car x) 'x (cadr y) (car x) (cdr x) y)"));
            assertEquals("Incorrect let", "(x 1 x 3)", lisp.run("x"));
            assertEquals("Incorrect let", "(1 x 3)", lisp.run("y"));

            /*
             * assertEquals("Incorrect let",
             * "(1 2 3)",lisp.run("(setq x (cons 'a 'b) y (list 1 2 3))"));
             * assertEquals("Incorrect let", "NIL",
             * lisp.run("(psetf (car x) 'x (cadr y) (car x) (cdr x) y)"));
             * assertEquals("Incorrect let", "(x 1 a 3)", lisp.run("x"));
             * assertEquals("Incorrect let", "(1 a 3)", lisp.run("y"));
             */
        });
    }

    @Test
    public void testLet()
    {
        runLisp(lisp ->
        {
            assertEquals("Incorrect let", "NIL", lisp.run("(setq o nil)"));
            assertEquals("Incorrect let", "top", lisp.run("(setq a 'top)"));
            assertEquals("Incorrect let", "dummy-function", lisp.run("(defun dummy-function () a)"));
            
            assertEquals("Incorrect let", "(inside top top)", lisp.run("(let ((a 'inside) (b a))\r\n" +
                    "                                                       (setq o (list a b (dummy-function))))"));
            
            assertEquals("Incorrect let", "(inside inside top)", lisp.run("(let* ((a 'inside) (b a))\r\n" +
                    "                                                       (setq o (list a b (dummy-function))))"));
            
            /*   Needs (declare (special a))
            assertEquals("Incorrect let", "(inside top inside)", lisp.run("(let ((a 'inside) (b a)) (declare (special a)) \r\n" + 
                    "                                                       (setq o (list a b (dummy-function))))"));
            */
            
        });
    }
}
