package se.hig.aod.projekt;

abstract class Part
{
    public abstract String asString();

    @Override
    public String toString()
    {
        return asString();
    }
}