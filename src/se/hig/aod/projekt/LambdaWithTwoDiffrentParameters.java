package se.hig.aod.projekt;

interface LambdaWithTwoDiffrentParameters<P1, P2, R>
{
    public R exec(P1 arg1, P2 arg2);
}