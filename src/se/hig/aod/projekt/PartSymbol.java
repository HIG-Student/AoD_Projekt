package se.hig.aod.projekt;

class PartSymbol extends PartAtom
{
    public final String value;

    PartSymbol(String value)
    {
        this.value = value;
    }

    @Override
    public String asString()
    {
        return value;
    }
}