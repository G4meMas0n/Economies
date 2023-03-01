package com.github.g4memas0n.economies.storage;

public interface Transaction {

    void abort();

    void begin();

    void end();

}
