package fbot.lib.core.auxi;

public class ProgressTracker {
    private int curr = 0;
    private int end;
    private String msg = "Processing item %d of %d";

    public ProgressTracker(int end) {
        this.end = end;
    }

    public synchronized void setMessage(String msg) {
        this.msg = msg;
    }

    public synchronized int inc() {
        return inc("");
    }

    public synchronized int inc(String head) {
        Logger.log(
                head
                        + String.format(this.msg,
                                new Object[] { Integer.valueOf(++this.curr),
                                        Integer.valueOf(this.end) }), "green");
        return this.curr;
    }
}
