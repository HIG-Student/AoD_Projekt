package se.hig.aod.projekt;

import java.util.Scanner;

/**
 * A REPL for my lisp interpreter
 *
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class Lisp_REPL
{
    /**
     * Runs the Lisp REPL, using the std(in & out)
     * 
     * @param args
     *            // ignored
     */
    public static void main(String[] args)
    {
        System.out.println("Welcome to my lisp interpreter!");
        System.out.println("Just start typing and let it do its magic!");
        System.out.println("");
        System.out.print("> ");
        Lisp lisp = new Lisp();
        
        // lisp.run("(defun loop (n f) ((defun sub_loop (i) (if (> i 0) (progn (print i) (eval f) (sub_loop (- i 1))))) n))");
        
        lisp.run("(setq i 0)");
        lisp.run("(defun loop (n f) (while (> (setq n (- n 1)) 0) (eval f)))");
        lisp.run("(loop 5 ''(setq i (+ i 1)))");
        
        //lisp.run("(defun loop (n f) (if (> n 0) (progn (print (* n 10)) (print (eval f)) (loop (- n 1) f) (print (* n 100)) ) n))");
        //lisp.run("(loop 4 '(print n))");

        try (Scanner scan = new Scanner(System.in))
        {
            String code;
            while (!(code = scan.nextLine()).equals("exit"))
            {
                try
                {
                    System.out.println(lisp.run(code));
                }
                catch (Exception e)
                {
                    System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                }

                System.out.print("> ");
            }
        }
    }
}
