package se.hig.aod.projekt;

/**
 * Indicates that there is something wrong with the syntax
 *
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
@SuppressWarnings("serial")
public class SyntaxError extends LispException
{
    SyntaxError(String message)
    {
        super(message);
    }

    SyntaxError()
    {
    }
}