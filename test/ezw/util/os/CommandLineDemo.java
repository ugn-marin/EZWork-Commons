package ezw.util.os;

import ezw.util.Sugar;
import ezw.util.calc.Units;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CommandLineDemo {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        var hostnameCommand = new CommandLine(true, "hostname");
        hostnameCommand.setNoPrints();
        var hostname = hostnameCommand.call();
        System.out.printf("%s, returned %d in %s%n", Sugar.first(hostname.getOutput()).getLine(),
                hostname.getExitStatus(), Units.Time.describeNano(hostname.getNanoTimeTook()));

        System.out.println(new Ping("google.com").attempt());

        System.out.println(new JVM(Ping.class, "speedtest.net").attempt());
        System.out.println(new JVM(Ping.class, "unknownhost").attempt());
        System.out.println(new JVM(Ping.class).attempt());
    }

    private static class Ping extends CommandLine {

        public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
            System.exit(new Ping(args[0]).call().getExitStatus());
        }

        Ping(String host) {
            super("ping", host);
        }
    }
}
