// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.data;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;

@ApplicationScoped
public class DbInit {

    @Inject
    Packages packages;

    public void init(@Observes Startup event) {
        // Liberty Dev mode restarts the app without restarting the JVM, which results
        // in the Db not being cleared from memory, so only add the packages if nothing
        // exists.
        if (packages.findAll().count() == 0) {
            packages.insert(new Package(1, 10f, 20f, 10f, "Rochester"));
            packages.insert(new Package(2, 30f, 10f, 10f, "Austin"));
            packages.insert(new Package(3, 5f, 10f, 5f, "RTP"));
            packages.insert(new Package(4, 24f, 15f, 6f, "Rochester"));
            packages.insert(new Package(5, 15f, 7f, 2f, "Austin"));
            packages.insert(new Package(6, 8f, 5f, 3f, "Rochester"));
            packages.insert(new Package(7, 16f, 3f, 15f, "RTP"));
            packages.insert(new Package(8, 2f, 15f, 18f, "Rochester"));
        // TODO: Add more examples
        }
    }
}
