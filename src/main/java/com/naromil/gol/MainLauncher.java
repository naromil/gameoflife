package com.naromil.gol;

public class MainLauncher {
    public static void main(String[] args) {
        int duration = 60;
        int gridHeight = 10, gridWidth = 16;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-t") || args[i].equals("--duration")) {
                if (i + 1 < args.length) {
                    duration = Integer.parseInt(args[i + 1]);
                } else {
                    System.err.println("Error: --duration needs an number.");
                }
            }
            if (args[i].equals("-h") || args[i].equals("--height")) {
                if (i + 1 < args.length) {
                    gridHeight = Integer.parseInt(args[i + 1]);
                } else {
                    System.err.println("Error: --height needs an number.");
                }
            }
            if (args[i].equals("-w") || args[i].equals("--width")) {
                if (i + 1 < args.length) {
                    gridWidth = Integer.parseInt(args[i + 1]);
                } else {
                    System.err.println("Error: --width needs an number.");
                }
            }
        }
        Sim.main(duration, gridHeight, gridWidth);
  }
}
