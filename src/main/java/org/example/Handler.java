package org.example;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;

@FunctionalInterface
public interface Handler {
    void handle(Request request, BufferedOutputStream responseStream) throws FileNotFoundException;
}
