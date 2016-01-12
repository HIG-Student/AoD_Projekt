package se.hig.aod.projekt;

import java.util.HashMap;

class Environment
{
    Environment outer = null;
    @SuppressWarnings("serial")
    HashMap<String, Part> env = new HashMap<String, Part>();

    public Environment()
    {

    }

    public Environment(Environment outer)
    {
        this.outer = outer;
    }

    public Part get(String key)
    {
        if (env.containsKey(key))
            return env.get(key);
        else
            if (outer != null)
                return outer.get(key);
            else
                throw new NoSuchVariable("Variable [" + key + "] is null!");
    }

    public Part set(String key, Part entry)
    {
        env.put(key, entry);
        return entry;
    }

    @SuppressWarnings("serial")
    class NoSuchVariable extends LispException
    {
        NoSuchVariable(String msg)
        {
            super(msg);
        }
    }
}