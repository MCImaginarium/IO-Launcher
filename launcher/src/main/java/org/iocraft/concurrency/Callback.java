/*
* WorldAutomation.Net Launcher
* Based off of sk89q's Source
* https://www.worldautomation.net
*/

package org.iocraft.concurrency;

public interface Callback<T> {

    void handle(T value);

}
