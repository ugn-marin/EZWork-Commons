package ezw.util;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CommandLineDemo {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        new CommandLine(true, "hostname").call();
        System.out.println(new Ping("google.com").attempt());
    }

    static class Ping extends CommandLine {

        Ping(String host) {
            super(true, "ping", host);
        }
    }
}
