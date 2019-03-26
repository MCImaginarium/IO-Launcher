/*
 * SKCraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package org.iocraft.launcher;

import com.launcher.concurrency.ObservableFuture;
import org.iocraft.launcher.dialog.ProgressDialog;
import org.iocraft.launcher.swing.SwingHelper;
import org.iocraft.launcher.update.HardResetter;
import org.iocraft.launcher.update.Remover;
import org.iocraft.launcher.util.SharedLocale;

import java.awt.*;

import static org.iocraft.launcher.util.SharedLocale.tr;

public class InstanceTasks {

    private final FancyLauncher launcher;

    public InstanceTasks(FancyLauncher launcher) {
        this.launcher = launcher;
    }

    public ObservableFuture<Instance> delete(Window window, Instance instance) {
        // Execute the deleter
        Remover resetter = new Remover(instance);
        ObservableFuture<Instance> future = new ObservableFuture<>(
                launcher.getExecutor().submit(resetter), resetter);

        // Show progress
        ProgressDialog.showProgress(
                window, future, SharedLocale.tr("instance.deletingTitle"), tr("instance.deletingStatus", instance.getTitle()));
        SwingHelper.addErrorDialogCallback(window, future);

        return future;
    }

    public ObservableFuture<Instance> hardUpdate(Window window, Instance instance) {
        // Execute the resetter
        HardResetter resetter = new HardResetter(instance);
        ObservableFuture<Instance> future = new ObservableFuture<>(
                launcher.getExecutor().submit(resetter), resetter);

        // Show progress
        ProgressDialog.showProgress(window, future, SharedLocale.tr("instance.resettingTitle"),
                tr("instance.resettingStatus", instance.getTitle()));
        SwingHelper.addErrorDialogCallback(window, future);

        return future;
    }

    public ObservableFuture<InstanceList> reloadInstances(Window window) {
        InstanceList.Enumerator loader = launcher.getInstances().createEnumerator();
        ObservableFuture<InstanceList> future = new ObservableFuture<>(launcher.getExecutor().submit(loader), loader);

        ProgressDialog.showProgress(window, future, SharedLocale.tr("launcher.checkingTitle"), SharedLocale.tr("launcher.checkingStatus"));
        SwingHelper.addErrorDialogCallback(window, future);

        return future;
    }

}
