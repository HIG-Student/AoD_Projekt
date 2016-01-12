package se.hig.aod.projekt;

interface Lambda<P, R>
{
    public R exec(@SuppressWarnings("unchecked") P... args);
}