/*******************************************************************************
* Copyright (c) 2025 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package it.io.openliberty.guides.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;


public class DataGuideIT {

    private static final String PORT = System.getProperty("http.port");
    private static final String URL = "http://localhost:" + PORT + "/shipping/packageQuery";

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
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URL);
        Response response = target.request().get();
        assertEquals(200, response.getStatus(),
            "Incorrect response code from " + target.getUri().getPath());

    }

    
}
