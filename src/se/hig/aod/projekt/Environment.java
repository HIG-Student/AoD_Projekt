package se.hig.aod.projekt;

import java.util.HashMap;

class Environment
{
    Environment outer = null;
    HashMap<String, PartContainer> env = new HashMap<String, PartContainer>();

    public Environment()
    {

    }

    public Environment(Environment outer)
    {
        this.outer = outer;
    }

    public PartContainer get(String key)
    {
        if (env.containsKey(key))
            return env.get(key);
        else
            if (outer != null)
                return outer.get(key);
            else
                throw new NoSuchVariable("Variable [" + key + "] is null!");
    }

    public boolean contains(String key)
    {
        return env.containsKey(key) || (outer != null && outer.contains(key));
    }

    public PartContainer set(String key, PartContainer entry)
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