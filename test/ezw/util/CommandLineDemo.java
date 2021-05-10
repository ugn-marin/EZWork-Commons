package ezw.util;

import ezw.util.calc.Units;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CommandLineDemo {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        var hostname = new CommandLine("hostname").call();
        System.out.printf("%s, returned %d in %s%n", Sugar.first(hostname.getOutput()).getLine(),
                hostname.getExitStatus(), Units.Time.describeNano(hostname.getNanoTimeTook()));
        System.out.println(new Ping("google.com").attempt());
    }

    private static class Ping extends CommandLine {

        Ping(String host) {
            super("ping", host);
        }
    }
}
