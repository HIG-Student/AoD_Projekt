package se.hig.aod.projekt;


class CharactersToParseStack
{
    public final String source;
    private int index = 0;

    CharactersToParseStack(String source)
    {
        this.source = source;
    }

    int getSize()
    {
        return source.length() - index;
    }

    char pop()
    {
        if (getSize() == 0)
            throw new StackEmpty("No more chars");

        return source.charAt(index++);
    }

    void unpop()
    {
        if (index == 0)
            throw new OutOfBounds("No more chars");

        index--;
    }

    char peak()
    {
        return peak(0);
    }

    char peak(int pos)
    {
        if (getSize() == 0)
            throw new StackEmpty("No more chars");

        if (index + pos < 0 || index + pos >= source.length())
            throw new OutOfBounds("Out of bounds");

        return source.charAt(index + pos);
    }

    boolean nextIs(char c)
    {
        if (getSize() == 0)
            throw new StackEmpty("No more chars");

        return peak() == c;
    }

    boolean nextIs(String s)
    {
        if (getSize() == 0)
            throw new StackEmpty("No more chars");

        if (getSize() < s.length())
            return false;

        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) != peak(i + 1))
                return false;

        return true;
    }

    boolean nextIsSpace()
    {
        return isSpace(peak());
    }

    boolean isSpace(char c)
    {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    boolean isParseEnd(char c)
    {
        return c == ')' || isSpace(c);
    }

    boolean isStringBreak(char c)
    {
        return c == '\n' || c == '\r';
    }

    boolean clean()
    {
        char character = peak();
        if (isSpace(character))
        {
            pop();
            clean();
            return true;
        }

        if (character == ';')
        {
            while (!isStringBreak(character = pop()))
            {
            }
            clean();
            return true;
        }

        if (character == '#' && peak(1) == '|')
        {
            while ((character = pop()) != '#')
            {
                if (getSize() == 0)
                    throw new SyntaxError("Unclosed multiline-comment!");

                if (character == '#' && peak(-1) == '|')
                    break;
            }
            clean();
            return true;
        }

        return false;
    }

    @SuppressWarnings("serial")
    static class StackEmpty extends LispException
    {
        public StackEmpty(String message)
        {
            super(message);
        }
    }

    @SuppressWarnings("serial")
    static class OutOfBounds extends LispException
    {
        public OutOfBounds(String message)
        {
            super(message);
        }
    }
}