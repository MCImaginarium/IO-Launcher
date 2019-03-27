/*
* IO-Launcher Launcher
* Based off of sk89q's Source
* https://www.iocraft.org
*/

package com.launcher.concurrency;

public interface Callback<T> {

    void handle(T value);

}
