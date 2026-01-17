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

/**
 * Initializes the database with sample Package data at application startup.
 * This CDI bean observes the Startup event and populates the database with
 * sample shipping packages if the database is empty. This ensures the
 * application has demo data available for testing Jakarta Data queries.
 */
@ApplicationScoped
public class PackageDataInitializer {

    @Inject
    Packages packages;

    /**
     * Initializes sample package data when the application starts.
     * Only populates data if the database is empty to avoid duplicates
     * during Liberty dev mode restarts.
     *
     * @param event the CDI Startup event
     */
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
            packages.insert(new Package(9, 5f, 22f, 10f, "Austin"));
            packages.insert(new Package(10, 24f, 10f, 8f, "Markham"));
            packages.insert(new Package(11, 5f, 12f, 14f, "RTP"));
            packages.insert(new Package(12, 24f, 14f, 8f, "Rochester"));
            packages.insert(new Package(13, 8f, 10f, 4f, "Austin"));
            packages.insert(new Package(14, 25f, 5f, 9f, "Hursley"));
            packages.insert(new Package(15, 15f, 8f, 15f, "RTP"));
            packages.insert(new Package(16, 38f, 16f, 25f, "Markham"));
            packages.insert(new Package(17, 7f, 7f, 7f, "Hursley"));
            packages.insert(new Package(18, 12f, 25f, 8f, "Rochester"));
            packages.insert(new Package(19, 16f, 5f, 3f, "RTP"));
            packages.insert(new Package(20, 4f, 15f, 16f, "Hursley"));
        }
    }
}
