package ezw.util;

import ezw.concurrent.Concurrent;
import ezw.util.calc.Units;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

public class CommandLine implements Callable<CommandLine.CommandLineResult> {

    public static class CommandLineResult {
        private int exitStatus = -1;
        private final List<CommandLineOutputLine> output = new ArrayList<>(1);
        private final ReentrantLock outputLock = new ReentrantLock(true);
        private boolean errorPrints = false;
        private long nanoTimeTook;

        public boolean isSuccessful() {
            return exitStatus == 0 && !errorPrints;
        }

        public int getExitStatus() {
            return exitStatus;
        }

        void setExitStatus(int exitStatus) {
            this.exitStatus = exitStatus;
        }

        public long getNanoTimeTook() {
            return nanoTimeTook;
        }

        void setNanoTimeTook(long nanoTimeTook) {
            this.nanoTimeTook = nanoTimeTook;
        }

        public List<CommandLineOutputLine> getOutput() {
            return output;
        }

        void addOutput(CommandLineOutputLine line) {
            outputLock.lock();
            try {
                output.add(line);
                errorPrints |= line.isError();
                (line.isError() ? System.err : System.out).println(line.getLine());
            } finally {
                outputLock.unlock();
            }
        }
    }

    public static class CommandLineOutputLine {
        private final Date timestamp = new Date();
        private final String line;
        private final boolean isError;

        public CommandLineOutputLine(String line, boolean isError) {
            this.line = line;
            this.isError = isError;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public String getLine() {
            return line;
        }

        public boolean isError() {
            return isError;
        }
    }

    protected String[] command;
    private final boolean collectOutput;
    protected Process process;
    protected CommandLineResult result;
    protected long startNano;

    public CommandLine(boolean collectOutput, Object... command) {
        if (command.length == 0)
            throw new IllegalArgumentException("No commands received.");
        this.command = Arrays.stream(command).filter(Objects::nonNull).map(Object::toString).toArray(String[]::new);
        this.collectOutput = collectOutput;
    }

    @Override
    public CommandLineResult call() throws IOException, InterruptedException, ExecutionException {
        result = new CommandLineResult();
        var builder = new ProcessBuilder(command);
        builder.redirectErrorStream(collectOutput);
        startNano = System.nanoTime();
        process = builder.start();
        if (collectOutput)
            Concurrent.getAll(Concurrent.submit(getOutputReader(false)),
                    Concurrent.submit(getOutputReader(true)));
        result.setExitStatus(process.waitFor());
        result.setNanoTimeTook(Units.Time.sinceNano(startNano));
        return result;
    }

    public boolean attempt() {
        return Sugar.orElse(() -> call().isSuccessful(), false);
    }

    private Runnable getOutputReader(boolean isError) {
        return () -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(isError ? process.getErrorStream() :
                    process.getInputStream()))) {
                br.lines().forEach(line -> result.addOutput(new CommandLineOutputLine(line, isError)));
            } catch (IOException e) {
                result.addOutput(new CommandLineOutputLine(e.toString(), true));
            }
        };
    }

    public CommandLineResult getLastResult() {
        return result;
    }

    public void stop() {
        if (process != null)
            process.destroy();
    }
}
