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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.AssertTrue;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
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
    public void testGetQueries() throws Exception {
        Response response = client.target(URL).request().get();
        assertEquals(200, response.getStatus(), "Incorrect response code from: " + URL);
        String jsonReponse = response.readEntity(String.class);
        JsonArray json = Json.createReader(new StringReader(jsonReponse)).readArray();
        // System.out.println(json);
        JsonObject findByLengthGreaterThan = Json.createObjectBuilder()
                .add("name", "findByLengthGreaterThan")
                .add("parameters", Json.createArrayBuilder().add("length").build())
                .add("types", Json.createArrayBuilder().add("float").build()).build();

        JsonObject findByLengthGreaterThanAndWidthLessThan = Json.createObjectBuilder()
                .add("name", "findByLengthGreaterThanAndWidthLessThan")
                .add("parameters",
                        Json.createArrayBuilder().add("length").add("width").build())
                .add("types",
                        Json.createArrayBuilder().add("float").add("float").build())
                .build();

        assertTrue(json.contains(findByLengthGreaterThan), json.toString());
        assertTrue(json.contains(findByLengthGreaterThanAndWidthLessThan),
                json.toString());
    }

}
