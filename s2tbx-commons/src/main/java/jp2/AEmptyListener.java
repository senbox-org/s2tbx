package jp2;

public class AEmptyListener implements BoxReader.Listener
{

    @Override
    public void knownBoxSeen(Box box) {
        // do nothing
    }

    @Override
    public void unknownBoxSeen(Box box) {
        // do nothing
    }
}
