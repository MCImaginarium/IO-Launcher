/*
* WorldAutomation.Net Launcher
* Based off of sk89q's Source
* https://www.worldautomation.net
*/

package org.iocraft.concurrency;

import lombok.Data;

/**
 * A simple default implementation of {@link org.iocraft.concurrency.ProgressObservable}
 * with settable properties.
 */
@Data
public class DefaultProgress implements ProgressObservable {

    private String status;
    private double progress = -1;

    public DefaultProgress() {
    }

    public DefaultProgress(double progress, String status) {
        this.progress = progress;
        this.status = status;
    }
}
