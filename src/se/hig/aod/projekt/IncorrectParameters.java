package se.hig.aod.projekt;

/**
 * Indicates that the supplied parameters are incorrect
 *
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
@SuppressWarnings("serial")
public class IncorrectParameters extends LispException
{
    IncorrectParameters()
    {
    }

    IncorrectParameters(String message)
    {
        super(message);
    }
}