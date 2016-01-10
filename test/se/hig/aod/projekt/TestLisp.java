package se.hig.aod.projekt;

import static org.junit.Assert.*;

import org.junit.Test;

import se.hig.aod.projekt.Lisp.Part;

@SuppressWarnings("javadoc")
public class TestLisp
{
    public String runLisp(String code)
    {
        return new Lisp().run(code);
    }

    public Part runLispAndReturnPart(String code)
    {
        return new Lisp().runAndReturnPart(code);
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
        assertEquals("Can't compare int", "true", runLisp("(> 80 4)"));
        assertEquals("Can't compare neg int", "true", runLisp("(> 4 -4)"));
        assertEquals("Can't compare int", "false", runLisp("(> 4 80)"));
        assertEquals("Can't compare neg int", "false", runLisp("(> -4 4)"));

        assertEquals("Can't mix", "true", runLisp("(> 80f 4)"));
        assertEquals("Can't mix", "false", runLisp("(> 4f 80)"));

        assertEquals("Can't mix", "true", runLisp("(> 80 4f)"));
        assertEquals("Can't mix", "false", runLisp("(> 4 80f)"));

        assertEquals("Can't compare float", "true", runLisp("(> 80f 4f)"));
        assertEquals("Can't compare neg float", "true", runLisp("(> 4f -4f)"));
        assertEquals("Can't compare float", "false", runLisp("(> 4f 80f)"));
        assertEquals("Can't compare neg float", "false", runLisp("(> -4f 4f)"));

        assertEquals("Can't compare multiple", "true", runLisp("(> 80 40 20 10 0)"));
        assertEquals("Can't compare multiple", "false", runLisp("(> 0 10 20 40 80)"));
        assertEquals("Can't compare multiple", "false", runLisp("(> 80 40 10 20 0)"));
        assertEquals("Can't compare multiple", "false", runLisp("(> 80 40 20 20 10)"));
    }

    @Test
    public void testLT()
    {
        assertEquals("Can't compare int", "false", runLisp("(< 80 4)"));
        assertEquals("Can't compare int", "true", runLisp("(< 4 80)"));

        assertEquals("Can't mix", "false", runLisp("(< 80f 4)"));
        assertEquals("Can't mix", "true", runLisp("(< 4f 80)"));

        assertEquals("Can't mix", "false", runLisp("(< 80 4f)"));
        assertEquals("Can't mix", "true", runLisp("(< 4 80f)"));

        assertEquals("Can't compare float", "false", runLisp("(< 80f 4f)"));
        assertEquals("Can't compare float", "true", runLisp("(< 4f 80f)"));

        assertEquals("Can't compare multiple", "false", runLisp("(< 80 40 20 10 0)"));
        assertEquals("Can't compare multiple", "true", runLisp("(< 0 10 20 40 80)"));
        assertEquals("Can't compare multiple", "false", runLisp("(< 80 40 10 20 0)"));
        assertEquals("Can't compare multiple", "false", runLisp("(< 10 20 20 40 80)"));
    }

    @Test
    public void testGTE()
    {
        assertEquals("Can't compare int", "true", runLisp("(>= 80 4)"));
        assertEquals("Can't compare int", "false", runLisp("(>= 4 80)"));

        assertEquals("Can't mix", "true", runLisp("(>= 80f 4)"));
        assertEquals("Can't mix", "false", runLisp("(>= 4f 80)"));

        assertEquals("Can't mix", "true", runLisp("(>= 80 4f)"));
        assertEquals("Can't mix", "false", runLisp("(>= 4 80f)"));

        assertEquals("Can't compare float", "true", runLisp("(>= 80f 4f)"));
        assertEquals("Can't compare float", "false", runLisp("(>= 4f 80f)"));

        assertEquals("Can't compare multiple", "true", runLisp("(>= 80 40 20 10 0)"));
        assertEquals("Can't compare multiple", "false", runLisp("(>= 0 10 20 40 80)"));
        assertEquals("Can't compare multiple", "false", runLisp("(>= 80 40 10 20 0)"));
        assertEquals("Can't compare multiple", "true", runLisp("(>= 80 40 20 20 10)"));
    }

