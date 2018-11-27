package com.company;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.print("Enter file path: ");
        Scanner scan = new Scanner(System.in);
        String path = scan.next();

        Automat automat;
        try(FileReader f = new FileReader(path))
        {
            scan = new Scanner(f);
            int a = 0, s = 0, q = 0, fn = 0;
            if (check(scan))
                a = scan.nextInt();
            if (check(scan))
                s = scan.nextInt();
            if (check(scan))
                q = scan.nextInt();
            if (check(scan))
                fn = scan.nextInt();
            automat = new Automat(a, s, q, fn);
            for (int i=0; i<fn; i++) {
                if (check(scan))
                    a = scan.nextInt();
                automat.setFinalState(i, a);
            }
            String line = new String();
            while (scan.hasNextLine()) {
                String ch = "";
                char c;
                a = scan.nextInt();
                if (check(scan)) ch = scan.next();
                if (check(scan)) s = scan.nextInt();
                c = ch.charAt(0);
                automat.setFunc(a, c, s);
            }
            if (!automat.isCorrect())
                System.out.println("Not correct!");

            automat.inaccessible();
            automat.dead_end();
            automat.equiv();
            automat.outAvtomat();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static public boolean check(Scanner a) throws Exception {
        if (!a.hasNext())
            throw new Exception("Not correct file");
        return true;
    }
}
