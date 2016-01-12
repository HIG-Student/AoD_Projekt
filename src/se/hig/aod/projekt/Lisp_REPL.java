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
