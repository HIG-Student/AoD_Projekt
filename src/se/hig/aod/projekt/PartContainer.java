package se.hig.aod.projekt;

class PartContainer
{
    private Part part;

    public PartContainer(Part part)
    {
        this.part = part;
    }

    public Part getPart()
    {
        return part;
    }

    public void setPart(Part part)
    {
        this.part = part;
    }

    @Override
    public boolean equals(Object other)
    {
        return other != null && part != null && other instanceof PartContainer && part.equals(((PartContainer) other).part);
    }

    @Override
    public String toString()
    {
        return asString();
    }

    public String asString()
    {
        return part.asString();
    }

    public static Part[] asParts(PartContainer[] containers)
    {
        Part[] result = new Part[containers.length];
        for (int i = 0; i < containers.length; i++)
            result[i] = containers[i].getPart();

        return result;
    }
}
