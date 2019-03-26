/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package org.iocraft.launcher.install;

import org.iocraft.concurrency.ProgressObservable;

public interface InstallTask extends ProgressObservable {

    void execute() throws Exception;

}
