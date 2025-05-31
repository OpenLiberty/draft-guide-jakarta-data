// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;


public class DataGuideIT {

    private static final String PORT = System.getProperty("http.port");
    private static final String URL = "http://localhost:" + PORT 
                                        + "/shipping/packageQuery";

    private Client client;

    @BeforeEach
    public void beforeEach() {
      client = ClientBuilder.newClient();
    }

        @AfterEach
    public void afterEach() {
      client.close();
    }


    @Test
    public void testQueries() throws Exception {
        Response response = client.target(URL).request().get();
        assertEquals(200, response.getStatus(),
            "Incorrect response code from: " + URL);

    }

}