    @Test
    public void testLTE()
    {
        assertEquals("Can't compare int", "false", runLisp("(<= 80 4)"));
        assertEquals("Can't compare int", "true", runLisp("(<= 4 80)"));

        assertEquals("Can't mix", "false", runLisp("(<= 80f 4)"));
        assertEquals("Can't mix", "true", runLisp("(<= 4f 80)"));

        assertEquals("Can't mix", "false", runLisp("(<= 80 4f)"));
        assertEquals("Can't mix", "true", runLisp("(<= 4 80f)"));

        assertEquals("Can't compare float", "false", runLisp("(<= 80f 4f)"));
        assertEquals("Can't compare float", "true", runLisp("(<= 4f 80f)"));

        assertEquals("Can't compare multiple", "false", runLisp("(<= 80 40 20 10 0)"));
        assertEquals("Can't compare multiple", "true", runLisp("(<= 0 10 20 40 80)"));
        assertEquals("Can't compare multiple", "false", runLisp("(<= 80 40 10 20 0)"));
        assertEquals("Can't compare multiple", "true", runLisp("(<= 10 20 20 40 80)"));
    }

    @Test
    public void testEquals()
    {
        assertEquals("Can't compare int", "true", runLisp("(= 80 80)"));
        assertEquals("Can't compare int", "false", runLisp("(= 80 40)"));
        assertEquals("Can't compare float", "true", runLisp("(= 80f 80f)"));
        assertEquals("Can't compare float", "false", runLisp("(= 80f 40f)"));
        assertEquals("Can't compare mix", "true", runLisp("(= 80 80f)"));
        assertEquals("Can't compare mix", "true", runLisp("(= 80f 80)"));

        assertEquals("Can't compare multiple int ", "true", runLisp("(= 80 80 80 80 80 80)"));
        assertEquals("Can't compare multiple int ", "false", runLisp("(= 80 80 80 80 40 80)"));
        assertEquals("Can't compare multiple float", "true", runLisp("(= 80f 80f 80f 80f 80f)"));
        assertEquals("Can't compare multiple float", "false", runLisp("(= 80f 80f 80f 40f 80f)"));
        assertEquals("Can't compare multiple mix", "true", runLisp("(= 80 80 80 80f 80)"));
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
        assertEquals("Incorrect eq for int", "false", runLisp("(eq 50 50)"));
        assertEquals("Incorrect eq for float", "false", runLisp("(eq 50f 50f)"));
        assertEquals("Incorrect eq for string", "false", runLisp("(eq \"hi\" \"hi\")"));

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
        assertEquals("Incorrect empty list", "[]", runLisp("(list)"));
        assertEquals("Incorrect list", "[[a, b], [c, d, e]]", runLisp("(list (list 'a 'b) (list 'c 'd 'e))"));
        assertEquals("Incorrect car list", "[3, 4, a, b, 4]", runLisp("(list 3 4 'a (car '(b . c)) (+ 6 -2))"));
    }

    @Test
    public void testCar()
    {
        // https://www.cs.cmu.edu/Groups/AI/util/html/cltl/clm/node148.html
        assertEquals("Incorrect empty list", "[]", runLisp("(car ())"));
        assertEquals("Incorrect empty list", "a", runLisp("(car '(a b c))"));
    }

    @Test
    public void testCdr()
    {
        // https://www.cs.cmu.edu/Groups/AI/util/html/cltl/clm/node148.html
        assertEquals("Incorrect empty list", "[]", runLisp("(cdr ())"));
        assertEquals("Incorrect list", "[b, c]", runLisp("(cdr '(a b c))"));
    }
}
